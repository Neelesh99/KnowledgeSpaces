import {KnowledgeFile, SimpleFilesRequest} from "../model/KnowledgeFile";


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
                result.push({
                    id: fileJson["id"] as string,
                    fileName: fileJson["name"] as string
                })
            })
            return result
        })
    })
}