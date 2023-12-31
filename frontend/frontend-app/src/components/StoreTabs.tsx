import {ChangeEvent, useContext, useState} from 'react'
import {Tab} from '@headlessui/react'
import StoreType from "./StoreType";
import {SimpleBlobUploadRequest} from "../model/StorageModel";
import {convertSimpleUploadRequestToForData, sendUploadForm} from "../service/StoreService";
import {ChosenKnowledgeFileContext} from "../service/ChosenKnowledgeFileContext";
import {AuthenticationContext} from "../service/AuthenticationContext";
import {EnvironmentContext} from "../service/EnvironmentContext";
import StoreLinkType from "./StoreLinkType";

function classNames(...classes: string[]) {
    return classes.filter(Boolean).join(' ')
}

export default function StoreTabs() {
    const [categories] = useState({
        Text: [
        ],
        PDF: [
        ],
        WebLink: [
        ],
    })
    const targetFile = useContext(ChosenKnowledgeFileContext)
    const user = useContext(AuthenticationContext)
    const environment = useContext(EnvironmentContext)

    const [text, setText] = useState("")
    const [file, setFile] = useState<File>()

    function onFileUpload(event: ChangeEvent<HTMLInputElement>) {
        setFile(event.target.files![0])
    }

    function onFileSubmit() {
        if (file === undefined) {
            alert("No file selected")
        } else {
            const simpleUploadRequest: SimpleBlobUploadRequest = {
                type: "PDF_DOCUMENT",
                fileName: file.name,
                dataStream: file,
                knowledgeFileTarget: targetFile.id,
                email: user.email
            }
            const formData = convertSimpleUploadRequestToForData(simpleUploadRequest)
            void sendUploadForm(environment.backendPrefix, formData).then(r => console.log(r))
        }
    }

    return (
        <div className="w-full max-w-md px-4 py-16 sm:px-0">
            <Tab.Group>
                <Tab.List className="flex space-x-1 rounded-xl bg-blue-900/20 p-1">
                    {Object.keys(categories).map((category) => (
                        <Tab
                            key={category}
                            className={({ selected }) =>
                                classNames(
                                    'w-full rounded-lg py-2.5 text-sm font-medium leading-5 text-blue-700',
                                    'ring-white ring-opacity-60 ring-offset-2 ring-offset-blue-400 focus:outline-none focus:ring-2',
                                    selected
                                        ? 'bg-white shadow'
                                        : 'text-blue-100 hover:bg-white/[0.12] hover:text-white'
                                )
                            }
                        >
                            {category}
                        </Tab>
                    ))}
                </Tab.List>
                <Tab.Panels className="mt-2">
                    <Tab.Panel
                        key={0}
                        className={classNames(
                            'rounded-xl p-3',
                            'ring-white ring-opacity-60 ring-offset-2 ring-offset-blue-400 focus:outline-none focus:ring-2'
                        )}
                    >
                        <div className="flex items-center">
                            <div className="bg-slate-100 h-full p-2 rounded-md">
                                <input className="text-2xl bg-transparent" type="text" placeholder="Input data" onChange={(e) => setText(e.target.value)}/>
                            </div>
                            <StoreType {...{text: text}}/>
                        </div>
                    </Tab.Panel>
                    <Tab.Panel
                        key={1}
                        className={classNames(
                            'rounded-xl p-3',
                            'ring-white ring-opacity-60 ring-offset-2 ring-offset-blue-400 focus:outline-none focus:ring-2'
                        )}
                    >
                        <input
                            type="file"
                            onChange={(e) => onFileUpload(e)}
                            className="h-full w-full flex items-center justify-center bg-slate-100 p-3 rounded-md hover:bg-slate-200 transition cursor-pointer"/>
                        <div
                            className="h-full w-full flex items-center justify-center bg-slate-100 p-3 rounded-md hover:bg-slate-200 transition cursor-pointer"
                            onClick={() => onFileSubmit()}
                        >Upload File</div>
                    </Tab.Panel>
                    <Tab.Panel
                        key={2}
                        className={classNames(
                            'rounded-xl p-3',
                            'ring-white ring-opacity-60 ring-offset-2 ring-offset-blue-400 focus:outline-none focus:ring-2'
                        )}
                    >
                        <div className="flex items-center">
                            <div className="bg-slate-100 h-full p-2 rounded-md">
                                <input className="text-2xl bg-transparent" type="text" placeholder="Input link" onChange={(e) => setText(e.target.value)}/>
                            </div>
                            <StoreLinkType {...{text: text}}/>
                        </div>
                    </Tab.Panel>
                </Tab.Panels>
            </Tab.Group>
        </div>
    )
}
