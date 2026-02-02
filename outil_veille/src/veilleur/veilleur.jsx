"use client"

import { useState, useRef, useContext ,useEffect} from "react"
import Navbar from "../nav/nav" // Updated Navbar import to relative path as fallback if @ alias fails
import "./sources.css"
import { GlobalState } from "../context/context"

export default function SourcesManagement() {
  const { user, token, role, setUser, setToken, setRole } = useContext(GlobalState)
  // const token = "mock-token" // Placeholder for demonstration

  const [activeTab, setActiveTab] = useState("sources")
  const [selectedFlux, setSelectedFlux] = useState(null) // add state to track selected flux
  const [showSuccessPopup, setShowSuccessPopup] = useState(false)
  const [message,setmessage]=useState("")
  const [sources, setSources] = useState([
    {
      idFlux: 1, // Changed to idFlux to match specified attributes
      nomFlux: "TechCrunch RSS", // nomFlux
      urlFlux: "https://techcrunch.com/feed/", // urlFlux
      description: "Actualités technologiques et startups",
      statut: "actif",
      dateAjout: "2025-01-15T10:30:00", // dateAjout
      derniereMaj: "2025-01-20T14:22:00", // derniereMaj
    },
    {
      idFlux: 2,
      nomFlux: "BBC News Technology",
      urlFlux: "https://feeds.bbci.co.uk/news/technology/rss.xml",
      description: "Actualités technologiques BBC",
      statut: "actif",
      dateAjout: "2025-01-10T09:15:00",
      derniereMaj: "2025-01-20T13:45:00",
    },
    {
      idFlux: 3,
      nomFlux: "Le Monde Économie",
      urlFlux: "https://www.lemonde.fr/economie/rss_full.xml",
      description: "Actualités économiques françaises",
      statut: "inactif",
      dateAjout: "2025-01-12T11:20:00",
      derniereMaj: "2025-01-20T12:10:00",
    },
  ])

  const [articles, setArticles] = useState([])
   

  const [selectedArticle, setSelectedArticle] = useState(null)

  const [keywords, setKeywords] = useState([
    {
      idMotCle: 1, // Specified attributes
      mot: "Cybersécurité",
      categorie: "Technologie",
      actif: true,
      dateAjout: "2025-01-18T09:00:00",
    },
    {
      idMotCle: 2,
      mot: "Intelligence Artificielle",
      categorie: "IA",
      actif: true,
      dateAjout: "2025-01-19T10:30:00",
    },
    {
      idMotCle: 3,
      mot: "RGPD",
      categorie: "Légal",
      actif: false,
      dateAjout: "2025-01-15T08:45:00",
    },
  ])

  const [showAddModal, setShowAddModal] = useState(false)
  const [showDetailsModal, setShowDetailsModal] = useState(false)
  const [selectedSource, setSelectedSource] = useState(null)
  const [newSourceUrl, setNewSourceUrl] = useState({
    nomFlux: "",
    urlFlux: "",
    description: "",
  })
  const [isLoading, setIsLoading] = useState(false)
  const [searchTerm, setSearchTerm] = useState("")
  const [showKeywordModal, setShowKeywordModal] = useState(false)
  const [newKeyword, setNewKeyword] = useState({ mot: "", categorie: "" })
  const [sortBy,setsortBy]=useState("date")
  const errorRef = useRef(null)
  const errorMessageRef = useRef(null)

  const formatDate = (dateString) => {
    const date = new Date(dateString)
    return date.toLocaleDateString("fr-FR", {
      day: "numeric",
      month: "long",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    })
  }

  const handleAddSource = async (e) => {
    e.preventDefault()
    setIsLoading(true)

    try {
      const response = await fetch("http://localhost:8080/api/flux-rss", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          nomFlux: newSourceUrl.nomFlux,
          urlFlux: newSourceUrl.urlFlux,
          description: newSourceUrl.description,
        }),
      })

      if (response.ok) {
        // getflux() would be called here
        setNewSourceUrl({
          nomFlux: "",
          urlFlux: "",
          description: "",
        })
        setShowAddModal(false)
      } else {
        if (errorRef.current && errorMessageRef.current) {
          errorMessageRef.current.textContent = "Erreur lors de l'ajout de la source"
          errorRef.current.style.display = "flex"
        }
      }
    } catch (error) {
      setShowAddModal(false)
    } finally {
      setIsLoading(false)
    }
  }

 async function handleDeleteSource(idFlux){
     const response = await fetch(`http://localhost:8080/api/flux-rss/${idFlux}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      
      })
      if(response.ok){
        getflux()
      }
  }

  async function changerstatut(flux) {
    try {
      const response = await fetch(`http://localhost:8080/api/flux-rss/${flux.idFlux}/statut`, {
        method: "PATCH",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          oldstatut: flux.statut,
          statut: flux.statut == "actif" ? "inactif" : "actif",
        }),
      })
      if (response.ok) {
        // getflux() would be called here
        setSources(
          sources.map((s) =>
            s.idFlux === flux.idFlux ? { ...s, statut: s.statut === "actif" ? "inactif" : "actif" } : s,
          ),
        )
      }
    } catch (error) {
      console.log(error)
    }
  }

  const toggleKeywordStatus = (idMotCle) => {
    setKeywords(keywords.map((kw) => (kw.idMotCle === idMotCle ? { ...kw, actif: !kw.actif } : kw)))
  }

  async function handleDeleteKeyword (idMotCle) {
     const response = await fetch(`http://localhost:8080/api/mots-cles/${idFlux}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      if(response.ok){
        getkeyword()
      }
  }
async function handleAddKeyword(e) {
    e.preventDefault()
    const kw = {
      idMotCle: keywords.length + 1,
      mot: newKeyword.mot,
      categorie: newKeyword.categorie,
      actif: true,
      dateAjout: new Date().toISOString(),
    }

    const response = await fetch("http://localhost:8080/api/mots-cles", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          mot:newKeyword.mot,
          categorie:newKeyword.categorie
        }),
      })
      if(response.ok){
        getkeyword()
      }
    setNewKeyword({ mot: "", categorie: "" })
    setShowKeywordModal(false)
  }

  const filteredSources = sources.filter((source) => {
    const matchesSearch =
      source.nomFlux.toLowerCase().includes(searchTerm.toLowerCase()) ||
      source.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
      source.urlFlux.toLowerCase().includes(searchTerm.toLowerCase())
    return matchesSearch
  })

  let filteredArticles = articles.filter((article) => {
    const matchesSearch =
      article.article.titre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      article.article.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
      article.article.urlSource.toLowerCase().includes(searchTerm.toLowerCase())
    return matchesSearch
  })
  const getSortedArticles = (articlesToSort) => {
      const sorted = [...articlesToSort]
      if (sortBy === "date") {
        sorted.sort((a, b) => new Date(b.article.dateCollecte) - new Date(a.article.dateCollecte))
      } else if (sortBy === "score") {
        sorted.sort((a, b) => (b.scoreMatch || 0) - (a.scoreMatch || 0))
      }
      return sorted
  }
  filteredArticles=getSortedArticles(filteredArticles)
  const filteredKeywords = keywords.filter(
    (kw) =>
      kw.mot.toLowerCase().includes(searchTerm.toLowerCase()) ||
      kw.categorie.toLowerCase().includes(searchTerm.toLowerCase()),
  )
  async function getflux() {
    try{
      let response=await fetch(`http://localhost:8080/api/flux-rss/mes-flux`,{
        method:"GET",
        headers:{
           Authorization: `Bearer ${token}`,
        }
      })
      if(response.ok){
        let data=await response.json()
        console.log(data);
        setSources([...data])
      }

    }catch(error){
      console.log(error)

    }
    
  }
  async function getkeyword() {
    try{
      let response=await fetch(`http://localhost:8080/api/mots-cles/mes-mots-cles`,{
        method:"GET",
        headers:{
           Authorization: `Bearer ${token}`,
        }
      })
      if(response.ok){
        let data=await response.json()
        console.log(data);
        setKeywords([...data])
      }

    }catch(error){
      console.log(error)

    }
    
  }
  async function lancerCollecte() {
    let res=await fetch(`http://localhost:8080/api/veille/collecter`,{
       method:"POST",
       headers:{
           Authorization: `Bearer ${token}`,
        }
    })
    if(res.ok){
       
        setmessage("collect commancer avec succes")
        setShowSuccessPopup(true)
        setTimeout(() => {
            setShowSuccessPopup(false)
        }, 2000)
    }
  }
  async function filltrearticles() {
      let res2=await fetch(`http://localhost:8080/api/veille/filtrer`,{
        method:"POST",
        headers:{
           Authorization: `Bearer ${token}`,
        }
    })
     if(res2.ok){
       
        setmessage("filltrage commancer avec succes")
        setShowSuccessPopup(true)
        setTimeout(() => {
            setShowSuccessPopup(false)
        }, 2000)
     }
  }
  async function getArticles(idFlux) {
    try{

   let response=await fetch(`http://localhost:8080/api/articles/flux/${idFlux}`,{
        method:"GET",
        headers:{
           Authorization: `Bearer ${token}`,
        }
      })
      if(response.ok){
        let data=await response.json()
       
        setArticles([...data])
      }
    }catch(error){
      console.log(error)
    }
  }
  async function getfiltres(idFlux) {
    try{
        let response=await fetch(`http://localhost:8080/api/filtres/flux/${idFlux}`,{
        method:"GET",
        headers:{
           Authorization: `Bearer ${token}`,
        }
      })
      if(response.ok){
        let data=await response.json()
        setArticles([...data.filtres])
        console.log(data);
        
      }

    }catch(error){
      console.log(error)
    }
  }
  useEffect(()=>{
      if(user){
        getflux()
        getkeyword()
      }
    },[user])
    useEffect(()=>{
        if(selectedFlux){
        
         getfiltres(selectedFlux)
        }
      },[selectedFlux])

  return (
    <div className="sources-page">
      <Navbar prenom="Jean" nom="Dupont" role="Veilleur" />

      {/* Main Content */}
      <main className="sources-main">
        <div className="sources-container">
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

          {/* Tabs navigation */}
          <div className="tabs-container">
            <button
              className={`tab-btn ${activeTab === "sources" ? "active" : ""}`}
              onClick={() => setActiveTab("sources")}
            >
              Sources RSS
            </button>
            {(selectedFlux)?
             <button
              className={`tab-btn ${activeTab === "articles" ? "active" : ""}`}
              onClick={() => {
                setActiveTab("articles")
                setSelectedArticle(null)
              }}
            >
              Articles
            </button>
            :null}
        
            <button
              className={`tab-btn ${activeTab === "keywords" ? "active" : ""}`}
              onClick={() => setActiveTab("keywords")}
            >
              Mots-clés
            </button>
          </div>

          {/* Filters and Actions */}
          <div className="sources-controls">
            <div className="controls-left">
              <h2 className="page-title">
                {activeTab === "sources"
                  ? "Gestion des Sources"
                  : activeTab === "articles"
                    ? ` Articles de ${sources.find((s) => s.idFlux === selectedFlux)?.nomFlux || "Flux supprimé"}`
                    : "Gestion des Mots-clés"}
              </h2>
              <p className="page-subtitle">
                {activeTab === "sources"
                  ? "Gérez vos flux RSS et sources de veille"
                  : activeTab === "articles"
                    ? "Consultez les articles collectés depuis vos sources RSS"
                    : "Gérez les mots-clés utilisés pour la veille automatique"}
              </p>
            </div>
            {activeTab !== "articles" && (
              <>
              <button
                className={(activeTab == "keywords")?"add-key-btn":"add-source-btn"}
                onClick={() => (activeTab === "sources" ? setShowAddModal(true) : setShowKeywordModal(true))}
              >
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" />
                </svg>
                <span>{activeTab === "sources" ? "Ajouter une source" : "Ajouter un mot-clé"}</span>
              </button>
              {activeTab == "sources"?
              <>
                 <button
              className="lance"
              onClick={() => lancerCollecte()}
            >
              
              <span>Lancer la collect de articles</span>
            </button>
               <button
              className="lance"
              onClick={() => filltrearticles()}
            >
              
              <span>Lancer le filltrage</span>
            </button>
            </>
              :null}
              </>
            )}
          </div>

          {/* Search Bar */}
          <div className="search-filter-section">
            <div className="search-bar">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5S13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z" />
              </svg>
              
              <input
                type="text"
                placeholder={
                  activeTab === "sources"
                    ? "Rechercher une source..."
                    : activeTab === "articles"
                      ? "Rechercher un article..."
                      : "Rechercher un mot-clé..."
                }
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
              {activeTab === "articles" && (
              <div className="sort-filter">
                <label htmlFor="sort-select">Trier par:</label>
                <select
                  id="sort-select"
                  value={sortBy}
                  onChange={(e) => setsortBy(e.target.value)}
                  className="sort-select"
                >
                  <option value="date">Date de collecte</option>
                  <option value="score">Score</option>
                </select>
              </div>
            )}
          </div>
        
          {activeTab === "sources" ? (
            <div className="sources-table-wrapper">
              <table className="sources-table">
                <thead>
                  <tr>
                    <th>Nom du flux</th>
                    <th>URL</th>
                  
                    <th>Statut</th>
                    <th>Date d'ajout</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredSources.map((source) => (
                    <tr key={source.idFlux}>
                      <td className="source-name">
                        <div className="source-name-content">
                          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                            <path d="M6.18 15.64a2.18 2.18 0 0 1 2.18 2.18C8.36 19 7.38 20 6.18 20C5 20 4 19 4 17.82a2.18 2.18 0 0 1 2.18-2.18M4 4.44A15.56 15.56 0 0 1 19.56 20h-2.83A12.73 12.73 0 0 0 4 7.27V4.44m0 5.66a9.9 9.9 0 0 1 9.9 9.9h-2.83A7.07 7.07 0 0 0 4 12.93V10.1z" />
                          </svg>
                          <span
                            onClick={() => {
                              setSelectedFlux(source.idFlux)
                              setActiveTab("articles")
                            }}
                            className="flux-name-clickable"
                            style={{ cursor: "pointer", color: "#0066cc", textDecoration: "underline" }}
                          >
                            {source.nomFlux}
                          </span>
                        </div>
                      </td>
                      <td className="source-url">
                        <a href={source.urlFlux} target="_blank" rel="noopener noreferrer">
                          {source.urlFlux}
                        </a>
                      </td>
                    
                      <td>
                        <button
                          className={`status-badge ${source.statut}`}
                          onClick={() => changerstatut(source)}
                          title="Cliquer pour changer le statut"
                        >
                          {source.statut}
                        </button>
                      </td>
                      <td className="source-date">{formatDate(source.dateAjout)}</td>
                      <td className="source-actions">
                       <button
                          className="action-btn view-btn"
                          title="Voir les détails"
                          onClick={() => {
                            setSelectedSource(source)
                            setShowDetailsModal(true)
                          }}
                        >
                          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                            <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" />
                          </svg>
                        </button>
                        <button
                          className="action-btn delete-btn"
                          title="Supprimer"
                          onClick={() => handleDeleteSource(source.idFlux)}
                        >
                          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                            <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" />
                          </svg>
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>

              {filteredSources.length === 0 && (
                <div className="empty-state">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z" />
                  </svg>
                  <p>Aucune source trouvée</p>
                  <span>Ajoutez une source pour commencer</span>
                </div>
              )}
            </div>
          ) : activeTab === "articles" ? (
            <div className="articles-container">
              {selectedArticle ? (
                <div className="article-detail">
                  <button className="back-btn" onClick={() => setSelectedArticle(null)}>
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z" />
                    </svg>
                    <span>Retour</span>
                  </button>
                  <div className="article-detail-content">
                    <div className="article-detail-header">
                      <h1>{selectedArticle.titre}</h1>
                      <div className="article-meta">
                        <span className="article-date">
                          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8zm3.5-9c.83 0 1.5-.67 1.5-1.5S16.33 8 15.5 8 14 8.67 14 9.5s.67 1.5 1.5 1.5zm-7 0c.83 0 1.5-.67 1.5-1.5S9.33 8 8.5 8 7 8.67 7 9.5 7.67 11 8.5 11zm3.5 6.5c2.33 0 4.31-1.46 5.11-3.5H6.89c.8 2.04 2.78 3.5 5.11 3.5z" />
                          </svg>
                          Publié le {formatDate(selectedArticle.datePub)}
                        </span>
                        <span className="article-collected">
                          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8zm3.5-9c.83 0 1.5-.67 1.5-1.5S16.33 8 15.5 8 14 8.67 14 9.5s.67 1.5 1.5 1.5zm-7 0c.83 0 1.5-.67 1.5-1.5S9.33 8 8.5 8 7 8.67 7 9.5 7.67 11 8.5 11zm3.5 6.5c2.33 0 4.31-1.46 5.11-3.5H6.89c.8 2.04 2.78 3.5 5.11 3.5z" />
                          </svg>
                          Collecté le {formatDate(selectedArticle.dateCollecte)}
                        </span>
                      </div>
                    </div>

                    <div className="article-detail-body">
                      <h2>Description</h2>
                      <p>{selectedArticle.description}</p>

                      <h2>Informations supplémentaires</h2>
                      <div className="article-info-grid">
                        <div className="info-item">
                          <label>ID Article:</label>
                          <span>{selectedArticle.idArticle}</span>
                        </div>
                        <div className="info-item">
                          <label>Source du flux:</label>
                          <span>
                            {sources.find((s) => s.idFlux === selectedArticle.fluxRss.idFlux)?.nomFlux || "Flux supprimé"}
                          </span>
                        </div>
                        <div className="info-item full-width">
                          <label>URL Source:</label>
                          <a
                            href={selectedArticle.urlSource}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="source-link"
                          >
                            {selectedArticle.urlSource}
                          </a>
                        </div>
                      </div>
                    </div>

                    <div className="article-detail-actions">
                      <a
                        href={selectedArticle.urlSource}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="read-btn"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                          <path d="M19 19H5V5h7V3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2v-7h-2v7z" />
                        </svg>
                        <span>Lire l'article complet</span>
                      </a>
                    </div>
                  </div>
                </div>
              ) : (
                <div className="articles-grid">
                  {selectedFlux ? (
                    <>
                      <div className="articles-flux-header">
                        <button className="back-btn" onClick={() => {setSelectedFlux(null);setActiveTab("sources")}}>
                          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                            <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z" />
                          </svg>
                          <span>Retour aux sources</span>
                        </button>
                      
                      </div>
                      <div className="articles-table-wrapper">
                        <table className="articles-table">
                          <thead>
                            <tr>
                              <th>Titre</th>
                              <th>Source</th>
                              <th>Score</th>
                              <th>Date collectée</th>
                            </tr>
                          </thead>
                          <tbody>
                            {filteredArticles
                              .filter((article) => article.article.fluxRss.idFlux === selectedFlux)
                              .map((article) => (
                                <tr
                                  key={article.article.idArticle}
                                  className="article-row"
                                  onClick={() => setSelectedArticle(article.article)}
                                >
                                  <td className="article-title">{article.article.titre}</td>
                                  <td className="article-source-url">
                                    <a href={article.article.urlSource} target="_blank" rel="noopener noreferrer">
                                      {new URL(article.article.urlSource).hostname}
                                    </a>
                                  </td>
                                   <td className="article-score" >{article.scoreMatch}</td>
                                  <td className="article-date">{formatDate(article.article.dateCollecte)}</td>
                                </tr>
                              ))}
                          </tbody>
                        </table>
                      </div>
                      {filteredArticles.filter((article) => article.article.fluxRss.idFlux === selectedFlux).length === 0 && (
                        <div className="empty-state">
                          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                            <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z" />
                          </svg>
                          <p>Aucun article trouvé</p>
                          <span>Les articles de ce flux apparaîtront ici une fois collectés</span>
                        </div>
                      )}
                    </>
                  ) : (
                    <>
                      <div className="sources-selection">
                        <h3>Sélectionnez une source pour voir ses articles</h3>
                        <div className="sources-cards">
                          {filteredSources.map((source) => (
                            <div
                              key={source.idFlux}
                              className="source-card"
                              onClick={() => setSelectedFlux(source.idFlux)}
                            >
                              <div className="source-card-header">
                                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                                  <path d="M6.18 15.64a2.18 2.18 0 0 1 2.18 2.18C8.36 19 7.38 20 6.18 20C5 20 4 19 4 17.82a2.18 2.18 0 0 1 2.18-2.18M4 4.44A15.56 15.56 0 0 1 19.56 20h-2.83A12.73 12.73 0 0 0 4 7.27V4.44m0 5.66a9.9 9.9 0 0 1 9.9 9.9h-2.83A7.07 7.07 0 0 0 4 12.93V10.1z" />
                                </svg>
                                <h3>{source.nomFlux}</h3>
                              </div>
                              <p className="source-card-desc">{source.description}</p>
                              <div className="source-card-meta">
                               
                                <span className={`status-badge ${source.statut}`}>{source.statut}</span>
                              </div>
                            </div>
                          ))}
                        </div>
                      </div>
                    </>
                  )}
                </div>
              )}
            </div>
          ) : (
            <div className="sources-table-wrapper">
              <table className="sources-table">
                <thead>
                  <tr>
                    <th>Mot-clé</th>
                    <th>Catégorie</th>
                    <th>Statut</th>
                    <th>Date d'ajout</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredKeywords.map((kw) => (
                    <tr key={kw.idMotCle}>
                      <td className="source-name">
                        <div className="source-name-content">
                          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                            <path d="M17 3H7c-1.1 0-2 .9-2 2v16l7-3 7 3V5c0-1.1-.9-2-2-2z" />
                          </svg>
                          <span>{kw.mot}</span>
                        </div>
                      </td>
                      <td>{kw.categorie}</td>
                      <td>
                        <button
                          className={`status-badge ${kw.actif ? "actif" : "inactif"}`}
                          onClick={() => toggleKeywordStatus(kw.idMotCle)}
                        >
                          {kw.actif ? "Actif" : "Inactif"}
                        </button>
                      </td>
                      <td className="source-date">{formatDate(kw.dateAjout)}</td>
                      <td className="source-actions">
                        <button className="action-btn delete-btn" onClick={() => handleDeleteKeyword(kw.idMotCle)}>
                          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                            <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" />
                          </svg>
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {filteredKeywords.length === 0 && (
                <div className="empty-state">
                  <p>Aucun mot-clé trouvé</p>
                </div>
              )}
            </div>
          )}
        </div>
      </main>

      {/* Add Source Modal */}
      {showAddModal && (
        <div className="modal-overlay" onClick={() => setShowAddModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Ajouter une nouvelle source</h3>
              <button className="modal-close" onClick={() => setShowAddModal(false)}>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
                </svg>
              </button>
            </div>
            <form onSubmit={handleAddSource} className="modal-form">
              <div className="form-group">
                <label htmlFor="sourceUrl">URL du flux RSS</label>
                <input
                  id="sourceUrl"
                  type="url"
                  placeholder="https://exemple.com/feed.xml"
                  value={newSourceUrl.urlFlux}
                  onChange={(e) => setNewSourceUrl({ ...newSourceUrl, urlFlux: e.target.value })}
                  required
                />
                <p className="form-hint">Entrez l'URL complète du flux RSS ou de la source</p>
              </div>
              <div className="form-group">
                <label htmlFor="sourceName">Nom du flux</label>
                <input
                  id="sourceName"
                  type="text"
                  placeholder="Nom du flux"
                  value={newSourceUrl.nomFlux}
                  onChange={(e) => setNewSourceUrl({ ...newSourceUrl, nomFlux: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="sourceDescription">Description</label>
                <input
                  id="sourceDescription"
                  type="text"
                  placeholder="Description"
                  value={newSourceUrl.description}
                  onChange={(e) => setNewSourceUrl({ ...newSourceUrl, description: e.target.value })}
                  required
                />
              </div>
              <div className="modal-actions">
                <button type="button" className="cancel-btn" onClick={() => setShowAddModal(false)}>
                  Annuler
                </button>
                <button type="submit" className="submit-btn" disabled={isLoading}>
                  {isLoading ? (
                    <>
                      <span className="spinner"></span>
                      <span>Ajout en cours...</span>
                    </>
                  ) : (
                    <>
                      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" />
                      </svg>
                      <span>Ajouter</span>
                    </>
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showKeywordModal && (
        <div className="modal-overlay" onClick={() => setShowKeywordModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Ajouter un mot-clé</h3>
              <button className="modal-close" onClick={() => setShowKeywordModal(false)}>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
                </svg>
              </button>
            </div>
            <form onSubmit={handleAddKeyword} className="modal-form">
              <div className="form-group">
                <label>Mot-clé</label>
                <input
                  type="text"
                  placeholder="Ex: Cybersécurité"
                  value={newKeyword.mot}
                  onChange={(e) => setNewKeyword({ ...newKeyword, mot: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Catégorie</label>
                <input
                  type="text"
                  placeholder="Ex: Technologie"
                  value={newKeyword.categorie}
                  onChange={(e) => setNewKeyword({ ...newKeyword, categorie: e.target.value })}
                  required
                />
              </div>
              <div className="modal-actions">
                <button type="button" className="cancel-btn" onClick={() => setShowKeywordModal(false)}>
                  Annuler
                </button>
                <button type="submit" className="submit-btn">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" />
                  </svg>
                  <span>Ajouter</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
      {showDetailsModal && selectedSource && (
        <div className="modal-overlay" onClick={() => setShowDetailsModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{selectedSource.nomFlux}</h2>
              <button className="modal-close" onClick={() => setShowDetailsModal(false)}>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
                </svg>
              </button>
            </div>
            <div className="modal-body">
              <div className="detail-row">
                <span className="detail-label">URL:</span>
                <a href={selectedSource.urlFlux} target="_blank" rel="noopener noreferrer" className="detail-link">
                  {selectedSource.urlFlux}
                </a>
              </div>
              <div className="detail-row">
                <span className="detail-label">Description:</span>
                <p className="detail-description">{selectedSource.description}</p>
              </div>
              <div className="detail-row">
                <span className="detail-label">Statut:</span>
                <span className={`status-badge ${selectedSource.statut}`}>{selectedSource.statut}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Date d'ajout:</span>
                <span className="detail-value">{formatDate(selectedSource.dateAjout)}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Dernière mise à jour:</span>
                <span className="detail-value">{formatDate(selectedSource.derniereMaj)}</span>
              </div>
            </div>
            <div className="modal-footer">
              <button className="cancel-btn" onClick={() => setShowDetailsModal(false)}>
                Fermer
              </button>
            </div>
          </div>
        </div>
      )}
      {showSuccessPopup && (
        <div className="success-popup">
          <div className="success-content">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" className="success-icon">
              <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" />
            </svg>
            <p>{message}</p>
          </div>
        </div>
      )}
    </div>
  )
}
