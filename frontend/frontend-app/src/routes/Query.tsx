import SubmitQuery from "../components/QueryType";

export default function Query() {

    const queryValue = "Some response"

    return <div className="flex flex-col p-12">
        <div className="text-6xl font-special">Query</div>
        <div className="flex flex-col space-y-4">
            <div className="flex pt-12 items-center">
                <div className="bg-sky-100 h-full p-2 rounded-md">
                    <input className="text-2xl bg-transparent" type="text" placeholder="Input query"/>
                </div>
                <SubmitQuery/>
            </div>
            <div className="p-2">
                {queryValue}
            </div>
        </div>
    </div>

}