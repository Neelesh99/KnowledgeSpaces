import json

from fastapi import FastAPI
from llama_index import StorageContext, ServiceContext, load_index_from_storage, LLMPredictor
from pydantic import BaseModel

from compose_graph import compose_graph_from_knowledge_space_collection
from construct_index import get_model_config_from_env, get_local_llm_from_huggingface, IndexMaker, get_openai_api_llm
from database_utils import DatabaseConfig, get_db_from_config, get_index, save_index_to_knowledge_space, \
    save_knowledge_space_collection, get_knowledge_space_collection
from knowledge_space import KnowledgeFile, KnowledgeSpace
from packaged_index_utilities import local_knowledge_space_model, model_config, open_ai_knowledge_space_model, \
    local_workspace_model, open_ai_workspace_model

app = FastAPI()

# Getting out database config from environment
db_config = DatabaseConfig.get_database_config_from_env()

# Getting db from db config
db = get_db_from_config(db_config)

# Getting knowledgespace collection
knowledge_space_collection = db.get_collection("knowledge_space")

knowledge_collection_collection = db.get_collection("knowledge_collection")

class Query(BaseModel):
    query: str

class TextIndex(BaseModel):
    text: str

class KnowledgeCollectionUpdate(BaseModel):
    knowledge_spaces: list[str]

@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.post("/query/knowledge_space/{user}/{knowledge_space}")
async def query_knowledge_space(user: str, knowledge_space: str, query: Query):
    print("Hello")
    knowledge_space = get_index(knowledge_space_collection, user, knowledge_space)
    try:
        index = local_knowledge_space_model(
            knowledge_space) if model_config.local else open_ai_knowledge_space_model(knowledge_space)
    except:
        index = local_workspace_model() if model_config.local else open_ai_workspace_model()
    response = index.query(query.query)
    return {"response": response.response}

@app.post("/index/text/{user}/{knowledge_space}")
async def save_knowledge_space(user: str, knowledge_space: str, text: TextIndex):
    index = IndexMaker.get_hf_index_from_text(text.text) if model_config.local else IndexMaker.get_index_from_text(
        text.text)
    save_index_to_knowledge_space(index, knowledge_space, knowledge_space_collection, user)
    return "Indexed"

@app.post("/collection/compile/{user}/{knowledge_collection_name}")
async def compile_knowledge_collection(user: str, knowledge_collection_name: str, knowledge_collection_update: KnowledgeCollectionUpdate):
    collection = KnowledgeSpace(user, knowledge_collection_name, knowledge_collection_update.knowledge_spaces)
    save_knowledge_space_collection(knowledge_collection_collection, collection)
    return "Collection Saved"

@app.post("/collection/query/{user}/{knowledge_collection_name}")
async def query_knowledge_collection(user: str, knowledge_collection_name: str, knowledge_query: Query):
    knowledge_collection = get_knowledge_space_collection(knowledge_collection_collection, user, knowledge_collection_name)
    graph = compose_graph_from_knowledge_space_collection(model_config, knowledge_collection, knowledge_space_collection)
    response = graph.query(knowledge_query.query)
    return {"response": response.response}

