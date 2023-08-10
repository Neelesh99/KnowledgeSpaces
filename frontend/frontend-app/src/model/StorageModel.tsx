export interface SimpleBlobUploadRequest {
    type: DataType,
    fileName: string,
    dataStream: Blob,
    knowledgeFileTarget: string,
    email: string
}

export type DataType = "PDF_DOCUMENT" | "WEB_LINK" | "YOUTUBE_LINK" | "PLAIN_TEXT"