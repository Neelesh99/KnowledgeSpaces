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
        cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
        headers: {
            "Content-Type": "multipart/form-data",
        },
        redirect: "follow", // manual, *follow, error
        referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: formData, // body data type must match "Content-Type" header
    }).then((response) => {
        return response.text().then((data) => {
            return data
        })
    })
}