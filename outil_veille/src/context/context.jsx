import { createContext, useState } from "react";

export const GlobalState=createContext(null);

export default function GlobalContext({children}){
    let [user,setuser]=useState(null);
    let [token,settoken]=useState("");
    let [role,setrole]=useState("");
    
    return(
        <GlobalState.Provider value={{user,setuser,token,settoken,role,setrole}}>
            {children}
        </GlobalState.Provider>
    )
}