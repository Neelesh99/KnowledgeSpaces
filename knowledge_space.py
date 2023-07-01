

class KnowledgeSpace:

    def __init__(self, user_name: str, index_dict: dict):
        self.user_name = user_name
        self.index_dict = index_dict

    def __eq__(self, other):
        if type(other) == KnowledgeSpace:
            return other.user_name == self.user_name and other.index_dict == self.index_dict
        return False
