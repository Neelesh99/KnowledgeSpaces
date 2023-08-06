import {NavigationButton} from "./NavigationButton";
import {LoginButton} from "./LoginButton";
import {User} from "../model/UserDataModel";

interface NavigationBarProps {
    user: User,
    loginFunction: () => void
}

export function NavigationBar(props: NavigationBarProps) {


    return <div className="w-screen bg-slate-800 h-12 sticky top-0 flex justify-between">
        <div className="flex">
            <NavigationButton title="Home"/>
            <NavigationButton title="Query"/>
            <NavigationButton title="Store"/>
        </div>
        <LoginButton title={props.user.valid ? props.user.username : "Login"} loginFunction={props.loginFunction}/>
    </div>

}