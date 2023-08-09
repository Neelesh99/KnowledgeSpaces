import {useState} from 'react'
import {Tab} from '@headlessui/react'
import StoreType from "./StoreType";
import StoreLink from "./StoreLink";

function classNames(...classes) {
    return classes.filter(Boolean).join(' ')
}

export default function StoreTabs() {
    const [categories] = useState({
        Text: [
            {
                id: 1,
                title: 'Does drinking coffee make you smarter?',
                date: '5h ago',
                commentCount: 5,
                shareCount: 2,
            },
            {
                id: 2,
                title: "So you've bought coffee... now what?",
                date: '2h ago',
                commentCount: 3,
                shareCount: 2,
            },
        ],
        PDF: [
            {
                id: 1,
                title: 'Is tech making coffee better or worse?',
                date: 'Jan 7',
                commentCount: 29,
                shareCount: 16,
            },
            {
                id: 2,
                title: 'The most innovative things happening in coffee',
                date: 'Mar 19',
                commentCount: 24,
                shareCount: 12,
            },
        ],
        WebLink: [
            {
                id: 1,
                title: 'Ask Me Anything: 10 answers to your questions about coffee',
                date: '2d ago',
                commentCount: 9,
                shareCount: 5,
            },
            {
                id: 2,
                title: "The worst advice we've ever heard about coffee",
                date: '4d ago',
                commentCount: 1,
                shareCount: 2,
            },
        ],
    })

    return (
        <div className="w-full max-w-md px-2 py-16 sm:px-0">
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
                            'rounded-xl bg-white p-3',
                            'ring-white ring-opacity-60 ring-offset-2 ring-offset-blue-400 focus:outline-none focus:ring-2'
                        )}
                    >
                        <div className="flex items-center">
                            <div className="bg-slate-100 h-full p-2 rounded-md">
                                <input className="text-2xl bg-transparent" type="text" placeholder="Input data"/>
                            </div>
                            <StoreType/>
                        </div>
                    </Tab.Panel>
                    <Tab.Panel
                        key={1}
                        className={classNames(
                            'rounded-xl bg-white p-3',
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
                            'rounded-xl bg-white p-3',
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
