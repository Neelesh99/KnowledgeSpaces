
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
                        className="flex w-full h-12 rounded-b-xl justify-center py-3 cursor-pointer transition duration-300 hover:bg-gradient-to-r from-sky-900 to-sky-500 hover:text-white">
                        Sign-up now
                    </div>
                </div>
            </div>
        </div>
    </div>
}