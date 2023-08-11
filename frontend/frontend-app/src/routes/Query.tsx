import SubmitQuery from "../components/QueryType";
import {useState} from "react";
import {QueryContext} from "../service/QueryContext";
import KnowledgeFilePicker from "../components/KnowledgeFilePicker";
import {ChosenKnowledgeFile} from "../model/KnowledgeFile";

export default function Query() {

    const queryResponse = "Some response"
    const [query, setQuery] = useState("")
    const [chosenFile, setChosenFile] = useState<ChosenKnowledgeFile>({id: "someId",
        fileName: "default"})

    return <QueryContext.Provider value={{queryString: query, knowledgeFileTarget: chosenFile}}>
        <div className="flex flex-col p-12">
            <div className="text-6xl font-special">Query</div>
            <div className="flex flex-col space-y-4">
                <div className="flex pt-12 items-center">
                    <div className="bg-sky-100 h-full p-2 rounded-md">
                        <input className="text-2xl bg-transparent" type="text" placeholder="Input query" onChange={ (e) => setQuery(e.target.value) }/>
                    </div>
                    <KnowledgeFilePicker setChosenFile={setChosenFile}/>
                    <SubmitQuery/>
                </div>
                <div className="p-2">
                    {queryResponse}
                </div>
            </div>
        </div>
    </QueryContext.Provider>


}