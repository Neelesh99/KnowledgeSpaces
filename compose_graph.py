from llama_index import VectorStoreIndex, LLMPredictor, ServiceContext
from llama_index.indices.composability import ComposableGraph
from pymongo.collection import Collection

from construct_index import IndexMaker, get_local_llm_from_huggingface, get_model_config_from_env, get_openai_api_llm, \
    ModelConfig
from database_utils import get_index
from knowledge_space import KnowledgeSpaceCollection
from packaged_index_utilities import full_index_local_knowledge_space_model, full_index_open_ai_knowledge_space_model, \
    local_workspace_model, open_ai_workspace_model


def compose_graph_from_knowledge_space_collection(
        model_config: ModelConfig,
        knowledge_space_collection: KnowledgeSpaceCollection,
        knowledge_space_mongo_collection: Collection
):
    knowledge_spaces = []
    for name in knowledge_space_collection.knowledge_space_names:
        knowledge_spaces.append(
            get_index(
                knowledge_space_mongo_collection,
                knowledge_space_collection.user_name,
                name
            ))
    indices = []
    for knowledge_space in knowledge_spaces:
        try:
            index, _ = full_index_local_knowledge_space_model(
                knowledge_space) if model_config.local else full_index_open_ai_knowledge_space_model(knowledge_space)
        except:
            index = local_workspace_model() if model_config.local else open_ai_workspace_model()
        indices.append(index)
    basic_summaries = ["Some information from the user" for i in range(len(indices))]
    return compose_graph_hf(indices, basic_summaries) if model_config.local else compose_graph_openai(indices, basic_summaries)


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
