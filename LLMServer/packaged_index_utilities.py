import json

from llama_index import StorageContext, ServiceContext, LLMPredictor, load_index_from_storage

from construct_index import get_local_llm_from_huggingface, get_model_config_from_env, IndexMaker, get_openai_api_llm
from database_utils import DatabaseConfig, get_db_from_config
from knowledge_space import KnowledgeFile

# Getting our app config from the environment
model_config = get_model_config_from_env()


def local_knowledge_space_model(knowledge_space: KnowledgeFile):
    index, service_context = full_index_local_knowledge_space_model(knowledge_space)
    return index.as_query_engine(service_context=service_context)


def full_index_local_knowledge_space_model(knowledge_space):
    model = get_local_llm_from_huggingface(model_config)
    literal_eval = json.loads(knowledge_space.indexDict)
    storage_context = StorageContext.from_dict(literal_eval)
    service_context = ServiceContext.from_defaults(llm_predictor=LLMPredictor(llm=model),
                                                   embed_model=IndexMaker.get_hf_embeddings())
    index = load_index_from_storage(storage_context, service_context=service_context)
    return index, service_context


def open_ai_knowledge_space_model(knowledge_space: KnowledgeFile):
    index, service_context = full_index_open_ai_knowledge_space_model(knowledge_space)
    return index.as_query_engine(service_context=service_context)


def full_index_open_ai_knowledge_space_model(knowledge_space):
    model = get_openai_api_llm(model_config)
    storage_context = StorageContext.from_dict(json.loads(knowledge_space.indexDict))
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