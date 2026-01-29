'use client'

import { useState ,useContext,useEffect} from 'react'
import Navbar from '../nav/nav'
import './visiteur.css'
import { GlobalState } from "../context/context"
export default function VisiteurPage() {
  let {user,token,role}=useContext(GlobalState);
  const [sources, setSources] = useState([
    {
      idFlux: 1,
      nomFlux: 'TechCrunch RSS',
      urlFlux: 'https://techcrunch.com/feed/',
      description: 'Actualités technologiques et startups',
      statut: 'actif',
      dateAjout: '2025-01-15T10:30:00',
      derniereMaj: '2025-01-20T14:22:00',
    },
    {
      idFlux: 2,
      nomFlux: 'BBC News Technology',
      urlFlux: 'https://feeds.bbci.co.uk/news/technology/rss.xml',
      description: 'Actualités technologiques BBC',
      statut: 'actif',
      dateAjout: '2025-01-10T09:15:00',
      derniereMaj: '2025-01-20T13:45:00',
    },
    {
      idFlux: 3,
      nomFlux: 'Le Monde Économie',
      urlFlux: 'https://www.lemonde.fr/economie/rss_full.xml',
      description: 'Actualités économiques françaises',
      statut: 'actif',
      dateAjout: '2025-01-12T11:20:00',
      derniereMaj: '2025-01-20T12:10:00',
    },
  ])

  const [analyses, setAnalyses] = useState([
    {
      idAnalyse: 1,
      idFlux: 1,
      resume: 'Campagne de phishing utilisant des techniques d\'IA pour la personnalisation avancée',
      typeAttaque: 'Phishing',
      niveauRisque: 'élevé',
      sophistication: 'élevé',
      dateAnalyse: '2025-01-15T14:30:00',
      modeleLlm: 'GPT-4',
      statut: 'validé',
    },
    {
      idAnalyse: 2,
      idFlux: 1,
      resume: 'Réseau de distribution automatique de malware détecté sur plusieurs canaux',
      typeAttaque: 'Malware',
      niveauRisque: 'élevé',
      sophistication: 'moyen',
      dateAnalyse: '2025-01-14T11:45:00',
      modeleLlm: 'Claude-3',
      statut: 'validé',
    },
    {
      idAnalyse: 3,
      idFlux: 2,
      resume: 'Nouvelle technique d\'attaque non encore cataloguée',
      typeAttaque: 'Attaque mixte',
      niveauRisque: 'moyen',
      sophistication: 'élevé',
      dateAnalyse: '2025-01-16T09:15:00',
      modeleLlm: 'GPT-4',
      statut: 'validé',
    },
    {
      idAnalyse: 4,
      idFlux: 3,
      resume: 'Campagne d\'attaque coordonnée avec plusieurs étapes',
      typeAttaque: 'Attaque coordonnée',
      niveauRisque: 'élevé',
      sophistication: 'élevé',
      dateAnalyse: '2025-01-13T16:20:00',
      modeleLlm: 'Claude-3',
      statut: 'validé',
    },
  ])

  const [selectedSource, setSelectedSource] = useState(null)
  const [showAnalysesModal, setShowAnalysesModal] = useState(false)
  const [searchTerm, setSearchTerm] = useState('')
  const [selectedAnalyse, setSelectedAnalyse] = useState(null)
  const [showDetailModal, setShowDetailModal] = useState(false)

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

  const handleSourceClick = (source) => {
    setSelectedSource(source)
   
    setShowAnalysesModal(true)
  }

  const filteredSources = sources.filter((source) =>
    source.nomFlux.toLowerCase().includes(searchTerm.toLowerCase()) ||
    source.urlFlux.toLowerCase().includes(searchTerm.toLowerCase())
  )

  const selectedSourceAnalyses = selectedSource
    ? analyses.filter((a) =>  a.statut === 'validé')
    : []

  async function getflux() {
    try{
      let response=await fetch(`http://localhost:8080/api/flux-rss`,{
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
  async function getanalyses(source) {
     try{
      let response=await fetch(`http://localhost:8080/api/analyses/flux/${source.idFlux}/validees`,{
        method:"GET",
        headers:{
           Authorization: `Bearer ${token}`,
        }
      })
      if(response.ok){
        let data=await response.json()
        let strdata=data.analyses.map((analyse)=>analyse?{...analyse,statut:(analyse.validation)?analyse.validation.statut:null}:null)
        console.log(strdata)
        setAnalyses([...strdata])
      }

    }catch(error){
      console.log(error)

    }
  }
 useEffect(()=>{
        if(user){
          getflux()
        }
     },[user])
  useEffect(()=>{
        if(selectedSource){
        
         getanalyses(selectedSource)
        }
      },[selectedSource])
  return (
    <div className="visiteur-page">
      <Navbar prenom="Jean" nom="Leclerc" role="Visiteur" />

      <main className="visiteur-main">
        <div className="visiteur-container">
          <div className="page-header">
            <h1>Sources RSS</h1>
            <p>Consultez les sources de veille disponibles</p>
          </div>

          <div className="search-section">
            <div className="search-box">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5S13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z" />
              </svg>
              <input
                type="text"
                placeholder="Rechercher une source..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          </div>

          <div className="sources-grid">
            {filteredSources.map((source) => (
              <div
                key={source.idFlux}
                className="source-card"
                onClick={() => handleSourceClick(source)}
              >
                <div className="source-header">
                  <h3>{source.nomFlux}</h3>
                  <span className={`status-badge ${source.statut}`}>{source.statut}</span>
                </div>
                <div className="source-info">
                  <p className="source-description">{source.description}</p>
                  <div className="source-meta">
                    <span className="meta-item">
                      Ajouté: {formatDate(source.dateAjout)}
                    </span>
                    <span className="meta-item">
                      Maj: {formatDate(source.derniereMaj)}
                    </span>
                  </div>
                </div>
                <div className="source-footer">
                  <span className="analyses-count">
                    {analyses.filter((a) =>  a.statut === 'validé').length} analyse(s)
                  </span>
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" className="arrow-icon">
                    <path d="M8.59 16.59L10 18l6-6-6-6-1.41 1.41L13.17 12z" />
                  </svg>
                </div>
              </div>
            ))}
          </div>

          {filteredSources.length === 0 && (
            <div className="empty-state">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V5h14v14z" />
              </svg>
              <p>Aucune source trouvée</p>
            </div>
          )}
        </div>
      </main>

      {/* Analyses Modal */}
      {showAnalysesModal && selectedSource && (
        <div className="modal-overlay" onClick={() => setShowAnalysesModal(false)}>
          <div className="modal-content analyses-modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Analyses validées - {selectedSource.nomFlux}</h2>
              <button className="modal-close" onClick={() => setShowAnalysesModal(false)}>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
                </svg>
              </button>
            </div>

            <div className="modal-body">
              {selectedSourceAnalyses.length > 0 ? (
                <div className="analyses-list">
                  {selectedSourceAnalyses.map((analyse) => (
                    <div
                      key={analyse.idAnalyse}
                      className="analyse-card"
                      onClick={() => {
                        setSelectedAnalyse(analyse)
                        setShowDetailModal(true)
                      }}
                    >
                      <div className="analyse-header">
                        <h4>{analyse.resume.substring(0, 60)}...</h4>
                        <span className={`severity-badge ${getRiskColor(analyse.niveauRisque)}`}>
                          {analyse.niveauRisque}
                        </span>
                      </div>
                      <div className="analyse-info">
                        <span className="badge">{analyse.typeAttaque}</span>
                        <span className="badge sophistication">{analyse.sophistication}</span>
                        <span className="date">{formatDate(analyse.dateAnalyse)}</span>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="empty-analyses">
                  <p>Aucune analyse validée pour cette source</p>
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Analysis Detail Modal */}
      {showDetailModal && selectedAnalyse && (
        <div className="modal-overlay" onClick={() => setShowDetailModal(false)}>
          <div className="modal-content detail-modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Analyse détaillée</h2>
              <button className="modal-close" onClick={() => setShowDetailModal(false)}>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
                </svg>
              </button>
            </div>

            <div className="modal-body">
              <div className="detail-row">
                <span className="detail-label">Résumé:</span>
                <p className="detail-description">{selectedAnalyse.resume}</p>
              </div>
              <div className="detail-row">
                <span className="detail-label">Type d'attaque:</span>
                <span className="detail-value">{selectedAnalyse.typeAttaque}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Niveau de risque:</span>
                <span className={`severity-badge ${getRiskColor(selectedAnalyse.niveauRisque)}`}>
                  {selectedAnalyse.niveauRisque}
                </span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Sophistication:</span>
                <span className="detail-value">{selectedAnalyse.sophistication}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Modèle LLM:</span>
                <span className="detail-value">{selectedAnalyse.modeleLlm}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Date d'analyse:</span>
                <span className="detail-value">{formatDate(selectedAnalyse.dateAnalyse)}</span>
              </div>
               <div className="detail-row">
                <span className="detail-label">source url:</span>
                <a href={selectedAnalyse.article.urlSource} target="_blank" rel="noopener noreferrer" className="detail-link">
                  {selectedAnalyse.article.urlSource}
                </a>
              </div>
            </div>

            <div className="modal-footer">
              <button className="btn-close" onClick={() => setShowDetailModal(false)}>
                Fermer
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
