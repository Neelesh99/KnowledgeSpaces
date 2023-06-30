import os


class DatabaseConfig:

    def __init__(self, connection_string: str):
        self.connection_string = connection_string

    def __eq__(self, other):
        if type(other) == DatabaseConfig:
            return other.connection_string == self.connection_string
        return False

    @staticmethod
    def get_database_config_from_env():
        connection_string_template = os.getenv("MONGO_CONNECTION_STRING_TEMPLATE")
        username = os.getenv("MONGO_USERNAME")
        password = os.getenv("MONGO_PASSWORD")
        username_replaced = connection_string_template.replace("<username>", username)
        full_string = username_replaced.replace("<password>", password)
        return DatabaseConfig(full_string)
