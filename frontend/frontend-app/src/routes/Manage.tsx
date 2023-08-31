import KnowledgeFilePicker from "../components/KnowledgeFilePicker";
import {ChosenKnowledgeFileContext} from "../service/ChosenKnowledgeFileContext";
import {useState} from "react";
import DeleteType from "../components/DeleteType";

export default function Manage() {

    const [chosenFile, setChosenFile] = useState({id: "default", fileName: "default"})

    return <ChosenKnowledgeFileContext.Provider value={chosenFile}>
        <div className="flex flex-col p-12">
            <div className="text-6xl font-special">Store</div>
            <KnowledgeFilePicker {...{setChosenFile: setChosenFile}}/>
            <div className="flex flex-col space-y-4">
                <DeleteType/>
            </div>
        </div>
    </ChosenKnowledgeFileContext.Provider>


}