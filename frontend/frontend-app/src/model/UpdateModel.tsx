export interface  SimpleKnowledgeFileUpdateRequest {
    knowledgeFileId: string,
    email: string,
    newName: string | undefined,
    newBlobs: string[] | undefined
}

export interface SimpleKnowledgeSpaceUpdateRequest {
    knowledgeSpaceId: string,
    email: string,
    newName: string,
    newFiles: string[]
}