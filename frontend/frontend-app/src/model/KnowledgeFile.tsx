export interface KnowledgeFile {
    id: string,
    fileName: string
}

export const DEFAULT = {
    id: "someId",
    fileName: "default"
}

export interface SimpleFilesRequest {
    email: string
}