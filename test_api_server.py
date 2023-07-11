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


if __name__ == '__main__':
    unittest.main()
