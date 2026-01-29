"use client"

import { useState, useRef ,useContext} from "react"
import { useNavigate } from "react-router-dom"
import "./login.css"
import { GlobalState } from "../context/context"
export default function Login() {
  const [email, setemail] = useState("")
  const [password, setPassword] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const errorRef = useRef(null)
  const nav=useNavigate()
    let {user,token,role,setuser,settoken,setrole}=useContext(GlobalState);

  async function handleSignIn(e) {
    e.preventDefault()
    setIsLoading(true)

    try {
      const res = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email: email,
          motDePasse: password,
        }),
      })

      const data = await res.json()
    
      if (data) {
        data.utilisateur.motDePasse=password;
            console.log(data)
            localStorage.setItem("userinfo",JSON.stringify(data));
            settoken(data.token);
            setuser(data.utilisateur);
            setrole(data.role); 
            if(data.role=="Décideur"){
              nav("/decideur")
            }else if(data.role=="Analyste") {
               nav("/analyseur")
            }else if(data.role=="Veilleur"){
                nav("/veilleur")
            }else{
                 nav("/visiteur")
              }
      }
    } catch (error) {
      console.log(error)
      if (errorRef.current) {
        errorRef.current.style.display = "flex"
      }
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="login-container">
      <div className="login-content">
        {/* Left side - Branding */}
        <div className="login-branding">
          <div className="branding-content">
            <div className="brand-icon">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z" />
              </svg>
            </div>
            <h1 className="brand-title">Outil de Veille</h1>
            <p className="brand-subtitle">
              Surveillez, analysez et anticipez avec notre plateforme de veille intelligente
            </p>
            <div className="features-list">
              <div className="feature-item">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" />
                </svg>
                <span>Surveillance en temps réel</span>
              </div>
              <div className="feature-item">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" />
                </svg>
                <span>Analyses approfondies</span>
              </div>
              <div className="feature-item">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" />
                </svg>
                <span>Alertes personnalisées</span>
              </div>
            </div>
          </div>
        </div>

        {/* Right side - Login Form */}
        <div className="login-form-section">
          <div className="login-form-container">
            <div className="form-header">
              <h2>Connexion</h2>
              <p>Accédez à votre espace de veille</p>
            </div>

            {/* Error message */}
            <div className="error-message" ref={errorRef}>
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" />
              </svg>
              <span>email ou mot de passe incorrect</span>
              <button
                onClick={() => errorRef.current && (errorRef.current.style.display = "none")}
                className="error-close"
              >
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
                </svg>
              </button>
            </div>

            <form onSubmit={handleSignIn} className="login-form">
              <div className="form-group">
                <label htmlFor="email">Email</label>
                <div className="input-wrapper">
                 
                  <input
                    id="email"
                    type="email"
                    placeholder="Entrez votre nom d'utilisateur"
                    value={email}
                    onChange={(e) => setemail(e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="password">Mot de passe</label>
                <div className="input-wrapper">
                
                  <input
                    id="password"
                    type="password"
                    placeholder="Entrez votre mot de passe"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                </div>
              </div>

              <button type="submit" className="submit-button" disabled={isLoading}>
                {isLoading ? (
                  <>
                    <span className="spinner"></span>
                    <span>Connexion...</span>
                  </>
                ) : (
                  <>
                    <span>Se connecter</span>
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M12 4l-1.41 1.41L16.17 11H4v2h12.17l-5.58 5.59L12 20l8-8z" />
                    </svg>
                  </>
                )}
              </button>

              <div className="signup-link">
                <span>Vous n'avez pas de compte ?</span>
                <button onClick={()=>nav("/newUser")}>Créer un compte</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  )
}
