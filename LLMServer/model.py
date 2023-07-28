from enum import Enum


class Datatype(Enum):
    PDF_DOCUMENT = 1
    WEB_LINK = 2
    YOUTUBE_LINK = 3
    PLAIN_TEXT = 4

class BlobReference():

    def __init__(self, blobId: str, type: Datatype, fileName: str):
        self.blobId = blobId
        self.type = type
        self.fileName = fileName

class UserDetails():

    def __init__(self, email: str):
        self.email = email

class IndexRequest():

    def __init__(self, userDetails: UserDetails, knowledgeFileTarget: str, blobReferences: [BlobReference]):
        self.userDetails = userDetails
        self.knowledgeFileTarget = knowledgeFileTarget
        self.blobReferences = blobReferences