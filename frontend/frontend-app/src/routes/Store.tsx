import StoreTabs from "../components/StoreTabs";
import KnowledgeFilePicker from "../components/KnowledgeFilePicker";
import {ChosenKnowledgeFileContext} from "../service/ChosenKnowledgeFileContext";
import {useState} from "react";
import {NewKnowledgeFile} from "../components/NewKnowledgeFile";

export default function Store() {

    const [chosenFile, setChosenFile] = useState({id: "default", fileName: "default"})

    return <ChosenKnowledgeFileContext.Provider value={chosenFile}>
        <div className="flex flex-col">
            <div className="w-full h-fit bg-slate-600">
                <div className="text-3xl font-special p-3 text-white">Store</div>
            </div>
            <div className="flex flex-col px-12 py-2 bg-slate-100">
                <div className="grid grid-cols-2">
                    <div className="">
                        <div className="flex flex-row">
                            <div className="text-2xl font-special p-3">Select knowledge file to save to:</div>
                            <KnowledgeFilePicker {...{setChosenFile: setChosenFile}}/>
                        </div>
                        <StoreTabs/>
                    </div>
                    <div className="p-2">
                        <NewKnowledgeFile/>
                    </div>
                </div>
            </div>
        </div>
    </ChosenKnowledgeFileContext.Provider>


}