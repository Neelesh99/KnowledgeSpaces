import SubmitQuery from "../components/QueryType";
import {useState} from "react";
import {QueryContext} from "../service/QueryContext";
import KnowledgeFilePicker from "../components/KnowledgeFilePicker";
import {KnowledgeFile} from "../model/KnowledgeFile";

export default function Query() {

    const [queryResponse, setQueryResponse] = useState("Submit to see response")
    const [query, setQuery] = useState("")
    const [chosenFile, setChosenFile] = useState<KnowledgeFile>({id: "someId",
        fileName: "default"})

    return <QueryContext.Provider value={{queryString: query, knowledgeFileTarget: chosenFile}}>
        <div className="flex flex-col">
            <div className="w-full h-fit bg-sky-100">
                <div className="text-3xl font-special p-3">Query</div>
            </div>
            <div className="flex flex-col px-12 py-2 bg-slate-100">
                <div className="">
                    <div className="flex flex-col">
                        <div className="text-2xl font-special py-3">Select knowledge file to query:</div>
                        <KnowledgeFilePicker {...{setChosenFile: setChosenFile}}/>
                        <div className="flex flex-row">
                            <div className="bg-sky-100 h-full p-2 my-3 w-fit rounded-md">
                                <input className="text-2xl bg-transparent" type="text" placeholder="Input query" onChange={ (e) => setQuery(e.target.value) }/>
                            </div>
                            <SubmitQuery setResponse={setQueryResponse}/>
                        </div>
                        <div className="font-special text-xl">
                            Response:
                        </div>
                        <div className="py-2">
                            {queryResponse}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </QueryContext.Provider>


}