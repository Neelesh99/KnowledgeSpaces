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
            <div className="flex text-lg w-32 font-special text-white items-center justify-center">
                <div>
                    MyStuff
                </div>
            </div>
            <NavigationButton title="Home" link="/"/>
            <NavigationButton title="Store" link="/store"/>
            <NavigationButton title="Query" link="/query"/>
            <NavigationButton title="Manage" link="/manage"/>
            <NavigationButton title="Space" link="/space"/>
        </div>
        <LoginButton title={props.user.valid ? props.user.username : "Login"} loginFunction={props.loginFunction}/>
    </div>

}