"use client"
import { useContext } from "react"
import { useNavigate } from "react-router-dom"
import "./nav.css"
import { GlobalState } from "../context/context"

export default function Navbar() {
  const router = useNavigate()
  let {user,token,role,setuser,settoken,setrole}=useContext(GlobalState);
  const handleSignOut = () => {
   setuser(null)
   setrole("")
   settoken("")
   router("/login")
  }

  return (
  <header className="main-navbar">
      <div className="navbar-container">
        <div className="navbar-brand-section">
          <div className="brand-logo">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z" />
            </svg>
          </div>
          <h1 className="brand-text">CyberAI</h1>
        </div>

        <div className="navbar-spacer" />

        <div className="navbar-user-section">
          <div className="user-profile-info">
            <div className="user-details">
              <span className="user-fullname"> {user?user.prenom:""} {user?user.nom:""}</span>
              {role && <span className="user-role-tag">{role}</span>}
            </div>
          </div>
          <button className="signout-btn" onClick={handleSignOut} title="Se déconnecter">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
              <path d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z" />
            </svg>
          </button>
        </div>
      </div>
    </header>
  )
}
