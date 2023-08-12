import {Fragment, useContext, useEffect, useState} from 'react'
import {Combobox, Transition} from '@headlessui/react'
import {CheckIcon, ChevronUpDownIcon} from '@heroicons/react/20/solid'
import {KnowledgeFile, SimpleFilesRequest} from "../model/KnowledgeFile";
import {AuthenticationContext} from "../service/AuthenticationContext";
import {getFilesForEmail} from "../service/FilesService";

const defaultFiles: KnowledgeFile[] = [
    { id: "1", fileName: 'Default' },
    { id: "2", fileName: 'SomeKnowledgeFile' },
    { id: "3", fileName: 'SomeOtherKnowledgeFile' },
    { id: "4", fileName: 'SomeOther KnowledgeFile' },
    { id: "5", fileName: 'SomeOther Knowledge File' },
    { id: "6", fileName: 'Some Other Knowledge File' },
]

export interface KnowledgeFilePickerProps {
    setChosenFile: (ChosenKnowledgeFile) => void
}

export default function KnowledgeFilePicker(props: KnowledgeFilePickerProps) {
    const [files, setFiles] = useState(defaultFiles)
    const [selected, setSelected] = useState(files.length > 0 ? files[0] : defaultFiles[0])
    const [query, setQuery] = useState('')

    const user = useContext(AuthenticationContext)
    useEffect(() => {
        const email = user.email
        const simpleFilesRequest: SimpleFilesRequest = {email: email}
        const prefix = "http://localhost:9000"
        void getFilesForEmail(prefix, simpleFilesRequest).then((files) => {
            console.log(files)
            setFiles(files)
        })
    }, [])

    const filteredPeople =
        query === ''
            ? files
            : files.filter((person) =>
                person.fileName
                    .toLowerCase()
                    .replace(/\s+/g, '')
                    .includes(query.toLowerCase().replace(/\s+/g, ''))
            )

    return (
        <div className="pt-6 w-72">
            <Combobox value={selected} onChange={(file) => {
                setSelected(file)
                props.setChosenFile(file)
            }}>
                <div className="relative mt-1">
                    <div className="relative w-full cursor-default overflow-hidden rounded-lg bg-white text-left shadow-md focus:outline-none focus-visible:ring-2 focus-visible:ring-white focus-visible:ring-opacity-75 focus-visible:ring-offset-2 focus-visible:ring-offset-teal-300 sm:text-sm">
                        <Combobox.Input
                            className="w-full border-none py-2 pl-3 pr-10 text-sm leading-5 text-gray-900 focus:ring-0"
                            displayValue={(person) => person.fileName}
                            onChange={(event) => setQuery(event.target.value)}
                        />
                        <Combobox.Button className="absolute inset-y-0 right-0 flex items-center pr-2">
                            <ChevronUpDownIcon
                                className="h-5 w-5 text-gray-400"
                                aria-hidden="true"
                            />
                        </Combobox.Button>
                    </div>
                    <Transition
                        as={Fragment}
                        leave="transition ease-in duration-100"
                        leaveFrom="opacity-100"
                        leaveTo="opacity-0"
                        afterLeave={() => setQuery('')}
                    >
                        <Combobox.Options className="absolute mt-1 max-h-60 w-full overflow-auto rounded-md bg-white py-1 text-base shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none sm:text-sm">
                            {filteredPeople.length === 0 && query !== '' ? (
                                <div className="relative cursor-default select-none py-2 px-4 text-gray-700">
                                    Nothing found.
                                </div>
                            ) : (
                                filteredPeople.map((person) => (
                                    <Combobox.Option
                                        key={person.id}
                                        className={({ active }) =>
                                            `relative cursor-default select-none py-2 pl-10 pr-4 ${
                                                active ? 'bg-teal-600 text-white' : 'text-gray-900'
                                            }`
                                        }
                                        value={person}
                                    >
                                        {({ selected, active }) => (
                                            <>
                        <span
                            className={`block truncate ${
                                selected ? 'font-medium' : 'font-normal'
                            }`}
                        >
                          {person.fileName}
                        </span>
                                                {selected ? (
                                                    <span
                                                        className={`absolute inset-y-0 left-0 flex items-center pl-3 ${
                                                            active ? 'text-white' : 'text-teal-600'
                                                        }`}
                                                    >
                            <CheckIcon className="h-5 w-5" aria-hidden="true" />
                          </span>
                                                ) : null}
                                            </>
                                        )}
                                    </Combobox.Option>
                                ))
                            )}
                        </Combobox.Options>
                    </Transition>
                </div>
            </Combobox>
        </div>
    )
}
