import {KnowledgeFile, SimpleFilesRequest} from "../model/KnowledgeFile";

interface FileJson {
    id: string,
    name: string
}

export async function getFilesForEmail(prefix: string, simpleFilesRequest: SimpleFilesRequest) : Promise<KnowledgeFile[]> {
    const url = prefix + "/contract/api/v1/getFiles?api=42"
    return await fetch(url, {
        method: "POST", // *GET, POST, PUT, DELETE, etc.
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(simpleFilesRequest), // body data type must match "Content-Type" header
    }).then((response) => {
        return response.json().then((data) => {
            const result: KnowledgeFile[] = []
            data.forEach((fileJson: object) => {
                const normalised = fileJson as FileJson
                result.push({
                    id: normalised.id,
                    fileName: normalised.name
                })
            })
            return result
        })
    })
}