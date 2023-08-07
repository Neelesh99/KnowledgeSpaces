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

function App() {
    const [user, setUser] = useState<User>({username: "default", email: "default", valid: false})
    const prefix = "http://localhost:9000";

    function login() {
        void getUser(prefix, setUser).then(
            (result) => {
                if (!result) {
                    window.location = prefix + "/oauth/sd?referralUrl=" + window.location;
                }
            })
    }

    function getUserWithoutLogin() {
        void getUser(prefix, setUser).then(
            (result) => {
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
                <Home/>
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
    ]);

  return (
      <AuthenticationContext.Provider value={user}>
          <>
              <RouterProvider router={router} />
          </>
      </AuthenticationContext.Provider>
  )
}

export default App
