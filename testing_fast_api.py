import json

from fastapi import FastAPI
from llama_index import StorageContext, ServiceContext, load_index_from_storage, LLMPredictor
from pydantic import BaseModel

from construct_index import get_model_config_from_env, get_local_llm_from_huggingface, IndexMaker, get_openai_api_llm
from database_utils import DatabaseConfig, get_db_from_config, get_index, save_index_to_knowledge_space
from knowledge_space import KnowledgeSpace
from packaged_index_utilities import local_knowledge_space_model, model_config, open_ai_knowledge_space_model, \
    local_workspace_model, open_ai_workspace_model

app = FastAPI()

# Getting out database config from environment
db_config = DatabaseConfig.get_database_config_from_env()

# Getting db from db config
db = get_db_from_config(db_config)

# Getting knowledgespace collection
knowledge_space_collection = db.get_collection("knowledge_space")

class Query(BaseModel):
    query: str

class TextIndex(BaseModel):
    text: str

@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.post("/query/knowledge_space/{knowledge_space}")
async def query_knowledge_space(knowledge_space: str, query: Query):
    knowledge_space = get_index(knowledge_space_collection, "test_server", knowledge_space)
    try:
        index = local_knowledge_space_model(
            knowledge_space) if model_config.local else open_ai_knowledge_space_model(knowledge_space)
    except:
        index = local_workspace_model() if model_config.local else open_ai_workspace_model()
    response = index.query(query.query)
    return {"response": response.response}

@app.post("/index/text/{user}/{knowledge_space}")
async def query_knowledge_space(user: str, knowledge_space: str, text: TextIndex):
    index = IndexMaker.get_hf_index_from_text(text.text) if model_config.local else IndexMaker.get_index_from_text(
        text.text)
    save_index_to_knowledge_space(index, knowledge_space, knowledge_space_collection, user)
    return "Indexed"



