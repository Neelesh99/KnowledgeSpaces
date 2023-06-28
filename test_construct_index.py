import os
import unittest
from unittest import mock

from gpt_index import PromptHelper

from construct_index import *

class ConstructIndexTest(unittest.TestCase):
    @mock.patch.dict(os.environ, {"MAX_INPUT_SIZE": "4096", "NUM_INPUTS": "512", "MAX_CHUNK_OVERLAP": "20", "CHUNK_SIZE_LIMIT": "600", "TEMPERATURE": "0.7", "MODEL_NAME": "gpt-3.5-turbo"})
    def test_will_get_model_restrictions_from_env(self):
        model_restrictions = get_model_restrictions_from_env()
        expected_model_restrictions = ModelConfig(4096, 512, 20, 600, 0.7, "gpt-3.5-turbo")
        self.assertEqual(expected_model_restrictions, model_restrictions)

    def test_will_use_default_restrictions_if_not_available_from_env(self):
        model_restrictions = get_model_restrictions_from_env()
        expected_model_restrictions = ModelConfig(2048, 512, 28, 300, 0.6, "gpt-3.5-turbo")
        self.assertEqual(expected_model_restrictions, model_restrictions)


if __name__ == '__main__':
    unittest.main()
