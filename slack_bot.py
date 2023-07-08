import ast
import json
import os
import re
import ssl

import certifi
from llama_index import VectorStoreIndex, LLMPredictor, StorageContext, load_index_from_storage, ServiceContext
from slack_bolt import App
from slack_sdk import WebClient

from compose_graph import compose_graph_hf, compose_graph_openai
from construct_index import IndexMaker, get_model_config_from_env, get_local_llm_from_huggingface, get_openai_api_llm, \
    get_prompt_helper
from database_utils import DatabaseConfig, get_db_from_config, save_index, get_index, save_index_to_knowledge_space
from knowledge_space import KnowledgeSpace

# Required to authenticate into slack
ssl_context = ssl.create_default_context(cafile=certifi.where())


# filter_channels takes a list of plaintext slack channel names and filters the slack channel api result for only
# those channel names, returning their ids
def filter_channels(list_channels, channel_names):
    list_ids = []
    for channel in list_channels:
        if channel["is_member"] and channel["name"] in channel_names:
            list_ids.append(channel["id"])
    return list_ids


# get_app constructs the slack bot, required environment variables is SLACK_BOT_TOKEN
# If you want to use OpenAPI models please set further env variables: LOCAL=False, OPENAI_API_TOKEN=<your-token>
def get_app():
    # Setting up slack application with custom ssl context and the bot token from our env
    client = WebClient(token=os.environ["SLACK_BOT_TOKEN"], ssl=ssl_context)
    app = App(client=client)

    # Getting our app config from the environment
    model_config = get_model_config_from_env()

    # Getting out database config from environment
    db_config = DatabaseConfig.get_database_config_from_env()

    # Getting db from db config
    db = get_db_from_config(db_config)

    # Getting knowledgespace collection
    knowledge_space_collection = db.get_collection("knowledge_space")

    # index_workspace indexes every channel that the slackbot is a part of
    @app.message("gpt index workspace")
    def index_workspace(message, say):
        list_channels = app.client.conversations_list().get("channels")
        list_ids = []
        for channel in list_channels:
            if channel["is_member"]:
                list_ids.append(channel["id"])
        index = IndexMaker.get_hf_index_from_slack(list_ids) if model_config.local else IndexMaker.get_index_from_slack(
            list_ids)
        knowledge_space_name = "workspace_knowledge_space_bot"
        save_index_to_knowledge_space(index, knowledge_space_name, knowledge_space_collection, "gpt_bot")
        say("Workspace has been indexed: use query command to query it")

    # index_channels_to_my_knowledge_space indexes the given channels to the specified users knowledge space
    @app.message("gpt index knowledge_space=")
    def index_channels_to_knowledge_space(message, say):
        user = message["user"]
        text = message["text"]
        regex = "gpt index knowledge_space=(.*) channels (.*)"
        result = re.search(regex, text)
        knowledge_space_to_save = result.group(1)
        parts = result.group(2)
        channels_to_keep = parts.split(" ")
        list_channels = app.client.conversations_list().get("channels")
        list_ids = filter_channels(list_channels, channels_to_keep)
        index = IndexMaker.get_hf_index_from_slack(list_ids) if model_config.local else IndexMaker.get_index_from_slack(
            list_ids)
        save_index_to_knowledge_space(index, knowledge_space_to_save, knowledge_space_collection, user)
        say("Channels indexed to the users " + knowledge_space_to_save)

    # gpt_query runs a natural language query on the index that has been constructed so far for the user
    @app.message("gpt query knowledge_space=")
    def gpt_query_knowledge_space(message, say):
        text = message["text"]
        regex = "gpt query knowledge_space=(.*) (.*)"
        result = re.search(regex, text)
        knowledge_space_to_save = result.group(1)
        knowledge_space = get_index(knowledge_space_collection, message["user"], knowledge_space_to_save)
        try:
            index = local_knowledge_space_model(
                knowledge_space) if model_config.local else open_ai_knowledge_space_model(knowledge_space)
        except:
            index = local_workspace_model() if model_config.local else open_ai_workspace_model()
        response = index.query(result.group(2))
        say(response.response)

    # gpt_query runs a natural language query on the index that has been constructed so far
    @app.message("gpt query")
    def gpt_query(message, say):
        split_on_query = str(message["text"]).split("gpt query")
        actual_query = split_on_query[1]
        knowledge_space_name = "workspace_knowledge_space_bot"
        knowledge_space = get_index(knowledge_space_collection, "gpt_bot", knowledge_space_name)
        try:
            index = local_knowledge_space_model(
                knowledge_space) if model_config.local else open_ai_knowledge_space_model(knowledge_space)
        except:
            index = local_workspace_model() if model_config.local else open_ai_workspace_model()
        response = index.query(actual_query)
        say(response.response)

    @app.message("gpt compose")
    def composed_query(message, say):
        text = message["text"]
        regex = "gpt compose knowledge_spaces=\[(.*)\] query (.*)"
        result = re.search(regex, text)
        knowledge_spaces_to_save_str = result.group(1)
        knowledge_spaces_to_save = knowledge_spaces_to_save_str.split(",")
        indices = []
        summaries = []
        for knowledge_space_to_save in knowledge_spaces_to_save:
            knowledge_space = get_index(knowledge_space_collection, message["user"], knowledge_space_to_save)
            try:
                index, _ = full_index_local_knowledge_space_model(
                    knowledge_space) if model_config.local else full_index_open_ai_knowledge_space_model(knowledge_space)
            except:
                index = local_workspace_model() if model_config.local else open_ai_workspace_model()
            indices.append(index)
            summaries.append("Some information from a slack workspace")
        graph_query_engine = compose_graph_hf(indices, summaries) if model_config.local else compose_graph_openai(indices, summaries)
        response = graph_query_engine.query(result.group(2))
        say(response.response)

    def local_knowledge_space_model(knowledge_space: KnowledgeSpace):
        index, service_context = full_index_local_knowledge_space_model(knowledge_space)
        return index.as_query_engine(service_context=service_context)

    def full_index_local_knowledge_space_model(knowledge_space):
        model = get_local_llm_from_huggingface(model_config)
        literal_eval = json.loads(knowledge_space.index_string)
        storage_context = StorageContext.from_dict(literal_eval)
        service_context = ServiceContext.from_defaults(llm_predictor=LLMPredictor(llm=model),
                                                       embed_model=IndexMaker.get_hf_embeddings())
        index = load_index_from_storage(storage_context, service_context=service_context)
        return index, service_context

    def open_ai_knowledge_space_model(knowledge_space: KnowledgeSpace):
        index, service_context = full_index_open_ai_knowledge_space_model(knowledge_space)
        return index.as_query_engine(service_context=service_context)

    def full_index_open_ai_knowledge_space_model(knowledge_space):
        model = get_openai_api_llm(model_config)
        storage_context = StorageContext.from_dict(json.loads(knowledge_space.index_string))
        service_context = ServiceContext.from_defaults(llm_predictor=LLMPredictor(llm=model))
        index = load_index_from_storage(storage_context, service_context=service_context)
        return index, service_context

    # local_workspace_model gets a local LLM for the user (if LOCAL=True or not set in env variables)
    def local_workspace_model():
        model = get_local_llm_from_huggingface(model_config)
        storage_context = StorageContext.from_defaults(persist_dir="./storage")
        service_context = ServiceContext.from_defaults(llm_predictor=LLMPredictor(llm=model),
                                                       embed_model=IndexMaker.get_hf_embeddings())
        index = load_index_from_storage(storage_context, service_context=service_context)
        return index.as_query_engine(service_context=service_context)

    # open_ai_workspace_model gets an OpenAi API LLM for the user (if LOCAL=False in env variables)
    def open_ai_workspace_model():
        model = get_openai_api_llm(model_config)
        storage_context = StorageContext.from_defaults(persist_dir="./storage")
        service_context = ServiceContext.from_defaults(llm_predictor=LLMPredictor(llm=model))
        index = load_index_from_storage(storage_context, service_context=service_context)
        return index.as_query_engine(service_context=service_context)

    return app
