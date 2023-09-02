
export interface HomeProps {
    loginFunction: () => void
}

export default function Home(props: HomeProps) {

    return <div className="flex flex-col">
        <div className="w-full flex h-fit">
            <div className="w-1/2 bg-slate-600 text-white text-8xl font-special px-6 py-12">
                A space for your stuff, with GPT powered queries
            </div>
            <div className="w-1/2 bg-slate-600 p-12">
                <div className="flex flex-col place-content-between h-full w-full bg-slate-400 rounded-xl">
                    <div className="w-full p-6 text-2xl font-special">
                        MyStuff is an app for keeping your digital life organised and searchable. Your documents are ingested into a Large Language Model which then allows you to query your data with simple natural language prompts.
                    </div>
                    <div
                        onClick={() => props.loginFunction()}
                        className="flex w-full h-12 rounded-b-xl justify-center py-3 cursor-pointer transition duration-300 hover:bg-gradient-to-r from-emerald-500 to-sky-500 hover:text-white">
                        Sign-up now
                    </div>
                </div>
            </div>
        </div>
        <div className="w-full flex h-fit bg-slate-100">
            <div className="grid grid-cols-2 w-full">
                <div className="flex p-4">
                    <img className="rounded-xl" src={"https://static.bimago.pl/mediacache/catalog/product/cache/0/6/147660/image/1500x2240/1643cf240370c0352b784358b4070f9e/147660_1.jpg"}/>
                </div>
                <div className="p-4">
                    <h1 className="font-special text-2xl">Knowledge files</h1>
                    <p className="mt-4">
                        We have oriented our service around "Knowledge Files", these are collections of separate
                        pieces of data that you upload. All the data in any individual knowledge file is then sent to our
                        LLM server which processes the data into a searchable space. You can then query these knowledge files
                        in our Query page using natural language queries.
                    </p>
                </div>
            </div>
        </div>
    </div>
}