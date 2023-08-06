import {NavigationButton} from "./NavigationButton";
import {LoginButton} from "./LoginButton";
import {User} from "../model/UserDataModel";

interface NavigationBarProps {
    user: User,
    loginFunction: () => void
}

export function NavigationBar(props: NavigationBarProps) {


    return <div className="w-screen bg-slate-500 h-12 absolute top-0 flex">
        <NavigationButton title="Home"/>
        <LoginButton title={props.user.valid ? props.user.username : "Login"} loginFunction={props.loginFunction}/>
    </div>

}