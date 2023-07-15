import json
import unittest

import pytest
import uvicorn
import requests

from non_blocking_server import Server


class MyTestCase(unittest.TestCase):
    @pytest.mark.api
    def test_something(self):
        config = uvicorn.Config("testing_fast_api:app", host="127.0.0.1", port=5000, log_level="info")
        server = Server(config=config)
        with server.run_in_thread():
            response = requests.get(
                "http://127.0.0.1:5000"
            )
            self.assertEqual("Hello World", response.json()["message"])

    @pytest.mark.api
    def test_will_store_data_into_knowledge_space(self):
        config = uvicorn.Config("testing_fast_api:app", host="127.0.0.1", port=5000, log_level="info")
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
        config = uvicorn.Config("testing_fast_api:app", host="127.0.0.1", port=5000, log_level="info")
        server = Server(config=config)
        user = "test_user"
        with server.run_in_thread():
            response = requests.post(
                "http://127.0.0.1:5000/query/knowledge_space/" + user + "/car_knowledge_space",
                json.dumps({"query": "What is the Cadillac Flyboy?"})
            )
            print(response.text)
            self.assertTrue(response.text.find("The Cadillac Flyboy"))


if __name__ == '__main__':
    unittest.main()
