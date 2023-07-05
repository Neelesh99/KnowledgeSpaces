import unittest

from gpt_index import LLMPredictor, GPTSimpleVectorIndex
from gpt_index.composability import ComposableGraph

from construct_index import get_local_llm_from_huggingface, get_model_config_from_env, IndexMaker

class MyTestCase(unittest.TestCase):

    def save_index_for(self, filename: str, content: str):
        index = IndexMaker.get_hf_index_from_text([content])
        index.save_to_disk(filename)

    def get_index_from_file(self, filename, model):
        return GPTSimpleVectorIndex.load_from_disk(filename, llm_predictor=LLMPredictor(llm=model),
                                                    embed_model=IndexMaker.get_hf_embeddings())

    def test_something(self):
        model_config = get_model_config_from_env()
        model = get_local_llm_from_huggingface(model_config)
        first_index_content = "Peter is a dog"
        first_index_filename = "first_index.json"
        second_index_content = "All dogs have bananas"
        second_index_filename = "second_index.json"
        self.save_index_for(first_index_filename, first_index_content)
        self.save_index_for(second_index_filename, second_index_content)

        retrieved_first_index = self.get_index_from_file(first_index_filename, model)
        retrieved_second_index = self.get_index_from_file(second_index_filename, model)

        ComposableGraph.build_from_index(retrieved_first_index)


if __name__ == '__main__':
    unittest.main()
