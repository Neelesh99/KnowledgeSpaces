import os
import ssl

import certifi
from gpt_index import GPTSimpleVectorIndex, LLMPredictor
from slack_bolt import App
from slack_sdk import WebClient

from construct_index import IndexMaker, get_model_config_from_env, get_local_llm_from_huggingface, get_openai_api_llm

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

    # index_channels indexes specific channels given by the user after "gpt index channels " for example "gpt index
    # channels random general" will index random and general
    @app.message("gpt index channels")
    def index_channels(message, say):
        full_message = str(message["text"]).split("gpt index channels ")
        channels_to_keep = full_message[1].split(" ")
        list_channels = app.client.conversations_list().get("channels")
        list_ids = filter_channels(list_channels, channels_to_keep)
        index = IndexMaker.get_hf_index_from_slack(list_ids) if model_config.local else IndexMaker.get_index_from_slack(list_ids)
        index.save_to_disk("workspace_index.json")
        say("Workspace has been indexed: use query command to query it")

    # index_workspace indexes every channel that the slackbot is a part of
    @app.message("gpt index workspace")
    def index_workspace(message, say):

        list_channels = app.client.conversations_list().get("channels")
        list_ids = []
        for channel in list_channels:
            if channel["is_member"]:
                list_ids.append(channel["id"])
        index = IndexMaker.get_hf_index_from_slack(list_ids) if model_config.local else IndexMaker.get_index_from_slack(list_ids)
        index.save_to_disk("workspace_index.json")
        say("Workspace has been indexed: use query command to query it")

    # gpt_query runs a natural language query on the index that has been constructed so far
    @app.message("gpt query")
    def gpt_query(message, say):
        split_on_query = str(message["text"]).split("gpt query")
        actual_query = split_on_query[1]
        index = local_model() if model_config.local else open_ai_model()
        response = index.query(actual_query)
        say(response.response)

    # local_model gets a local LLM for the user (if LOCAL=True or not set in env variables)
    def local_model():
        model = get_local_llm_from_huggingface(model_config)
        index = GPTSimpleVectorIndex.load_from_disk('workspace_index.json', llm_predictor=LLMPredictor(llm=model),
                                                    embed_model=IndexMaker.get_hf_embeddings())
        return index

    # open_ai_model gets an OpenAi API LLM for the user (if LOCAL=False in env variables)
    def open_ai_model():
        model = get_openai_api_llm(model_config)
        index = GPTSimpleVectorIndex.load_from_disk('workspace_index.json', llm_predictor=LLMPredictor(llm=model))
        return index
    return app
