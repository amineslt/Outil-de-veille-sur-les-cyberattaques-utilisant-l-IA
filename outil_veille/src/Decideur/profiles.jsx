"use client"

import { useState,useEffect ,useContext} from "react"
import Navbar from "../nav/nav"
import "./profiles.css"
import { GlobalState } from "../context/context"
export default function ProfilesPage() {
  let {user,token,role,setuser,settoken,setrole}=useContext(GlobalState);
  const [profiles, setProfiles] = useState([
    {
      idUtilisateur: 1,
      prenom: "Jean",
      nom: "Dupont",
      email: "jean.dupont@example.com",
      role: "veilleur",
      dateCreation: "2024-01-15",
      
    },
    {
      idUtilisateur: 2,
      prenom: "Marie",
      nom: "Martin",
      email: "marie.martin@example.com",
      role: "analyseur",
      dateCreation: "2024-02-10",

    },
    {
      idUtilisateur: 3,
      prenom: "Pierre",
      nom: "Durand",
      email: "pierre.durand@example.com",
      role: "visiteur",
      dateCreation: "2024-03-05",
      
    },
    {
      idUtilisateur: 4,
      prenom: "Sophie",
      nom: "Bernard",
      email: "sophie.bernard@example.com",
      role: "decideur",
      dateCreation: "2024-01-20",
      
    },
  ])
  const [activeTab, setActiveTab] = useState("profiles")
  const [searchQuery, setSearchQuery] = useState("")
  const [filterRole, setFilterRole] = useState("all")
  const [editingProfileId, setEditingProfileId] = useState(null)
  const [editingRole, setEditingRole] = useState("")

  const roles = [
    { value: "Visiteur", label: "Visiteur", color: "#94a3b8" },
    { value: "Veilleur", label: "Veilleur", color: "#60a5fa" },
    { value: "Analyste", label: "Analyseur", color: "#34d399" },
    { value: "Décideur", label: "Décideur", color: "#f59e0b" },
  ]

  const filteredProfiles = profiles.filter((profile) => {
    const matchesSearch =
      profile.prenom.toLowerCase().includes(searchQuery.toLowerCase()) ||
      profile.nom.toLowerCase().includes(searchQuery.toLowerCase()) ||
      profile.email.toLowerCase().includes(searchQuery.toLowerCase())

    const matchesRole = filterRole === "all" || profile.role === filterRole

    return matchesSearch && matchesRole
  })

  const handleStartEdit = (profile) => {
    setEditingProfileId(profile.idUtilisateur)
    setEditingRole(profile.role)
  }

  const handleCancelEdit = () => {
    setEditingProfileId(null)
    setEditingRole("")
  }

  const handleSaveRole = (prf) => {
    setProfiles(profiles.map((profile) => (profile.idUtilisateur === prf.idUtilisateur ? { ...profile, role: editingRole } : profile)))
    setEditingProfileId(null)
    setEditingRole("")
  }

  const getRoleColor = (role) => {
    const roleObj = roles.find((r) => r.value === role)
    return roleObj ? roleObj.color : "#94a3b8"
  }
  async function handleSaveRole2(prf) {

        let res=await fetch(`http://localhost:8080/api/utilisateurs/${prf.idUtilisateur}/role`,{
            method:"PUT",
            headers:{
                Authorization: `Bearer ${token}`,
                 "Content-Type": "application/json"
            } ,
            body:JSON.stringify(
                {
                    role:editingRole
                }
            )
        })
        if(res.ok){
            fetchprofile()
            setEditingProfileId(null)
    setEditingRole("")
        }
        
  }
  async function fetchprofile() {
    try{
        let res=await fetch("http://localhost:8080/api/utilisateurs/profiles",{
            method:"GET",
            headers:{
                Authorization: `Bearer ${token}`,
            }
           
        })
        const data =await res.json()
        if(data){
            console.log(data)
            setProfiles([...data]);
        }

    }catch(error){
        console.log(error)
    }
  }
  async function handledeleteuser(prf) {
     let res=await fetch(`http://localhost:8080/api/utilisateurs/${prf.idUtilisateur}`,{
            method:"DELETE",
            headers:{
                Authorization: `Bearer ${token}`,
            } ,
        })
        if(res.ok){
            fetchprofile()
        }
        
  }
   const [showRequestModal, setShowRequestModal] = useState(false)
 const [selectedRapport, setSelectedRapport] = useState(null)
  const [showDetailModal, setShowDetailModal] = useState(false)
  // Sample reports data
  const [rapports, setRapports] = useState([
    {
      idRapport: 1,
      titre: 'Rapport Hebdomadaire - Menaces Détectées',
      periodeDebut: '2025-01-13',
      periodeFin: '2025-01-19',
      typeRapport: 'hebdomadaire',
      dateCreation: '2025-01-20T10:30:00',
      contenu: 'Résumé des menaces détectées cette semaine incluant phishing et malware.',
      status: 'généré',
    },
    {
      idRapport: 2,
      titre: 'Rapport Mensuel - Analyse des Tendances',
      periodeDebut: '2024-12-01',
      periodeFin: '2024-12-31',
      typeRapport: 'mensuel',
      dateCreation: '2025-01-01T14:00:00',
      contenu: 'Analyse détaillée des tendances de sécurité pour décembre 2024.',
      status: 'généré',
    },
    {
      idRapport: 3,
      titre: 'Rapport Quotidien - 21 Janvier 2025',
      periodeDebut: '2025-01-21',
      periodeFin: '2025-01-21',
      typeRapport: 'quotidien',
      dateCreation: '2025-01-22T08:00:00',
      contenu: 'Rapport quotidien avec les principaux événements de sécurité.',
      status: 'généré',
    },
  ])

  const [requestForm, setRequestForm] = useState({
    titre: '',
    periodeDebut: '',
    periodeFin: '',
    typeRapport: 'personnalisé',
    niveauRisqueMin: 'moyen',
    typeAttaque: '',
    limitArticles: 10,
  })

  const [requestError, setRequestError] = useState('')
  const [requestSuccess, setRequestSuccess] = useState(false)

  const handleRequestChange = (e) => {
    const { name, value } = e.target
    setRequestForm((prev) => ({
      ...prev,
      [name]: name === 'limitArticles' ? parseInt(value) : value,
    }))
    setRequestError('')
  }

  async function handleSubmitRequest() {
    if (!requestForm.titre || !requestForm.periodeDebut || !requestForm.periodeFin) {
      setRequestError('Veuillez remplir tous les champs obligatoires')
      return
    }

    if (new Date(requestForm.periodeDebut) > new Date(requestForm.periodeFin)) {
      setRequestError('La date de début doit être antérieure à la date de fin')
      return
    }
      try{
      let response=await fetch(`http://localhost:8080/api/rapports/generer`,{
        method:"POST",
        headers:{
           Authorization: `Bearer ${token}`,
           "Content-Type": "application/json",
        },
        body:JSON.stringify(requestForm)
      })
      if(response.ok){
        setRequestSuccess(true)
        setRequestForm({
          titre: '',
          periodeDebut: '',
          periodeFin: '',
          typeRapport: 'personnalisé',
          niveauRisqueMin: 'moyen',
          typeAttaque: '',
          limitArticles: 10,
        })

        setTimeout(() => {
          setRequestSuccess(false)
          setShowRequestModal(false)
        }, 2000)
      }

     }catch(error){
      console.log(error)
     }
  
  }

  const formatDate = (dateString) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    })
  }

  const getStatusColor = (status) => {
    switch (status) {
      case 'généré':
        return 'status-generated'
      case 'en_generation':
        return 'status-generating'
      case 'erreur':
        return 'status-error'
      default:
        return 'status-pending'
    }
  }

  const getStatusLabel = (status) => {
    switch (status) {
      case 'généré':
        return 'Généré'
      case 'en_generation':
        return 'En génération'
      case 'erreur':
        return 'Erreur'
      default:
        return 'En attente'
    }
  }
  async function fetchrapports() {
      try{
      let response=await fetch(`http://localhost:8080/api/rapports`,{
        method:"GET",
        headers:{
           Authorization: `Bearer ${token}`,
        }
      })
      if(response.ok){
        let data=await response.json()
        console.log(data)
        setRapports([...data])
      }

     }catch(error){
      console.log(error)
     }
  }
  async function handledeleteRapport(id) {
    try{
      let response=await fetch(`http://localhost:8080/api/rapports/${id}`,{
        method:"DELETE",
        headers:{
           Authorization: `Bearer ${token}`,
        }
      })
      if(response.ok){
      fetchrapports()
      }

     }catch(error){
      console.log(error)
     }
  }
  useEffect(()=>{
    if(user){
      fetchprofile()
    }
    
  },[user])
  useEffect(()=>{
    if(user && activeTab=="rapports"){
      fetchrapports()
    }
  },[activeTab])
  return (
    <div className="profiles-page">
      <Navbar prenom="Admin" nom="Décideur" role="Décideur" />

      <main className="profiles-main">
        <div className="profiles-container">
           <div className="tabs-navigation">
              <button
              className={`tab-button ${activeTab === 'profiles' ? 'active' : ''}`}
              onClick={() => setActiveTab('profiles')}
            >
              Profiles
            </button>
            <button
              className={`tab-button ${activeTab === 'rapports' ? 'active' : ''}`}
              onClick={() => setActiveTab('rapports')}
            >
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-8-6z" />
              </svg>
              Mes Rapports
            </button>
            <button
              className={`tab-button ${activeTab === 'demander' ? 'active' : ''}`}
              onClick={() => setActiveTab('demander')}
            >
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" />
              </svg>
              Demander un Rapport
            </button>
          </div>
          {(activeTab=="profiles")?
          <>
          <div className="profiles-header">
            <div className="header-content">
              <h1>Gestion des Profils</h1>
              <p className="page-subtitle">Gérez les utilisateurs et leurs rôles</p>
            </div>
            <div className="header-stats">
              <div className="stat-card">
                <span className="stat-number">{profiles.length}</span>
                <span className="stat-label">Utilisateurs</span>
              </div>
               <div className="stat-card">
                <span className="stat-number">{profiles.filter((p) => p.role === "Visiteur").length}</span>
                <span className="stat-label">Visiteur</span>
              </div>
              <div className="stat-card">
                <span className="stat-number">{profiles.filter((p) => p.role === "Veilleur").length}</span>
                <span className="stat-label">Veilleurs</span>
              </div>
              <div className="stat-card">
                <span className="stat-number">{profiles.filter((p) => p.role === "Analyste").length}</span>
                <span className="stat-label">Analyseurs</span>
              </div>
               <div className="stat-card">
                <span className="stat-number">{profiles.filter((p) => p.role === "Décideur").length}</span>
                <span className="stat-label">Décideurs</span>
              </div>
            </div>
          </div>

          <div className="search-filter-section">
            <div className="search-bar">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <circle cx="11" cy="11" r="8" />
                <path d="m21 21-4.35-4.35" />
              </svg>
              <input
                type="text"
                placeholder="Rechercher par nom, prénom ou email..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>
            <div className="role-filter">
              <label htmlFor="role-filter">Filtrer par rôle:</label>
              <select id="role-filter" value={filterRole} onChange={(e) => setFilterRole(e.target.value)}>
                <option value="all">Tous les rôles</option>
                <option value="Visiteur">Visiteur</option>
                <option value="Veilleur">Veilleur</option>
                <option value="Analyste">Analyseur</option>
                <option value="Décideur">Décideur</option>
              </select>
            </div>
          </div>

          <div className="profiles-table-wrapper">
            <table className="profiles-table">
              <thead>
                <tr>
                  <th>Utilisateur</th>
                  <th>Email</th>
                  <th>Rôle</th>
                  <th>Date d'inscription</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredProfiles.length > 0 ? (
                  filteredProfiles.map((profile) => (
                    <tr key={profile.idUtilisateur}>
                      <td>
                        <div className="user-info-cell">
                          <div className="user-avatar">
                            {profile.prenom.charAt(0)}
                            {profile.nom.charAt(0)}
                          </div>
                          <span className="user-name">
                            {profile.prenom} {profile.nom}
                          </span>
                        </div>
                      </td>
                      <td>
                        <span className="user-email">{profile.email}</span>
                      </td>
                      <td>
                        {editingProfileId === profile.idUtilisateur ? (
                          <select
                            className="role-select-edit"
                            value={editingRole}
                            onChange={(e) => setEditingRole(e.target.value)}
                          >
                            {roles.map((role) => (
                              <option key={role.value} value={role.value}>
                                {role.label}
                              </option>
                            ))}
                          </select>
                        ) : (
                          <span className="role-badge" style={{ backgroundColor: getRoleColor(profile.role) }}>
                            {roles.find((r) => r.value === profile.role)?.label}
                          </span>
                        )}
                      </td>
                      <td>
                        <span className="profile-date">
                          {new Date(profile.dateCreation).toLocaleDateString("fr-FR")}
                        </span>
                      </td>
                      <td>
                        <div className="profile-actions">
                          {editingProfileId === profile.idUtilisateur ? (
                            <>
                              <button
                                className="action-btn save-btn"
                                onClick={() => handleSaveRole2(profile)}
                                title="Enregistrer"
                              >
                                <svg
                                  xmlns="http://www.w3.org/2000/svg"
                                  viewBox="0 0 24 24"
                                  fill="none"
                                  stroke="currentColor"
                                >
                                  <polyline points="20 6 9 17 4 12" />
                                </svg>
                              </button>
                              <button className="action-btn cancel-btn" onClick={handleCancelEdit} title="Annuler">
                                <svg
                                  xmlns="http://www.w3.org/2000/svg"
                                  viewBox="0 0 24 24"
                                  fill="none"
                                  stroke="currentColor"
                                >
                                  <line x1="18" y1="6" x2="6" y2="18" />
                                  <line x1="6" y1="6" x2="18" y2="18" />
                                </svg>
                              </button>
                            </>
                          ) : (
                            <>
                            <button
                              className="action-btn edit-btn"
                              onClick={() => handleStartEdit(profile)}
                              title="Modifier le rôle"
                            >
                              <svg
                                xmlns="http://www.w3.org/2000/svg"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                              >
                                <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z" />
                              </svg>
                            </button>
                              <button
                              className="action-btn  cancel-btn"
                              onClick={() => handledeleteuser(profile)}
                              title="supprimer utilisateur"
                            >
                             <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                            <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" />
                          </svg>
                            </button>
                            </>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="6" className="empty-state">
                      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                        <circle cx="12" cy="7" r="4" />
                      </svg>
                      <p>Aucun profil trouvé</p>
                      <span>Essayez de modifier vos critères de recherche</span>
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
          </>
          :
         <div className="decideur-page">
      <main className="decideur-main">
        <div className="decideur-container">     

          {/* Rapports Tab */}
          {activeTab === 'rapports' && (
            <div className="rapports-section">
              <div className="section-header">
                <h2>Mes Rapports</h2>
                <p>{rapports.length} rapport(s) disponible(s)</p>
              </div>

              <div className="rapports-grid">
                {rapports.map((rapport) => (
                  <div
                    key={rapport.idRapport}
                    className="rapport-card"
                    onClick={() => {
                      setSelectedRapport(rapport)
                      setShowDetailModal(true)
                    }}
                  >
                    <div className="rapport-summary">
                      <h3>{rapport.titre}</h3>
                      <div className="summary-info">
                        <span className="type-badge">{rapport.typeRapport}</span>
                        <span className="period">
                          {new Date(rapport.periodeDebut).toLocaleDateString('fr-FR')} -{' '}
                          {new Date(rapport.periodeFin).toLocaleDateString('fr-FR')}
                        </span>
                      </div>
                      <div className="card-footer">
                       
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" className="arrow-icon">
                          <path d="M8.59 16.59L10 18l6-6-6-6-1.41 1.41L13.17 12z" />
                        </svg>
                      </div>
                    </div>
                  </div>
                ))}
              </div>

              {rapports.length === 0 && (
                <div className="empty-state">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-8-6z" />
                  </svg>
                  <p>Aucun rapport disponible</p>
                  <span>Demandez un rapport pour commencer</span>
                </div>
              )}
            </div>
          )}

          {/* Demander Tab */}
          {activeTab === 'demander' && (
            <div className="demander-section">
              <div className="section-header">
                <h2>Générer un Rapport</h2>
                <p>Spécifiez les paramètres pour votre rapport personnalisé</p>
              </div>

              <div className="request-form-container">
                <div className="form-group">
                  <label htmlFor="titre">Titre du rapport *</label>
                  <input
                    id="titre"
                    type="text"
                    name="titre"
                    value={requestForm.titre}
                    onChange={handleRequestChange}
                    placeholder="Ex: Rapport de menaces - Janvier 2025"
                  />
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label htmlFor="periodeDebut">Date de début *</label>
                    <input
                      id="periodeDebut"
                      type="date"
                      name="periodeDebut"
                      value={requestForm.periodeDebut}
                      onChange={handleRequestChange}
                    />
                  </div>
                  <div className="form-group">
                    <label htmlFor="periodeFin">Date de fin *</label>
                    <input
                      id="periodeFin"
                      type="date"
                      name="periodeFin"
                      value={requestForm.periodeFin}
                      onChange={handleRequestChange}
                    />
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label htmlFor="typeRapport">Type de rapport</label>
                    <select
                      id="typeRapport"
                      name="typeRapport"
                      value={requestForm.typeRapport}
                      onChange={handleRequestChange}
                    >
                      <option value="quotidien">Quotidien</option>
                      <option value="hebdomadaire">Hebdomadaire</option>
                      <option value="mensuel">Mensuel</option>
                      <option value="personnalisé">Personnalisé</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label htmlFor="niveauRisqueMin">Niveau de risque minimum</label>
                    <select
                      id="niveauRisqueMin"
                      name="niveauRisqueMin"
                      value={requestForm.niveauRisqueMin}
                      onChange={handleRequestChange}
                    >
                      <option value="faible">Faible</option>
                      <option value="moyen">Moyen</option>
                      <option value="élevé">Élevé</option>
                    </select>
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label htmlFor="typeAttaque">Type d'attaque (optionnel)</label>
                    <input
                      id="typeAttaque"
                      type="text"
                      name="typeAttaque"
                      value={requestForm.typeAttaque}
                      onChange={handleRequestChange}
                      placeholder="Ex: Phishing, Malware, etc."
                    />
                  </div>
                  <div className="form-group">
                    <label htmlFor="limitArticles">Nombre max d'articles</label>
                    <input
                      id="limitArticles"
                      type="number"
                      name="limitArticles"
                      value={requestForm.limitArticles}
                      onChange={handleRequestChange}
                      min="1"
                      max="100"
                    />
                  </div>
                </div>

                {requestError && <div className="form-error">{requestError}</div>}

                <div className="form-actions">
                  <button className="btn-submit" onClick={handleSubmitRequest}>
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" />
                    </svg>
                    Générer le rapport
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      </main>

      {/* Success Modal */}
      {requestSuccess && (
        <div className="success-popup">
          <div className="success-content">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" className="success-icon">
              <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" />
            </svg>
            <p>Rapport demandé avec succès!</p>
          </div>
        </div>
      )}
          {/* Detail Modal */}
      {showDetailModal && selectedRapport && (
        <div className="modal-overlay" onClick={() => setShowDetailModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{selectedRapport.titre}</h2>
              <button className="modal-close" onClick={() => setShowDetailModal(false)}>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
                </svg>
              </button>
            </div>

            <div className="modal-body">
              <div className="detail-row">
                <span className="detail-label">Type de rapport:</span>
                <span className="detail-value">{selectedRapport.typeRapport}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Période:</span>
                <span className="detail-value">
                  {new Date(selectedRapport.periodeDebut).toLocaleDateString('fr-FR')} -{' '}
                  {new Date(selectedRapport.periodeFin).toLocaleDateString('fr-FR')}
                </span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Date de création:</span>
                <span className="detail-value">{formatDate(selectedRapport.dateCreation)}</span>
              </div>
           
               {typeof selectedRapport.contenu === 'string' ? (
                (() => {
                  try {
                    const contenuObj = JSON.parse(selectedRapport.contenu)
                    return (
                      <div className="contenu-section">
                        {contenuObj.resume_global && (
                          <div className="detail-row full-width">
                            <span className="detail-label">Résumé Global:</span>
                            <p className="detail-description">{contenuObj.resume_global}</p>
                          </div>
                        )}

                        {contenuObj.tendances && Array.isArray(contenuObj.tendances) && (
                          <div className="detail-row full-width">
                            <span className="detail-label">Tendances:</span>
                            <ul className="tendances-list">
                              {contenuObj.tendances.map((tendance, idx) => (
                                <li key={idx}>{tendance}</li>
                              ))}
                            </ul>
                          </div>
                        )}

                        {contenuObj.recommandations && Array.isArray(contenuObj.recommandations) && (
                          <div className="detail-row full-width">
                            <span className="detail-label">Recommandations:</span>
                            <ul className="recommendations-list">
                              {contenuObj.recommandations.map((rec, idx) => (
                                <li key={idx}>{rec}</li>
                              ))}
                            </ul>
                          </div>
                        )}

                        {contenuObj.menaces_prioritaires && Array.isArray(contenuObj.menaces_prioritaires) && (
                          <div className="detail-row full-width">
                            <span className="detail-label">Menaces Prioritaires:</span>
                            <ul className="threats-list">
                              {contenuObj.menaces_prioritaires.map((menace, idx) => (
                                <li key={idx}>{menace}</li>
                              ))}
                            </ul>
                          </div>
                        )}

                        {contenuObj.niveau_alerte_global && (
                          <div className="detail-row full-width">
                            <span className="detail-label">Niveau d'Alerte Global:</span>
                            <span className={`alert-level ${contenuObj.niveau_alerte_global.toLowerCase()}`}>
                              {contenuObj.niveau_alerte_global}
                            </span>
                          </div>
                        )}

                        {contenuObj.statistiques && (
                          <div className="detail-row full-width">
                            <span className="detail-label">Statistiques:</span>
                            <div className="statistics-grid">
                              {Object.entries(contenuObj.statistiques).map(([key, value]) => (
                                <div key={key} className="stat-item">
                                  <span className="stat-label">{key.replace(/_/g, ' ')}:</span>
                                  {typeof value === 'object' ? (
                                    <div className="stat-nested">
                                      {Object.entries(value).map(([subKey, subValue]) => (
                                        <div key={subKey} className="stat-sub">
                                          {subKey}: <strong>{subValue}</strong>
                                        </div>
                                      ))}
                                    </div>
                                  ) : (
                                    <span className="stat-value">{value}</span>
                                  )}
                                </div>
                              ))}
                            </div>
                          </div>
                        )}
                      </div>
                    )
                  } catch (e) {
                    return (
                      <div className="detail-row full-width">
                        <span className="detail-label">Contenu:</span>
                        <p className="detail-description">{selectedRapport.contenu}</p>
                      </div>
                    )
                  }
                })()
              ) : (
                <div className="detail-row full-width">
                  <span className="detail-label">Contenu:</span>
                  <p className="detail-description">{JSON.stringify(selectedRapport.contenu, null, 2)}</p>
                </div>
              )}
            </div>

            <div className="modal-footer">
             
              <button className="btn-download" onClick={()=>{handledeleteRapport(selectedRapport.idRapport)}}>
                 <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                            <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" />
                          </svg>
                Supprimer
              </button>
              <button className="btn-close" onClick={() => setShowDetailModal(false)}>
                Fermer
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
          }
        </div>
      </main>
    </div>
  )
}
