export const LoginButton = ({ title, loginFunction }: { title: string, loginFunction: () => void }) => {

    return <div className="flex w-36 h-12 rounded-sm items-center font-special text-lg cursor-pointer transition justify-center" onClick={() => loginFunction()}>
        <div className="rounded-lg px-2 hover:bg-teal-700 hover:text-white transition">
            {title}
        </div>
    </div>


}