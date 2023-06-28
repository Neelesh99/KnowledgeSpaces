import os
import unittest
from unittest import mock
from construct_index import *

class ConstructIndexTest(unittest.TestCase):
    @mock.patch.dict(os.environ, {"MAX_INPUT_SIZE": "4096", "NUM_INPUTS": "512", "MAX_CHUNK_OVERLAP": "20", "CHUNK_SIZE_LIMIT": "600"})
    def test_will_get_model_restrictions_from_env(self):
        construct_index = ConstructIndex()
        model_restrictions = construct_index.get_model_restrictions_from_env()
        expected_model_restrictions = ModelRestrictions(4096, 512, 20, 600)
        self.assertEqual(expected_model_restrictions, model_restrictions)

    def test_will_use_default_restrictions_if_not_available_from_env(self):
        construct_index = ConstructIndex()
        model_restrictions = construct_index.get_model_restrictions_from_env()
        expected_model_restrictions = ModelRestrictions(2048, 512, 28, 300)
        self.assertEqual(expected_model_restrictions, model_restrictions)


if __name__ == '__main__':
    unittest.main()
