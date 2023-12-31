import json
import unittest

import pytest
import uvicorn
import requests

from non_blocking_server import Server


class MyTestCase(unittest.TestCase):

    @pytest.mark.api
    def test_will_store_data_into_knowledge_space(self):
        config = uvicorn.Config("api:app", host="127.0.0.1", port=5000, log_level="info")
        server = Server(config=config)
        user = "test_user"
        with server.run_in_thread():
            response = requests.post(
                "http://127.0.0.1:5000/index/text/" + user + "/car_knowledge_space",
                json.dumps({"text": "The Cadillac Flyboy is a mid sized SUV which is designed to bridge"
                                    "the gap Cadillac's lineup between the Morocco small SUV and the Dennis"
                                    "large SUV."})
            )
            print(response.text)
            self.assertEqual('"Indexed"', response.text)

    @pytest.mark.api
    def test_will_query_knowledge_space(self):
        config = uvicorn.Config("api:app", host="127.0.0.1", port=5000, log_level="info")
        server = Server(config=config)
        user = "test_user"
        with server.run_in_thread():
            response = requests.post(
                "http://127.0.0.1:5000/query/knowledge_space/" + user + "/car_knowledge_space",
                json.dumps({"query": "What is the Cadillac Flyboy?"})
            )
            print(response.text)
            self.assertTrue(response.text.find("The Cadillac Flyboy") != -1)

    @pytest.mark.api
    def test_will_save_knowledge_collection(self):
        config = uvicorn.Config("api:app", host="127.0.0.1", port=5000, log_level="info")
        server = Server(config=config)
        user = "test_user"
        with server.run_in_thread():
            response = requests.post(
                "http://127.0.0.1:5000/collection/compile/" + user + "/car_knowledge_collection",
                json.dumps({"knowledge_spaces": ["car_knowledge_space"]})
            )
            print(response.text)
            self.assertEqual(response.text, '"Collection Saved"')

    @pytest.mark.api
    def test_will_query_knowledge_collection(self):
        config = uvicorn.Config("api:app", host="127.0.0.1", port=5000, log_level="info")
        server = Server(config=config)
        user = "test_user"
        with server.run_in_thread():
            response = requests.post(
                "http://127.0.0.1:5000/collection/query/" + user + "/car_knowledge_collection",
                json.dumps({"query": "What is the Cadillac Flyboy?"})
            )
            print(response.text)
            self.assertTrue(response.text.find("The Cadillac Flyboy") != -1)

if __name__ == '__main__':
    unittest.main()
