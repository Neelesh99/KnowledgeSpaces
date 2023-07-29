import json
from typing import Annotated

from fastapi import Form, File, UploadFile, Request, FastAPI
from typing import List
from llama_index import StorageContext, ServiceContext, load_index_from_storage, LLMPredictor, Document, \
    StringIterableReader
from pydantic import BaseModel

from index_request_handler import plain_text_handler
from compose_graph import compose_graph_from_knowledge_space_collection
from construct_index import get_model_config_from_env, get_local_llm_from_huggingface, IndexMaker, get_openai_api_llm
from database_utils import DatabaseConfig, get_db_from_config, get_index, save_index_to_knowledge_space, \
    save_knowledge_space_collection, get_knowledge_space_collection, save_index_api
from knowledge_space import KnowledgeFile, KnowledgeSpace
from packaged_index_utilities import local_knowledge_space_model, model_config, open_ai_knowledge_space_model, \
    local_workspace_model, open_ai_workspace_model

app = FastAPI()

# Getting out database config from environment
db_config = DatabaseConfig.get_database_config_from_env()
#
# # Getting db from db config
db = get_db_from_config(db_config)
#
# # Getting knowledgespace collection
knowledge_file_collection = db.get_collection("knowledgeFileCollection")
#
# knowledge_collection_collection = db.get_collection("knowledge_collection")

class Query(BaseModel):
    query: str

class TextIndex(BaseModel):
    text: str

class KnowledgeCollectionUpdate(BaseModel):
    knowledge_spaces: list[str]

@app.get("/")
async def root():
    return {"message": "Hello World"}

@app.post("/api/v1/llm/index")
async def handle_index_request(request: Request):
    async with request.form() as form:
        indexRequestBytes = await form["indexRequest"].read()
        indexRequestMap = json.loads(indexRequestBytes.decode())
        blobReferences = indexRequestMap["blobReferences"]
        documentsForIndex: [Document] = []
        for blobReference in blobReferences:
            if blobReference["type"] == "PLAIN_TEXT":
                data = form[blobReference["fileName"]]
                text = plain_text_handler(await data.read())
                documentsForIndex = documentsForIndex + StringIterableReader().load_data([text])
        index = IndexMaker.get_hf_index_from_docs(documentsForIndex)
        save_index_api(index, indexRequestMap["userDetails"]["email"], indexRequestMap["knowledgeFileTarget"], knowledge_file_collection)
    return {"runId": "someRunId"}

@app.post("/api/v1/llm/knowledgeFile/query")
async def handle_file_query(request: Request):
    async with request.form() as form:
        query = form["query"]
        knowledgeFileBytes = await form["knowledgeFile.json"].read()
        knowledgeFileMap = json.loads(knowledgeFileBytes.decode())
        knowledgeFile = KnowledgeFile(
            knowledgeFileMap["id"],
            knowledgeFileMap["email"],
            knowledgeFileMap["name"],
            knowledgeFileMap["blobIds"],
            knowledgeFileMap["indexDict"]
        )
        model = local_knowledge_space_model(knowledgeFile)
        return model.query(query).response

