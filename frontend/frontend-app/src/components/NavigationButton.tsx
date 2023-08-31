import {useNavigate} from "react-router-dom"

export const NavigationButton = ({ title, link }: { title: string, link: string }) => {

    const navigate = useNavigate()

    return <div
        onClick={() => navigate(link)}
        className="flex w-32 h-12 bg-slate-800 rounded-sm items-center font-special text-lg cursor-pointer transition justify-center hover:bg-gradient-to-r from-emerald-500 to-sky-500">
        <div className="text-white">
            {title}
        </div>
    </div>


}