import os

from gpt_index import GPTSimpleVectorIndex, LLMPredictor, PromptHelper, Document, \
    StringIterableReader, SlackReader, LangchainEmbedding
from langchain import HuggingFaceHub, HuggingFacePipeline
from langchain.chat_models import ChatOpenAI
from langchain.embeddings import HuggingFaceEmbeddings
from langchain.llms.base import LLM


class ModelConfig:
    def __init__(self, max_input_size, num_outputs, max_chunk_overlap, chunk_size_limit, temperature, model_name):
        self.max_input_size = max_input_size
        self.num_outputs = num_outputs
        self.max_chunk_overlap = max_chunk_overlap
        self.chunk_size_limit = chunk_size_limit
        self.temperature = temperature
        self.model_name = model_name

    def __eq__(self, o: object) -> bool:
        if isinstance(o, ModelConfig):
            return self.chunk_size_limit == o.chunk_size_limit and self.max_chunk_overlap == o.max_chunk_overlap and self.num_outputs == o.num_outputs and self.max_input_size == o.max_input_size and self.temperature == o.temperature and self.model_name == o.model_name
        return False


def get_model_config_from_env() -> ModelConfig:
    max_input_size_str = os.getenv("MAX_INPUT_SIZE") if "MAX_INPUT_SIZE" in os.environ else "2048"
    num_outputs_str = os.getenv("NUM_OUTPUTS") if "NUM_OUTPUTS" in os.environ else "512"
    max_chunk_overlap_str = os.getenv("MAX_CHUNK_OVERLAP") if "MAX_CHUNK_OVERLAP" in os.environ else "28"
    chunk_size_limit_str = os.getenv("CHUNK_SIZE_LIMIT") if "CHUNK_SIZE_LIMIT" in os.environ else "300"
    temperature_str = os.getenv("TEMPERATURE") if "TEMPERATURE" in os.environ else "0.6"
    model_name = os.getenv("MODEL_NAME") if "MODEL_NAME" in os.environ else "gpt-3.5-turbo"

    return ModelConfig(int(max_input_size_str), int(num_outputs_str), int(max_chunk_overlap_str),
                       int(chunk_size_limit_str), float(temperature_str), model_name)


def get_prompt_helper(model_restrictions: ModelConfig) -> PromptHelper:
    return PromptHelper(model_restrictions.max_input_size, model_restrictions.num_outputs,
                        model_restrictions.max_chunk_overlap, chunk_size_limit=model_restrictions.chunk_size_limit)


def get_vector_index(documents: list[Document], llm: LLM, model_config: ModelConfig, embeddings=None) -> GPTSimpleVectorIndex:
    predictor = LLMPredictor(llm=llm)
    prompt_helper = get_prompt_helper(model_config)
    if embeddings == None:
        return GPTSimpleVectorIndex(documents, llm_predictor=predictor, prompt_helper=prompt_helper)
    else:
        return GPTSimpleVectorIndex(documents, llm_predictor=predictor, prompt_helper=prompt_helper, embed_model=LangchainEmbedding(embeddings))


def get_llm(model_config):
    return ChatOpenAI(temperature=model_config.temperature, model_name=model_config.model_name,
                      max_tokens=model_config.num_outputs)

def get_hf_llm(model_config):
    return HuggingFaceHub(repo_id="declare-lab/flan-alpaca-base", model_kwargs={"temperature":1e-10})

def get_hf_llm_2(model_config):
    return HuggingFacePipeline.from_model_id(
        model_id="declare-lab/flan-alpaca-base", task="text2text-generation"
    )


class IndexMaker:

    @staticmethod
    def get_index_from_text(list_of_text: list[str]):
        documents = StringIterableReader().load_data(list_of_text)
        model_config = get_model_config_from_env()
        return get_vector_index(documents, get_llm(model_config), model_config)

    @staticmethod
    def get_index_from_slack(channel_ids: list[str]):
        documents = SlackReader().load_data(channel_ids)
        model_config = get_model_config_from_env()
        return get_vector_index(documents, get_llm(model_config), model_config)

    @staticmethod
    def get_hf_index_from_text(list_of_text: list[str]):
        documents = StringIterableReader().load_data(list_of_text)
        model_config = get_model_config_from_env()
        model_name = "sentence-transformers/all-mpnet-base-v2"
        model_kwargs = {'device': 'cpu'}
        hf = HuggingFaceEmbeddings(model_name=model_name, model_kwargs=model_kwargs)
        return get_vector_index(documents, get_hf_llm_2(model_config), model_config, hf)