import {useState} from "react";
import {SpaceQueryContext} from "../service/QueryContext";
import SubmitSpace from "../components/SpaceType";
import KnowledgeSpacePicker from "../components/KnowledgeSpacePicker";
import {KnowledgeSpace} from "../model/KnowledgeSpace";

export default function Space() {

    const [queryResponse, setQueryResponse] = useState("Submit to see response")
    const [query, setQuery] = useState("")
    const [chosenSpace, setChosenSpace] = useState<KnowledgeSpace>({id: "someId",
        name: "default"})

    return <SpaceQueryContext.Provider value={{queryString: query, knowledgeSpaceTarget: chosenSpace}}>
        <div className="flex flex-col">
            <div className="w-full h-fit bg-slate-600">
                <div className="text-3xl font-special p-3 text-white">Space query</div>
            </div>
            <div className="flex flex-col px-12 py-2 bg-slate-100">
                <div className="">
                    <div className="flex flex-col">
                        <div className="text-2xl font-special py-3">Select knowledge space to query:</div>
                        <KnowledgeSpacePicker {...{setChosenFile: setChosenSpace}}/>
                        <div className="flex flex-row">
                            <div className="bg-sky-100 h-full p-2 my-3 w-fit rounded-md">
                                <input className="text-2xl bg-transparent" type="text" placeholder="Input query" onChange={ (e) => setQuery(e.target.value) }/>
                            </div>
                            <SubmitSpace setResponse={setQueryResponse}/>
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
    </SpaceQueryContext.Provider>


}