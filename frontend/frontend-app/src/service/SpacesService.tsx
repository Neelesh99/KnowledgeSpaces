import {KnowledgeSpace, SimpleSpacesRequest} from "../model/KnowledgeSpace";

interface SpacesJson {
    id: string,
    name: string
}

export async function getSpacesForEmail(prefix: string, simpleSpacesRequest: SimpleSpacesRequest) : Promise<KnowledgeSpace[]> {
    const url = prefix + "/contract/api/v1/getSpaces?api=42"
    return await fetch(url, {
        method: "POST", // *GET, POST, PUT, DELETE, etc.
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(simpleSpacesRequest), // body data type must match "Content-Type" header
    }).then((response) => {
        return response.json().then((data) => {
            const result: KnowledgeSpace[] = []
            data.forEach((fileJson: object) => {
                const normalised = fileJson as SpacesJson
                result.push({
                    id: normalised.id,
                    name: normalised.name
                })
            })
            return result
        })
    })
}