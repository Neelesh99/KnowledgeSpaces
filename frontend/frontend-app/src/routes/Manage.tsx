import KnowledgeFilePicker from "../components/KnowledgeFilePicker";
import {ChosenKnowledgeFileContext} from "../service/ChosenKnowledgeFileContext";
import {useState} from "react";
import DeleteType from "../components/DeleteType";

export default function Manage() {

    const [chosenFile, setChosenFile] = useState({id: "default", fileName: "default"})

    return <ChosenKnowledgeFileContext.Provider value={chosenFile}>
        <div className="flex flex-col">
            <div className="w-full h-fit bg-slate-600">
                <div className="text-3xl font-special p-3 text-white">Manage</div>
            </div>
            <div className="flex flex-col px-12 py-2 bg-slate-100">
                <div className="flex flex-col">
                    <div className="">
                        <div className="flex flex-col">
                            <div className="text-2xl font-special py-3">Select knowledge file to delete:</div>
                            <KnowledgeFilePicker {...{setChosenFile: setChosenFile}}/>
                        </div>
                    </div>
                    <div className="p-2">
                        <DeleteType/>
                    </div>
                </div>
            </div>
        </div>
    </ChosenKnowledgeFileContext.Provider>


}