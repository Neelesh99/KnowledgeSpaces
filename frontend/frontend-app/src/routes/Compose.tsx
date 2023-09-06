import {ChosenKnowledgeSpaceContext} from "../service/ChosenKnowledgeFileContext";
import {useContext, useEffect, useState} from "react";
import {NewKnowledgeSpace} from "../components/NewKnowledgeSpace";
import KnowledgeSpacePicker from "../components/KnowledgeSpacePicker";
import ComposeSet from "../components/ComposeSet";
import {AuthenticationContext} from "../service/AuthenticationContext";
import {EnvironmentContext} from "../service/EnvironmentContext";
import {KnowledgeFile, SimpleFilesRequest} from "../model/KnowledgeFile";
import {getFilesForEmail} from "../service/FilesService";
import {SimpleKnowledgeSpaceUpdateRequest} from "../model/UpdateModel";
import {sendKnowledgeSpaceUpdateRequest} from "../service/UpdateService";

const defaultFiles: KnowledgeFile[] = []

export default function Compose() {

    const [chosenSpace, setChosenSpace] = useState({id: "default", name: "default"})
    const [files, setFiles] = useState(defaultFiles)
    const user = useContext(AuthenticationContext)
    const environment = useContext(EnvironmentContext)
    const [chosenFiles, setChosenFiles] = useState(defaultFiles)
    useEffect(() => {
        const email = user.email
        const simpleFilesRequest: SimpleFilesRequest = {email: email}
        const prefix = environment.backendPrefix
        void getFilesForEmail(prefix, simpleFilesRequest).then((files) => {
            console.log(files)
            setFiles(files)
        })
    }, [user.valid])

    function pickFile(file: KnowledgeFile) {
        if (chosenFiles.includes(file)) {
            const index = chosenFiles.indexOf(file)
            const final = chosenFiles.slice(0, index).concat(chosenFiles.slice(index + 1))
            setChosenFiles(final)
        } else {
            setChosenFiles(chosenFiles.concat([file]))
        }
    }

    function updateSpace() {
        const spaceUpdateRequest: SimpleKnowledgeSpaceUpdateRequest = {
            knowledgeSpaceId: chosenSpace.id,
            email: user.email,
            newName: chosenSpace.name,
            newFiles: chosenFiles.map(file => file.id)
        }
        void sendKnowledgeSpaceUpdateRequest(environment.backendPrefix, spaceUpdateRequest)
    }

    return <ChosenKnowledgeSpaceContext.Provider value={chosenSpace}>
        <div className="flex flex-col">
            <div className="w-full h-fit bg-slate-600">
                <div className="text-3xl font-special p-3 text-white">Compose</div>
            </div>
            <div className="flex flex-col px-12 py-2 bg-slate-100">
                <div className="grid grid-cols-2">
                    <div className="">
                        <div className="flex flex-col">
                            <div className="text-2xl font-special py-3">Select knowledge file to save to:</div>
                            <KnowledgeSpacePicker {...{setChosenFile: setChosenSpace}}/>
                        </div>
                        <div className="my-4 font-special">Select files to add to space:</div>
                        <ComposeSet files={files} setChosenComposeFile={pickFile}/>
                        <div className="flex flex-row">
                            {chosenFiles.map(file => <div>{file.fileName}</div>)}
                        </div>
                        <div
                            onClick={() => updateSpace()}
                            className="w-fit h-fit p-2 m-3 bg-slate-300 rounded-lg hover:bg-slate-400 hover:text-white cursor-pointer">
                            Submit
                        </div>
                    </div>
                    <div className="p-2">
                        <NewKnowledgeSpace/>
                    </div>
                </div>
            </div>
        </div>
    </ChosenKnowledgeSpaceContext.Provider>


}