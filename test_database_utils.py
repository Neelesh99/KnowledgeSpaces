import os
import unittest
from unittest import mock
from unittest.mock import MagicMock, Mock

from gpt_index import GPTSimpleVectorIndex
from pymongo.collection import Collection
from pymongo.results import InsertOneResult

from database_utils import DatabaseConfig, get_db_from_config, save_index
from knowledge_space import KnowledgeSpace


class DatabaseUtilsTestCase(unittest.TestCase):

    @mock.patch.dict(os.environ, {
        "MONGO_CONNECTION_STRING_TEMPLATE": "mongodb+srv://<username>:<password>@cluster0.pxqerb3.mongodb.net/?retryWrites=true&w=majority",
        "MONGO_USERNAME": "userName",
        "MONGO_PASSWORD": "somePassword",
        "DB_NAME": "someDBName"
    })
    def test_will_get_database_config_from_env(self):
        database_config = DatabaseConfig.get_database_config_from_env()
        expected_config = DatabaseConfig("mongodb+srv://userName:somePassword@cluster0.pxqerb3.mongodb.net/?retryWrites=true&w=majority", "someDBName")
        self.assertEqual(expected_config, database_config)

    def test_will_get_database_client_from_config(self):
        config = DatabaseConfig(
            "mongodb+srv://userName:somePassword@cluster0.pxqerb3.mongodb.net/?retryWrites=true&w=majority", "someDBName")
        db = get_db_from_config(config)
        self.assertEqual(db.name, "someDBName")

    def test_save_index_will_construct_and_save_knowledge(self):
        collection = Mock()
        collection.insert_one = MagicMock(return_value=InsertOneResult("some_id", True))
        index = Mock()
        index.save_to_dict = MagicMock(return_value={})
        expected_knowledge_space = KnowledgeSpace("some_user", {})
        save_index(index, collection, "some_user")
        collection.insert_one.assert_called_with(expected_knowledge_space)