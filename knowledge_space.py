
class KnowledgeSpaceCollection:
    def __int__(self, user_name: str, knowledge_space_collection_name: str, knowledge_space_names: [str]):
        self.user_name = user_name
        self.knowledge_space_collection_name = knowledge_space_collection_name
        self.knowledge_space_names = knowledge_space_names

    def __eq__(self, other):
        if type(other) == KnowledgeSpace:
            return other.user_name == self.user_name and other.knowledge_space_collection_name == self.knowledge_space_collection_name and other.knowledge_space_names == self.knowledge_space_names
        return False

    def to_dict(self):
        return {
            "user_name": self.user_name,
            "knowledge_space_collection_name": self.knowledge_space_collection_name,
            "knowledge_space_names": self.knowledge_space_names
        }

class KnowledgeSpace:

    def __init__(self, user_name: str, knowledge_space_name: str, index_dict: str):
        self.knowledge_space_name = knowledge_space_name
        self.user_name = user_name
        self.index_string = index_dict

    def __eq__(self, other):
        if type(other) == KnowledgeSpace:
            return other.user_name == self.user_name and other.index_string == self.index_string and other.knowledge_space_name == self.knowledge_space_name
        return False

    def to_dict(self):
        return {
            "user_name": self.user_name,
            "knowledge_space_name": self.knowledge_space_name,
            "index_dict": self.index_string
        }