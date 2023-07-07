import json
import os

from llama_index import VectorStoreIndex
from pymongo import MongoClient
from pymongo.collection import Collection
from pymongo.database import Database

from knowledge_space import KnowledgeSpace


class DatabaseConfig:

    def __init__(self, connection_string: str, db_name: str):
        self.connection_string = connection_string
        self.db_name = db_name

    def __eq__(self, other):
        if type(other) == DatabaseConfig:
            return other.connection_string == self.connection_string and other.db_name == self.db_name
        return False

    @staticmethod
    def get_database_config_from_env():
        connection_string_template = os.getenv("MONGO_CONNECTION_STRING_TEMPLATE")
        username = os.getenv("MONGO_USERNAME")
        password = os.getenv("MONGO_PASSWORD")
        db_name = os.getenv("DB_NAME")
        username_replaced = connection_string_template.replace("<username>", username)
        full_string = username_replaced.replace("<password>", password)
        return DatabaseConfig(full_string, db_name)


def get_db_from_config(config: DatabaseConfig) -> Database:
    client = MongoClient(config.connection_string)
    return client[config.db_name]

def save_index(index: VectorStoreIndex, knowledge_collection: Collection, user_name: str):
    save_index_to_knowledge_space(index, "my_knowledge_space", knowledge_collection, user_name)

def save_index_to_knowledge_space(index: VectorStoreIndex, knowledge_space_name: str, knowledge_collection: Collection, user_name: str):
    knowledge_space = KnowledgeSpace(user_name, knowledge_space_name, json.dumps(index.storage_context.to_dict()))
    knowledge_collection.replace_one({"user_name": user_name}, knowledge_space.to_dict(), upsert=True)

def get_index(knowledge_collection: Collection, user_name: str, knowledge_space: str):
    result = knowledge_collection.find_one({"user_name": user_name})
    return KnowledgeSpace(result["user_name"], knowledge_space, result["index_dict"])

def slotted_to_dict(obj):
    return {s: getattr(obj, s) for s in obj.__slots__ if hasattr(obj, s)}