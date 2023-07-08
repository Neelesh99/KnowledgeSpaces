from llama_index import VectorStoreIndex, LLMPredictor, ServiceContext
from llama_index.indices.composability import ComposableGraph

from construct_index import IndexMaker, get_local_llm_from_huggingface, get_model_config_from_env


def compose_graph(indices: [VectorStoreIndex], summaries: [str]):
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