import os
import unittest
from unittest import mock

from database_utils import DatabaseConfig


class DatabaseUtilsTestCase(unittest.TestCase):

    @mock.patch.dict(os.environ, {
        "MONGO_CONNECTION_STRING_TEMPLATE": "mongodb+srv://<username>:<password>@cluster0.pxqerb3.mongodb.net/?retryWrites=true&w=majority",
        "MONGO_USERNAME": "userName",
        "MONGO_PASSWORD": "somePassword"
    })
    def test_will_get_database_config_from_env(self):
        database_config = DatabaseConfig.get_database_config_from_env()
        expected_config = DatabaseConfig("mongodb+srv://userName:somePassword@cluster0.pxqerb3.mongodb.net/?retryWrites=true&w=majority")
        self.assertEqual(expected_config, database_config)