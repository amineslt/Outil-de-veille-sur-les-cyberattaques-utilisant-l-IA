'use client'

import { useState,useContext, useEffect } from 'react'
import Navbar from '../nav/nav'
import './analyzer.css'
import { GlobalState } from '../context/context'
export default function AnalyzerReview() {
  let {user,token,role,setuser,settoken,setrole}=useContext(GlobalState);
  const [analyses, setAnalyses] = useState([
    {
      idAnalyse: 1,
      resume: 'Campagne de phishing utilisant des techniques d\'IA pour la personnalisation avancée',
      typeAttaque: 'Phishing',
      niveauRisque: 'élevé',
      sophistication: 'élevé',
      dateAnalyse: '2025-01-15T14:30:00',
      modeleLlm: 'GPT-4',
      statut: null,
      validation: null,
    },
    {
      idAnalyse: 2,
      resume: 'Réseau de distribution automatique de malware détecté sur plusieurs canaux',
      typeAttaque: 'Malware',
      niveauRisque: 'élevé',
      sophistication: 'moyen',
      dateAnalyse: '2025-01-14T11:45:00',
      modeleLlm: 'Claude-3',
      statut: 'validé',
      validation: {
        idValidation: 1,
        commentaire: 'Analyse complète et détaillée',
        typeModif: 'Enrichissement',
        dateValid: '2025-01-18T09:30:00',
      },
    },
    {
      idAnalyse: 3,
      resume: 'Nouvelle technique d\'attaque non encore cataloguée combinant plusieurs vecteurs',
      typeAttaque: 'Attaque mixte',
      niveauRisque: 'moyen',
      sophistication: 'élevé',
      dateAnalyse: '2025-01-16T09:15:00',
      modeleLlm: 'GPT-4',
      statut: null,
      validation: null,
    },
    {
      idAnalyse: 4,
      resume: 'Campagne d\'attaque coordonnée avec plusieurs étapes d\'exploitation',
      typeAttaque: 'Attaque coordonnée',
      niveauRisque: 'élevé',
      sophistication: 'élevé',
      dateAnalyse: '2025-01-13T16:20:00',
      modeleLlm: 'Claude-3',
      statut: 'rejeté',
      validation: {
        idValidation: 2,
        commentaire: 'Manque d\'informations de source',
        typeModif: 'Suppression',
        dateValid: '2025-01-17T14:45:00',
      },
    },
    {
      idAnalyse: 5,
      resume: 'Génération automatique de variantes de malware avec polymorphisme avancé',
      typeAttaque: 'Malware',
      niveauRisque: 'élevé',
      sophistication: 'élevé',
      dateAnalyse: '2025-01-16T10:00:00',
      modeleLlm: 'GPT-4',
      statut: null,
      validation: null,
    },
  ])

  const [filterType, setFilterType] = useState('All Types')
  const [filterStatus, setFilterStatus] = useState('All Status')
  const [searchTerm, setSearchTerm] = useState('')
  const [selectedAnalyse, setSelectedAnalyse] = useState(null)
  const [showModal, setShowModal] = useState(false)
  const [commentaire, setCommentaire] = useState('')
  const [typeModif, setTypeModif] = useState('Enrichissement')
  const [editedResume, setEditedResume] = useState('')
  const [editedSophistication, setEditedSophistication] = useState('')
  const [editedNiveauRisque, setEditedNiveauRisque] = useState('')
  const [isEditing, setIsEditing] = useState(false)
  const [showSuccessPopup, setShowSuccessPopup] = useState(false)
  const formatDate = (dateString) => {
    if (!dateString) return 'N/A'
    const date = new Date(dateString)
    return date.toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    })
  }

  const getRiskColor = (niveauRisque) => {
    switch (niveauRisque) {
      case 'élevé':
        return 'severity-critical'
      case 'moyen':
        return 'severity-medium'
      case 'faible':
        return 'severity-low'
      default:
        return 'severity-low'
    }
  }
   const getSortedanalyses = (articlesToSort) => {
      const sorted = [...articlesToSort]
      sorted.sort((a, b) => new Date(b.dateAnalyse) - new Date(a.dateAnalyse))
      return sorted
  }

  const totalAnalyses = analyses.length
  const pendingAnalyses = analyses.filter((a) => !a.statut).length
  const validatedAnalyses = analyses.filter((a) => a.statut == 'validé').length
  const rejectedAnalyses = analyses.filter((a) => a.statut == 'rejeté').length

  const types = ['All Types', ...new Set(analyses.map((a) => a.typeAttaque))]
  const statuses = ['All Status', 'En attente', 'validé', 'rejeté']

  let filteredAnalyses = analyses.filter((analyse) => {
    const matchType = filterType === 'All Types' || analyse.typeAttaque === filterType
    const matchStatus =
      filterStatus === 'All Status' ||
      (filterStatus === 'En attente' && !analyse.statut) ||
      analyse.statut === filterStatus
    const matchSearch =
      analyse.resume.toLowerCase().includes(searchTerm.toLowerCase()) ||
      analyse.typeAttaque.toLowerCase().includes(searchTerm.toLowerCase()) ||
      analyse.idAnalyse.toString().includes(searchTerm)
    return matchType && matchStatus && matchSearch
  })
  filteredAnalyses=getSortedanalyses(filteredAnalyses)

  async function handleValidate  (analyse,status) {
     try{
      let response=await fetch(`http://localhost:8080/api/validations/analyse/${analyse.idAnalyse}`,{
        method:"POST",
        headers:{
           Authorization: `Bearer ${token}`,
           "Content-Type": "application/json",
        },
        body:JSON.stringify({
            statut:status,
            commentaire: commentaire||analyse.validation.commentaire,
            typeModif: typeModif|| analyse.validation.typeModif,
            nouveauTypeAttaque:analyse.typeAttaque,
            nouveauResume: editedResume ||analyse.resume,
            nouveauNiveauRisque:editedNiveauRisque ||analyse.niveauRisque,
            nouvelleSophistication: editedSophistication|| analyse.sophistication
        })
      })
      if(response.ok){
        resetModal()
        getanalysts()

      }

    }catch(error){
      console.log(error)

    }
    setSelectedAnalyse(null)
    setShowModal(false)
    setCommentaire('')
  }

 const resetModal = () => {
    setSelectedAnalyse(null)
    setShowModal(false)
    setIsEditing(false)
    setCommentaire('')
    setEditedResume('')
    setEditedSophistication('')
    setEditedNiveauRisque('')
  }
   const startEditing = () => {
    setEditedResume(selectedAnalyse.resume)
    setIsEditing(true)
  }

  const cancelEditing = () => {
    setIsEditing(false)
    setEditedResume(selectedAnalyse.resume)
    setEditedSophistication(selectedAnalyse.sophistication)
    setEditedNiveauRisque(selectedAnalyse.niveauRisque)
  }

  const openDetails = (analyse) => {
    setSelectedAnalyse(analyse)
    setCommentaire('')
    setTypeModif('Enrichissement')
    setShowModal(true)
  }

  const getSeverityColor = (severite) => {
    switch (severite) {
      case 'critique':
        return 'severity-critical'
      case 'haute':
        return 'severity-high'
      case 'moyenne':
        return 'severity-medium'
      default:
        return 'severity-low'
    }
  }



  async function getanalysts() {
     try{
      let response=await fetch(`http://localhost:8080/api/analyses`,{
        method:"GET",
        headers:{
           Authorization: `Bearer ${token}`,
        }
      })
      if(response.ok){
        let data=await response.json()
        
        let strdata=data.map((analyse)=>analyse?{...analyse,statut:(analyse.validation)?analyse.validation.statut:null}:null)
        console.log(strdata)
        setAnalyses([...strdata])
      }

    }catch(error){
      console.log(error)

    }
  }
  async function lanceranalyse(){
     try{
      let response=await fetch(`http://localhost:8080/api/veille/analyser`,{
        method:"POST",
        headers:{
           Authorization: `Bearer ${token}`,
        }
      })
      if(response.ok){
        getanalysts()
        setShowSuccessPopup(true)
        setTimeout(() => {
            setShowSuccessPopup(false)
            resetModal()
        }, 2000)
      }

    }catch(error){
      console.log(error)

    }
  }
  useEffect(()=>{
    if(user){
      getanalysts();
    }
  },[user])

  return (
    <div className="analyzer-page">
      <Navbar prenom="Paul" nom="Leclerc" role="Analyseur" />

      <main className="analyzer-main">
        <div className="analyzer-container">
          <div className="page-header">
            <h1>Examen des Rapports d'Attaque</h1>
            <p>Validez ou rejetez les rapports d'analyse des attaques détectées</p>
          </div>

          {/* Summary Cards */}
          <div className="summary-cards">
            <div className="card card-total">
              <div className="card-content">
                <span className="card-label">Total des analyses</span>
                <span className="card-value">{totalAnalyses}</span>
              </div>
              <div className="card-icon">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-8-6z" />
                </svg>
              </div>
            </div>

            <div className="card card-pending">
              <div className="card-content">
                <span className="card-label">En attente de révision</span>
                <span className="card-value">{pendingAnalyses}</span>
              </div>
              <div className="card-icon">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm3.5-9c.83 0 1.5-.67 1.5-1.5S16.33 8 15.5 8 14 8.67 14 9.5s.67 1.5 1.5 1.5zm-7 0c.83 0 1.5-.67 1.5-1.5S9.33 8 8.5 8 7 8.67 7 9.5 7.67 11 8.5 11zm3.5 6.5c2.33 0 4.31-1.46 5.11-3.5H6.89c.8 2.04 2.78 3.5 5.11 3.5z" />
                </svg>
              </div>
            </div>

            <div className="card card-validated">
              <div className="card-content">
                <span className="card-label">Validés</span>
                <span className="card-value">{validatedAnalyses}</span>
              </div>
              <div className="card-icon">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" />
                </svg>
              </div>
            </div>

            <div className="card card-rejected">
              <div className="card-content">
                <span className="card-label">Rejetés</span>
                <span className="card-value">{rejectedAnalyses}</span>
              </div>
              <div className="card-icon">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
                </svg>
              </div>
            </div>
          </div>

          {/* Filters Section */}
          <div className="filters-section">
            <div className="filter-group">
              <label htmlFor="typeFilter">Filtrer par type d'attaque :</label>
              <select id="typeFilter" value={filterType} onChange={(e) => setFilterType(e.target.value)}>
                {types.map((type) => (
                  <option key={type} value={type}>
                    {type}
                  </option>
                ))}
              </select>
            </div>

            <div className="filter-group">
              <label htmlFor="statusFilter">Filtrer par statut :</label>
              <select id="statusFilter" value={filterStatus} onChange={(e) => setFilterStatus(e.target.value)}>
                {statuses.map((status) => (
                  <option key={status} value={status}>
                    {status === 'All Status' ? 'Tous les statuts' : status}
                  </option>
                ))}
              </select>
            </div>

            <div className="search-box">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5S13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z" />
              </svg>
              <input
                type="text"
                placeholder="Rechercher par type, ID ou résumé..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            <div className='filter-group'>
                <button onClick={()=>{lanceranalyse()}} className='view-details-btn'>
                  Lancer l'analyse
                </button>
            </div>
            
          </div>

          {/* Reports Table */}
          <div className="reports-table-wrapper">
            <table className="reports-table">
              <thead>
                <tr>
                  <th>ID ANALYSE</th>
                 
                  <th>STATUT</th>
                  <th>TYPE D'ATTAQUE</th>
                  <th>NIVEAU DE RISQUE</th>
                  <th>SOPHISTICATION</th>
                  <th>DATE ANALYSE</th>
                  <th>ACTIONS</th>
                </tr>
              </thead>
              <tbody>
                {filteredAnalyses.map((analyse) => (
                  <tr key={analyse.idAnalyse}>
                    <td className="report-id">{analyse.idAnalyse}</td>
                   
                    <td>
                      <span className={`status-badge status-${analyse.statut || 'en-attente'}`}>
                        {analyse.statut || 'En attente'}
                      </span>
                    </td>
                    <td className="report-type">{analyse.typeAttaque}</td>
                    <td>
                      <span className={`severity-badge ${getRiskColor(analyse.niveauRisque)}`}>
                        {analyse.niveauRisque}
                      </span>
                    </td>
                    <td className="report-type">{analyse.sophistication}</td>
                    <td className="report-date">{formatDate(analyse.dateAnalyse)}</td>
                    <td className="report-actions">
                      <button
                        className="view-details-btn"
                        onClick={() => openDetails(analyse)}
                        title="Voir les détails"
                      >
                        Voir détails
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            {filteredAnalyses.length === 0 && (
              <div className="empty-state">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V5h14v14z" />
                </svg>
                <p>Aucune analyse trouvée</p>
              </div>
            )}
          </div>
        </div>
      </main>

      {/* Details Modal */}
    {showModal && selectedAnalyse && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Analyse ID {selectedAnalyse.idAnalyse}</h2>
              <button className="modal-close" onClick={() => setShowModal(false)}>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
                </svg>
              </button>
            </div>

            <div className="modal-body">
              <div className="detail-row">
                <span className="detail-label">ID Analyse:</span>
                <span className="detail-value">{selectedAnalyse.idAnalyse}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Résumé:</span>
                {isEditing && !selectedAnalyse.statut ? (
                  <textarea
                    value={editedResume}
                    onChange={(e) => setEditedResume(e.target.value)}
                    className="modal-textarea"
                    rows="3"
                  />
                ) : (
                  <p className="detail-description">{selectedAnalyse.resume}</p>
                )}
              </div>
              <div className="detail-row">
                <span className="detail-label">Type d'attaque:</span>
                <span className="detail-value">{selectedAnalyse.typeAttaque}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Niveau de risque:</span>
                {isEditing && !selectedAnalyse.statut ? (
                  <select
                    value={editedNiveauRisque}
                    onChange={(e) => setEditedNiveauRisque(e.target.value)}
                    className="modal-select"
                  >
                    <option value="faible">Faible</option>
                    <option value="moyen">Moyen</option>
                    <option value="élevé">Élevé</option>
                  </select>
                ) : (
                  <span className={`severity-badge ${getRiskColor(selectedAnalyse.niveauRisque)}`}>
                    {selectedAnalyse.niveauRisque}
                  </span>
                )}
              </div>
              <div className="detail-row">
                <span className="detail-label">Sophistication:</span>
                {isEditing && !selectedAnalyse.statut ? (
                  <select
                    value={editedSophistication}
                    onChange={(e) => setEditedSophistication(e.target.value)}
                    className="modal-select"
                  >
                    <option value="faible">Faible</option>
                    <option value="moyen">Moyen</option>
                    <option value="élevé">Élevé</option>
                  </select>
                ) : (
                  <span className="detail-value">{selectedAnalyse.sophistication}</span>
                )}
              </div>
              <div className="detail-row">
                <span className="detail-label">Modèle LLM:</span>
                <span className="detail-value">{selectedAnalyse.modeleLlm}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Date d'analyse:</span>
                <span className="detail-value">{formatDate(selectedAnalyse.dateAnalyse)}</span>
              </div>

              {!selectedAnalyse.statut && (
                <>
                  <div className="detail-row">
                    <label className="detail-label">Type de modification:</label>
                    <select
                      value={typeModif}
                      onChange={(e) => setTypeModif(e.target.value)}
                      className="modal-select"
                    >
                      <option>Enrichissement</option>
                      <option>Correction</option>
                      <option>Suppression</option>
                    </select>
                  </div>
                  <div className="detail-row">
                    <label className="detail-label">Commentaire:</label>
                    <textarea
                      value={commentaire}
                      onChange={(e) => setCommentaire(e.target.value)}
                      className="modal-textarea"
                      placeholder="Ajouter un commentaire..."
                      rows="3"
                    />
                  </div>
                </>
              )}

              {selectedAnalyse.validation && (
                <>
                  <div className="detail-row">
                    <span className="detail-label">Commentaire validation:</span>
                    <p className="detail-description">{selectedAnalyse.validation.commentaire}</p>
                  </div>
                  <div className="detail-row">
                    <span className="detail-label">Type de modification:</span>
                    <span className="detail-value">{selectedAnalyse.validation.typeModif}</span>
                  </div>
                  <div className="detail-row">
                    <span className="detail-label">Date de validation:</span>
                    <span className="detail-value">{formatDate(selectedAnalyse.validation.dateValid)}</span>
                  </div>
                </>
              )}
            </div>

            <div className="modal-footer">
              {!selectedAnalyse.statut ? (
                <>
                  {!isEditing ? (
                    <button className="btn-edit" onClick={startEditing}>
                      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25z" />
                        <path d="M20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                      </svg>
                      Modifier les valeurs
                    </button>
                  ) : (
                    <button className="btn-cancel" onClick={cancelEditing}>
                      Annuler
                    </button>
                  )}
                  <button className="btn-reject" onClick={() => handleValidate(selectedAnalyse,"rejeté")}>
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
                    </svg>
                    Rejeter
                  </button>
                  <button className="btn-validate" onClick={() => handleValidate(selectedAnalyse,"validé")}>
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" />
                    </svg>
                    Valider
                  </button>
                </>
              ) : (
                <button className="btn-close" onClick={() => resetModal()}>
                  Fermer
                </button>
              )}
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
            <p>Analyse validée avec succès!</p>
          </div>
        </div>
      )}
    </div>
  )
}
