import {SimpleKnowledgeFileUpdateRequest, SimpleKnowledgeSpaceUpdateRequest} from "../model/UpdateModel";

export async function sendKnowledgeFileUpdateRequest(prefix: string, knowledgeFileUpdateRequest: SimpleKnowledgeFileUpdateRequest) : Promise<string> {
    const url = prefix + "/contract/api/v1/knowledgeFile/update?api=42"
    return await fetch(url, {
        method: "POST", // *GET, POST, PUT, DELETE, etc.
        cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
        headers: {
            "Content-Type": "application/json",
        },
        redirect: "follow", // manual, *follow, error
        referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: JSON.stringify(knowledgeFileUpdateRequest), // body data type must match "Content-Type" header
    }).then((response) => {
        return response.text().then((data) => {
            return data
        })
    })
}

export async function sendKnowledgeSpaceUpdateRequest(prefix: string, knowledgeSpaceUpdateRequest: SimpleKnowledgeSpaceUpdateRequest) : Promise<string> {
    const url = prefix + "/contract/api/v1/knowledgeSpace/update?api=42"
    return await fetch(url, {
        method: "POST", // *GET, POST, PUT, DELETE, etc.
        cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
        headers: {
            "Content-Type": "application/json",
        },
        redirect: "follow", // manual, *follow, error
        referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: JSON.stringify(knowledgeSpaceUpdateRequest), // body data type must match "Content-Type" header
    }).then((response) => {
        return response.text().then((data) => {
            return data
        })
    })
}