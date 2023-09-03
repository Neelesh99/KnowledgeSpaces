import {useState} from "react";
import SubmitNewSpace from "./NewSpaceSubmit";


export function NewKnowledgeSpace() {

    const [newSpace, setNewSpace] = useState("")


    return <div className="flex flex-col p-2 space-y-4">
        <div className="text-xl font-special">Create new knowledge space</div>
        <input className="text-2xl bg-transparent w-fit" type="text" placeholder="Input data" onChange={(e) => setNewSpace(e.target.value)}/>
        <SubmitNewSpace spacename={newSpace}/>
    </div>
}