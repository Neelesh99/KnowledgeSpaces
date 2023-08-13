import {useState} from "react";
import SubmitNewFile from "./NewFileSubmit";


export function NewKnowledgeFile() {

    const [newFile, setNewFile] = useState("")


    return <div className="flex flex-col p-2 space-y-4">
        <div className="text-xl font-special">Create new knowledge file</div>
        <input className="text-2xl bg-transparent w-fit" type="text" placeholder="Input data" onChange={(e) => setNewFile(e.target.value)}/>
        <SubmitNewFile filename={newFile}/>
    </div>
}