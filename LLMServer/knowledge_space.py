
class KnowledgeSpace:
    def __init__(self, id: str, name: str, files: [str]):
        self.id = id
        self.name = name
        self.files = files

    def __eq__(self, other):
        if type(other) == KnowledgeFile:
            return other.id == self.id and other.name == self.name and other.files == self.files
        return False

    def to_dict(self):
        return {
            "id": self.id,
            "name": self.name,
            "files": self.files
        }

class KnowledgeFile:

    def __init__(self, email: str, name: str, indexDict: str):
        self.name = name
        self.email = email
        self.indexDict = indexDict

    def __eq__(self, other):
        if type(other) == KnowledgeFile:
            return other.id == self.email and other.indexDict == self.indexDict and other.name == self.name
        return False

    def to_dict(self):
        return {
            "email": self.email,
            "name": self.name,
            "indexDict": self.indexDict
        }