import os
import unittest
from unittest import mock
from unittest.mock import MagicMock, Mock

from pymongo.results import InsertOneResult

from database_utils import DatabaseConfig, get_db_from_config, save_index, slotted_to_dict, get_index
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
        collection.replace_one = MagicMock(return_value=InsertOneResult("some_id", True))
        index = Mock()
        index.save_to_string = MagicMock(return_value="{}")
        expected_knowledge_space = KnowledgeSpace("some_user", "my_knowledge_space", "{}")
        save_index(index, collection, "some_user")
        collection.replace_one.assert_called_with({"user_name": "some_user"}, expected_knowledge_space.to_dict(), upsert=True)

    def test_get_index_will_get_KnowledgeSpace_for_user(self):
        collection = Mock()
        space = KnowledgeSpace("some_user", "my_knowledge_space", "{}")
        collection.find_one = MagicMock(return_value=space.to_dict())
        actual_space = get_index(collection, "some_user", "my_knowledge_space")
        self.assertEqual(space, actual_space)