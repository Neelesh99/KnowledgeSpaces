export interface KnowledgeSpace {
    id: string,
    name: string
}

export const SPACESDEFAULT = {
    id: "someId",
    name: "default"
}

export interface SimpleSpacesRequest {
    email: string
}