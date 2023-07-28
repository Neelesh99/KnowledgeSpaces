
class KnowledgeSpace:
    def __init__(self, id: str, name: str, files: [str], email: str):
        self.id = id
        self.name = name
        self.files = files
        self.email = email

    def __eq__(self, other):
        if type(other) == KnowledgeFile:
            return other.id == self.id and other.name == self.name and other.files == self.files and self.email == other.email
        return False

    def to_dict(self):
        return {
            "id": self.id,
            "name": self.name,
            "files": self.files,
            "email": self.email
        }

class KnowledgeFile:

    def __init__(self, id: str, email: str, name: str, blobIds: [str], indexDict: str):
        self.id = id
        self.email = email
        self.name = name
        self.blobIds = blobIds
        self.indexDict = indexDict

    def __eq__(self, other):
        if type(other) == KnowledgeFile:
            return other.id == self.email and other.indexDict == self.indexDict and other.name == self.name
        return False

    def to_dict(self):
        return {
            "id": self.id,
            "email": self.email,
            "name": self.name,
            "blobIds": self.blobIds,
            "indexDict": self.indexDict,
        }