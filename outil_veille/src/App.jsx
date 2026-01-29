import { useState,useEffect ,useContext} from 'react'
import './App.css'
import { useNavigate,Route,Routes } from 'react-router-dom'
import Login from './login/login'
import Signup from './signup/signup'
import SourcesManagement from './veilleur/veilleur'
import { GlobalState } from './context/context'
import ProfilesPage from './Decideur/profiles'

import AnalyzerReview from './analyseur/analyseur'
import VisiteurPage from './visiteur/visiteur'
function App() {

  let {user,token,role,setuser,settoken,setrole}=useContext(GlobalState);
   
  let navigate=useNavigate();
  async function fetchuser(token) {
        try{
          let res=await fetch("http://localhost:8080/api/auth/verify",{
          method:"GET",
          headers:{
             "Authorization": `Bearer ${token}`
          }
        });
        if(res.ok){
          let data=await res.json();
          console.log(data);
          return data
        }else{
          navigate("/login")
          return null
        }
        }catch(error){
          navigate("/login")
        }
        
      }
       async function handeluser() {
        const storeduser=localStorage.getItem("userinfo");
          if(storeduser){
             const userdata=JSON.parse(storeduser);
             let data= await fetchuser(userdata.token);
             if(data &&data.email==userdata.utilisateur.email){
              console.log(userdata)
              settoken(userdata.token);
              setuser(userdata.utilisateur);
              setrole(userdata.role);
              setrole(data.role); 
              if(userdata.role=="Décideur"){
                console.log("ff")
                navigate("/decideur")
              } else if(userdata.role=="Analyste"){
                navigate("/analyseur")
              }else if(userdata.role=="Veilleur"){
                navigate("/veilleur")
              }else{
                 navigate("/visiteur")
              }
              
             }
          }else{
             navigate("/login")
          }
            
      }
        useEffect(()=>{
          handeluser()
        },[])
  return (
   <>
   <Routes>
    <Route path='/login' element={<Login/>}/>
    <Route path='/newUser' element={<Signup/>}/>
    <Route path='/veilleur' element={<SourcesManagement/>}/>
    <Route path='/visiteur' element={<VisiteurPage/>}/>
    <Route path='/' element={<div>home</div>}/>
    <Route path='/decideur' element={<ProfilesPage/>}/>
    <Route path='/analyseur' element={<AnalyzerReview/>}/>
   </Routes>

   </>
  )
}

export default App
