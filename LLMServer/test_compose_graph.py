import json
import unittest
from unittest.mock import Mock

from compose_graph import compose_graph_hf, compose_graph_from_knowledge_space_collection
from construct_index import IndexMaker, get_model_config_from_env
from knowledge_space import KnowledgeFile, KnowledgeSpace


class ComposeGraphTestCase(unittest.TestCase):
    def test_will_compose_indexes_into_graph(self):
        info_index_1 = ["Alan is a dog"]
        summary_info_1 = "Some information about Alan"
        info_index_2 = ["All dogs have a ball"]
        summary_info_2 = "Some information about dogs"

        index_1 = IndexMaker.get_hf_index_from_text(info_index_1)
        index_2 = IndexMaker.get_hf_index_from_text(info_index_2)

        graph = compose_graph_hf([index_1, index_2], [summary_info_1, summary_info_2])
        response = graph.query("Does Alan have a ball?")
        text_response = str(response.response).lower()
        self.assertTrue(text_response.find("yes") != -1)

if __name__ == '__main__':
    unittest.main()
