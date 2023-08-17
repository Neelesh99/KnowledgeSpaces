import SubmitQuery from "../components/QueryType";
import {useState} from "react";
import {QueryContext} from "../service/QueryContext";
import KnowledgeFilePicker from "../components/KnowledgeFilePicker";
import {KnowledgeFile} from "../model/KnowledgeFile";

export default function Query() {

    const [queryResponse, setQueryResponse] = useState("Some response")
    const [query, setQuery] = useState("")
    const [chosenFile, setChosenFile] = useState<KnowledgeFile>({id: "someId",
        fileName: "default"})

    return <QueryContext.Provider value={{queryString: query, knowledgeFileTarget: chosenFile}}>
        <div className="flex flex-col p-12">
            <div className="text-6xl font-special">Query</div>
            <div className="flex flex-col space-y-4">
                <KnowledgeFilePicker setChosenFile={setChosenFile}/>
                <div className="bg-sky-100 h-full p-2 w-fit rounded-md">
                    <input className="text-2xl bg-transparent" type="text" placeholder="Input query" onChange={ (e) => setQuery(e.target.value) }/>
                </div>
                <SubmitQuery setResponse={setQueryResponse}/>
                <div className="p-2">
                    {queryResponse}
                </div>
            </div>
        </div>
    </QueryContext.Provider>


}