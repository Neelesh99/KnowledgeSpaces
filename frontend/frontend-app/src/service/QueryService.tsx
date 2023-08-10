import {SimpleQueryRequest, SimpleSpaceQueryRequest} from "../model/QueryModel";

export function generateFileQueryStructure(email:string, target: string, text: string): SimpleQueryRequest {
    return {
        email: email,
        knowledgeFileTarget: target,
        query: text
    }
}

export function generateSpaceQueryStructure(email:string, target: string, text: string): SimpleSpaceQueryRequest {
    return {
        email: email,
        knowledgeSpaceTarget: target,
        query: text
    }
}

export async function sendQueryRequest(prefix: string, queryRequest: SimpleQueryRequest) : Promise<string> {
    const url = prefix + "/contract/api/v1/queryRequest&api=42"
    return await fetch(url, {
        method: "POST", // *GET, POST, PUT, DELETE, etc.
        cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
        headers: {
            "Content-Type": "application/json",
        },
        redirect: "follow", // manual, *follow, error
        referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: JSON.stringify(queryRequest), // body data type must match "Content-Type" header
    }).then((response) => {
        return response.text().then((data) => {
            return data
        })
    })
}


export async function sendSpaceQueryRequest(prefix: string, queryRequest: SimpleSpaceQueryRequest) : Promise<string> {
    const url = prefix + "/contract/api/v1/space/queryRequest&api=42"
    return await fetch(url, {
        method: "POST", // *GET, POST, PUT, DELETE, etc.
        cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
        headers: {
            "Content-Type": "application/json",
        },
        redirect: "follow", // manual, *follow, error
        referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: JSON.stringify(queryRequest), // body data type must match "Content-Type" header
    }).then((response) => {
        return response.text().then((data) => {
            return data
        })
    })
}