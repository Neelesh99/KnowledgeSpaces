import json
import unittest
from unittest.mock import Mock

from compose_graph import compose_graph_hf, compose_graph_from_knowledge_space_collection
from construct_index import IndexMaker, get_model_config_from_env
from knowledge_space import KnowledgeSpace, KnowledgeSpaceCollection


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

    def test_will_compose_knowledge_spaces_into_graph(self):
        info_index_1 = ["Alan is a dog"]
        info_index_2 = ["All dogs have a ball"]

        index_1 = IndexMaker.get_hf_index_from_text(info_index_1)
        index_2 = IndexMaker.get_hf_index_from_text(info_index_2)
        index_1_str = json.dumps(index_1.storage_context.to_dict())
        index_2_str = json.dumps(index_2.storage_context.to_dict())
        knowledge_space_1 = KnowledgeSpace("some_user", "first_space", index_1_str)
        knowledge_space_2 = KnowledgeSpace("some_user", "second_space", index_2_str)

        knowledge_space_collection = KnowledgeSpaceCollection(
            "some_user",
            "some_knowledge_space_collection_name",
            ["first_space", "second_space"]
        )

        collection = Mock()
        m = Mock()
        collection.find_one = m
        m.side_effect = [knowledge_space_1.to_dict(), knowledge_space_2.to_dict()]

        result = compose_graph_from_knowledge_space_collection(
            get_model_config_from_env(),
            knowledge_space_collection,
            collection
        )

        response = result.query("Does Alan have a ball?")
        text_response = str(response.response).lower()
        self.assertTrue(text_response.find("yes") != -1)

if __name__ == '__main__':
    unittest.main()
