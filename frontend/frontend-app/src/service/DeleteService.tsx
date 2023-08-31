import {SimpleKnowledgeFileDeletionRequest} from "../model/StorageModel";

export async function sendDeleteKnowledgeFile(prefix: string, simpleKnowledgeFileDeletionRequest: SimpleKnowledgeFileDeletionRequest) : Promise<string> {
    const url = prefix + "/contract/api/v1/knowledgeFile/delete?api=42"
    return await fetch(url, {
        method: "POST", // *GET, POST, PUT, DELETE, etc.
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(simpleKnowledgeFileDeletionRequest), // body data type must match "Content-Type" header
    }).then((response) => {
        return response.text().then((data) => {
            return data
        })
    })
}