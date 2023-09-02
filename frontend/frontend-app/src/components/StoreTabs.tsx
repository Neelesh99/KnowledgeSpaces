import {useState} from 'react'
import {Tab} from '@headlessui/react'
import StoreType from "./StoreType";
import StoreLink from "./StoreLink";

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

    const [text, setText] = useState("")

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
                        <div className="h-full w-full flex items-center justify-center bg-slate-100 p-3 rounded-md hover:bg-slate-200 transition cursor-pointer" >
                            Upload File
                        </div>
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
                                <input className="text-2xl bg-transparent" type="text" placeholder="Input link"/>
                            </div>
                            <StoreLink/>
                        </div>
                    </Tab.Panel>
                </Tab.Panels>
            </Tab.Group>
        </div>
    )
}
