import {useLocation, useNavigate} from "react-router-dom"

export const NavigationButton = ({ title, link }: { title: string, link: string }) => {

    const location = useLocation()
    const unselectedStyle = "h-fit w-fit px-2 rounded-lg hover:bg-teal-700 hover:text-white transition"
    const selectedStyle = "h-fit w-fit px-2 bg-teal-700 rounded-lg text-white hover:bg-teal-800 transition"

    const isSelected = location.pathname === link;

    const navigate = useNavigate()
    return <div
        onClick={() => navigate(link)}
        className={"flex w-32 h-12 rounded-t-sm items-center font-special text-lg cursor-pointer transition justify-center"}>
        <div className={isSelected ? selectedStyle : unselectedStyle}>
            {title}
        </div>
    </div>


}