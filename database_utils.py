import os

from pymongo import MongoClient
from pymongo.database import Database


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
