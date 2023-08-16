import {User} from "../model/UserDataModel";


export function redirectToOAuth(prefix: string) {
    window.location.href = prefix + "/oauth"
}

function getOnfulfilled(setAuth: (user: User) => void) {
    return (success: Response) => {
        return success.json().then(
            (data) => {
                console.log(data)
                const user: User = {
                    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access,@typescript-eslint/no-unsafe-assignment
                    username: data["username"],
                    // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access,@typescript-eslint/no-unsafe-assignment
                    email: data["email"],
                    valid: true
                }
                setAuth(user)
                return true
            }, () => {
                return false
            }
        )
    };
}

export function getUser(prefix: string, setAuth: (user: User) => void) : Promise<boolean> {
    return fetch(prefix + "/oauth/getUser", {
        credentials: "include"
    }).then(getOnfulfilled(setAuth), () => {
        return false
    })
}