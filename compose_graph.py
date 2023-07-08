from llama_index import VectorStoreIndex, LLMPredictor, ServiceContext
from llama_index.indices.composability import ComposableGraph

from construct_index import IndexMaker, get_local_llm_from_huggingface, get_model_config_from_env, get_openai_api_llm


def compose_graph_hf(indices: [VectorStoreIndex], summaries: [str]):
    hf_embedding = IndexMaker.get_hf_embeddings()
    model_config = get_model_config_from_env()
    llm = get_local_llm_from_huggingface(model_config)
    predictor = LLMPredictor(llm=llm)
    service_context = ServiceContext.from_defaults(llm_predictor=predictor, embed_model=hf_embedding)
    graph = ComposableGraph.from_indices(
        VectorStoreIndex,
        indices,
        index_summaries=summaries,
        service_context=service_context
    )
    return graph.as_query_engine()

def compose_graph_openai(indices: [VectorStoreIndex], summaries: [str]):
    model_config = get_model_config_from_env()
    llm = get_openai_api_llm(model_config)
    predictor = LLMPredictor(llm=llm)
    service_context = ServiceContext.from_defaults(llm_predictor=predictor)
    graph = ComposableGraph.from_indices(
        VectorStoreIndex,
        indices,
        index_summaries=summaries,
        service_context=service_context
    )
    return graph.as_query_engine()
