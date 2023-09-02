import {useLocation, useNavigate} from "react-router-dom"

export const NavigationButton = ({ title, link }: { title: string, link: string }) => {

    const location: Location = useLocation();
    console.log(location.pathname)
    const unselectedStyle = "flex w-32 h-12 bg-slate-800 rounded-sm items-center font-special text-lg cursor-pointer transition justify-center hover:bg-gradient-to-r from-emerald-500 to-sky-500"
    const selectedStyle = "flex w-32 h-12 bg-slate-600 rounded-t-sm items-center font-special text-lg cursor-pointer transition justify-center hover:bg-gradient-to-r from-emerald-500 to-sky-500"

    const navigate = useNavigate()

    return <div
        onClick={() => navigate(link)}
        className={location.pathname === link ? selectedStyle : unselectedStyle}>
        <div className="text-white">
            {title}
        </div>
    </div>


}