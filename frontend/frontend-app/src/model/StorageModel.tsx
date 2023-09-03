export interface SimpleBlobUploadRequest {
    type: DataType,
    fileName: string,
    dataStream: Blob,
    knowledgeFileTarget: string,
    email: string
}

export interface SimpleKnowledgeFileCreationRequest {
    knowledgeFileName: string,
    email: string
}

export interface SimpleKnowledgeSpaceCreationRequest {
    knowledgeSpaceName: string,
    email: string
}

export interface SimpleKnowledgeFileDeletionRequest {
    knowledgeFileId: string,
    email: string
}

export type DataType = "PDF_DOCUMENT" | "WEB_LINK" | "YOUTUBE_LINK" | "PLAIN_TEXT"