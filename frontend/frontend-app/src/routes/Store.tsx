import StoreTabs from "../components/StoreTabs";
import KnowledgeFilePicker from "../components/KnowledgeFilePicker";
import {ChosenKnowledgeFileContext} from "../service/ChosenKnowledgeFileContext";
import {useState} from "react";

export default function Store() {

    const [chosenFile, setChosenFile] = useState({id: "default", fileName: "default"})

    const queryResponse = "Some response"

    return <ChosenKnowledgeFileContext.Provider value={chosenFile}>
        <div className="flex flex-col p-12">
            <div className="text-6xl font-special">Store</div>
            <KnowledgeFilePicker {...{setChosenFile: setChosenFile}}/>
            <div className="flex flex-col space-y-4">
                <div className="flex items-center">
                    <StoreTabs/>
                </div>
                <div className="p-2">
                    {queryResponse}
                </div>
            </div>
        </div>
    </ChosenKnowledgeFileContext.Provider>


}