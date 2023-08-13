import {SimpleBlobUploadRequest} from "../model/StorageModel";

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