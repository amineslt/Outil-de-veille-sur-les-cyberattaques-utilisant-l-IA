"use client"

import { useState, useRef } from "react"
import "./signup.css"
import { useNavigate } from "react-router-dom"
export default function Signup() {
  const [prenom, setPrenom] = useState("")
  const [nom, setNom] = useState("")
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [confirmPassword, setConfirmPassword] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const errorRef = useRef(null)
  const errorMessageRef = useRef(null)
  const navigate=useNavigate()
  async function handleSignUp(e) {
    e.preventDefault()

    // Validate password match
    if (password !== confirmPassword) {
      if (errorRef.current && errorMessageRef.current) {
        errorMessageRef.current.textContent = "Les mots de passe ne correspondent pas"
        errorRef.current.style.display = "flex"
      }
      return
    }

    setIsLoading(true)

    try {
      const res = await fetch("http://localhost:8080/api/utilisateurs/inscription", {
        method: "Post",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          nom:nom,
          prenom:prenom,
          email:email,
          motDePasse:password,
          role:"Visiteur",
        }),
      })


      if (  res.ok) {
        console.log("Registration successful:")
        // Redirect to login page
        navigate("/login")
      } else {
        if (errorRef.current && errorMessageRef.current) {
          errorMessageRef.current.textContent = data.message || "Une erreur est survenue lors de l'inscription"
          errorRef.current.style.display = "flex"
        }
      }
    } catch (error) {
      if (errorRef.current && errorMessageRef.current) {
        errorMessageRef.current.textContent = "Erreur de connexion au serveur"
        errorRef.current.style.display = "flex"
      }
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="signup-container">
      <div className="signup-content">
        {/* Left side - Branding */}
        <div className="signup-branding">
          <div className="branding-content">
            <div className="brand-icon">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z" />
              </svg>
            </div>
            <h1 className="brand-title">Rejoignez-nous</h1>
            <p className="brand-subtitle">Créez votre compte et accédez à notre plateforme de veille intelligente</p>
            <div className="features-list">
              <div className="feature-item">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" />
                </svg>
                <span>Accès complet aux outils</span>
              </div>
              <div className="feature-item">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" />
                </svg>
                <span>Tableau de bord personnalisé</span>
              </div>
              <div className="feature-item">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" />
                </svg>
                <span>Support dédié</span>
              </div>
            </div>
          </div>
        </div>

        {/* Right side - Signup Form */}
        <div className="signup-form-section">
          <div className="signup-form-container">
            <div className="form-header">
              <h2>Créer un compte</h2>
              <p>Remplissez les informations ci-dessous</p>
            </div>

            {/* Error message */}
            <div className="error-message" ref={errorRef}>
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" />
              </svg>
              <span ref={errorMessageRef}>Une erreur est survenue</span>
              <button
                onClick={() => errorRef.current && (errorRef.current.style.display = "none")}
                className="error-close"
              >
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
                </svg>
              </button>
            </div>

            <form onSubmit={handleSignUp} className="signup-form">
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="prenom">Prénom</label>
                  <div className="input-wrapper">
                  
                    <input
                      id="prenom"
                      type="text"
                      placeholder="Votre prénom"
                      value={prenom}
                      onChange={(e) => setPrenom(e.target.value)}
                      required
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="nom">Nom</label>
                  <div className="input-wrapper">
            
                    <input
                      id="nom"
                      type="text"
                      placeholder="Votre nom"
                      value={nom}
                      onChange={(e) => setNom(e.target.value)}
                      required
                    />
                  </div>
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="email">Email</label>
                <div className="input-wrapper">
               
                  <input
                    id="email"
                    type="email"
                    placeholder="votre.email@exemple.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
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
                    minLength="6"
                  />
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="confirmPassword">Confirmer le mot de passe</label>
                <div className="input-wrapper">
               
                  <input
                    id="confirmPassword"
                    type="password"
                    placeholder="Confirmez votre mot de passe"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                    minLength="6"
                  />
                </div>
              </div>

              <button type="submit" className="submit-button" disabled={isLoading}>
                {isLoading ? (
                  <>
                    <span className="spinner"></span>
                    <span>Création en cours...</span>
                  </>
                ) : (
                  <>
                    <span>Créer mon compte</span>
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M12 4l-1.41 1.41L16.17 11H4v2h12.17l-5.58 5.59L12 20l8-8z" />
                    </svg>
                  </>
                )}
              </button>

              <div className="login-link">
                <span>Vous avez déjà un compte ?</span>
                <button onClick={()=>navigate("/login")}>Se connecter</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  )
}
