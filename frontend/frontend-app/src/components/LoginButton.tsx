export const LoginButton = ({ title, loginFunction }: { title: string, loginFunction: () => void }) => {

    return <div className="flex w-32 h-12 bg-zinc-800 rounded-sm items-center font-special text-lg cursor-pointer transition justify-center hover:bg-gradient-to-r from-cyan-500 to-blue-500" onClick={() => loginFunction()}>
        <div className="text-white">
            {title}
        </div>
    </div>


}