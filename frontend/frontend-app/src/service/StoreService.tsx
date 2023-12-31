import {
    SimpleBlobUploadRequest,
    SimpleKnowledgeFileCreationRequest,
    SimpleKnowledgeSpaceCreationRequest
} from "../model/StorageModel";

export function convertTextToBlob(text: string) : Blob {
    return new Blob([text], {
        type: 'text/plain'
    })
}

export function convertSimpleUploadRequestToForData(simpleUploadRequest: SimpleBlobUploadRequest) : FormData {
    const result = new FormData()
    result.append("dataType", simpleUploadRequest.type)
    result.append("fileName", simpleUploadRequest.fileName)
    result.append("file", simpleUploadRequest.dataStream, simpleUploadRequest.fileName)
    result.append("knowledgeFileTarget", simpleUploadRequest.knowledgeFileTarget)
    result.append("email", simpleUploadRequest.email)
    return result
}

export async function sendUploadForm(prefix: string, formData: FormData) : Promise<string> {
    const url = prefix + "/contract/api/v1/upload/blob?api=42"
    return await fetch(url, {
        method: "POST", // *GET, POST, PUT, DELETE, etc.
        body: formData, // body data type must match "Content-Type" header
    }).then((response) => {
        return response.text().then((data) => {
            return data
        })
    })
}

export async function sendCreateKnowledgeFile(prefix: string, simpleCreateFileRequest: SimpleKnowledgeFileCreationRequest) : Promise<string> {
    const url = prefix + "/contract/api/v1/knowledgeFile/create?api=42"
    return await fetch(url, {
        method: "POST", // *GET, POST, PUT, DELETE, etc.
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(simpleCreateFileRequest), // body data type must match "Content-Type" header
    }).then((response) => {
        return response.text().then((data) => {
            return data
        })
    })
}

export async function sendCreateKnowledgeSpace(prefix: string, simpleKnowledgeSpaceCreationRequest: SimpleKnowledgeSpaceCreationRequest) : Promise<string> {
    const url = prefix + "/contract/api/v1/knowledgeSpace/create?api=42"
    return await fetch(url, {
        method: "POST", // *GET, POST, PUT, DELETE, etc.
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(simpleKnowledgeSpaceCreationRequest), // body data type must match "Content-Type" header
    }).then((response) => {
        return response.text().then((data) => {
            return data
        })
    })
}
