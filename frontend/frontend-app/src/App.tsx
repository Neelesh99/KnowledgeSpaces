import {useEffect, useState} from 'react'
import {createBrowserRouter, RouterProvider,} from "react-router-dom";
import './App.css'
import './index.css'
import {AuthenticationContext} from "./service/AuthenticationContext";
import {User} from "./model/UserDataModel";
import {getUser} from "./service/UserAuthenticationService";
import {NavigationBar} from "./components/NavigationBar";
import Home from "./routes/Home";
import ErrorPage from "./routes/ErrorPage";
import Store from "./routes/Store";
import Query from "./routes/Query";
import {EnvironmentContext} from "./service/EnvironmentContext";
import {Environment} from "./model/EnvironmentModel";
import Manage from "./routes/Manage";
import Space from "./routes/Space";
import Compose from "./routes/Compose";

function App() {
    const [user, setUser] = useState<User>({username: "default", email: "default", valid: false})
    const environment: Environment = {
        backendPrefix: import.meta.env.VITE_BACKEND_PREFIX as string
    }
    const prefix = environment.backendPrefix;
    console.log(prefix)

    function login() {
        void getUser(prefix, setUser).then(
            (result) => {
                if (!result) {
                    window.location.assign(prefix + "/oauth/sd?referralUrl=" + window.location.toString())
                }
            })
    }

    function getUserWithoutLogin() {
        void getUser(prefix, setUser).then(
            (result) => {
                console.log(result)
            })
    }

    useEffect(() => {
        getUserWithoutLogin();
    }, [])

    const router = createBrowserRouter([
        {
            path: "/",
            element: <div>
                <NavigationBar {...{user: user, loginFunction: login}}/>
                <Home {...{loginFunction: login}}/>
            </div>,
            errorElement: <div>
                <NavigationBar {...{user: user, loginFunction: login}}/>
                <ErrorPage/>
            </div>
        },
        {
            path: "/store",
            element: <div>
                <NavigationBar {...{user: user, loginFunction: login}}/>
                <Store/>
            </div>,
            errorElement: <div>
                <NavigationBar {...{user: user, loginFunction: login}}/>
                <ErrorPage/>
            </div>
        },
        {
            path: "/manage",
            element: <div>
                <NavigationBar {...{user: user, loginFunction: login}}/>
                <Manage/>
            </div>,
            errorElement: <div>
                <NavigationBar {...{user: user, loginFunction: login}}/>
                <ErrorPage/>
            </div>
        },
        {
            path: "/query",
            element: <div>
                <NavigationBar {...{user: user, loginFunction: login}}/>
                <Query/>
            </div>,
            errorElement: <div>
                <NavigationBar {...{user: user, loginFunction: login}}/>
                <ErrorPage/>
            </div>
        },
        {
            path: "/compose",
            element: <div>
                <NavigationBar {...{user: user, loginFunction: login}}/>
                <Compose/>
            </div>,
            errorElement: <div>
                <NavigationBar {...{user: user, loginFunction: login}}/>
                <ErrorPage/>
            </div>
        },
        {
            path: "/space",
            element: <div>
                <NavigationBar {...{user: user, loginFunction: login}}/>
                <Space/>
            </div>,
            errorElement: <div>
                <NavigationBar {...{user: user, loginFunction: login}}/>
                <ErrorPage/>
            </div>
        },
    ]);

  return (
      <AuthenticationContext.Provider value={user}>
          <>
              <EnvironmentContext.Provider value={environment}>
                <RouterProvider router={router} />
              </EnvironmentContext.Provider>
          </>
      </AuthenticationContext.Provider>
  )
}

export default App
