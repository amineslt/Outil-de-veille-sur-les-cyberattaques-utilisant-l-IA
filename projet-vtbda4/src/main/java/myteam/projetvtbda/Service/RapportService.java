package myteam.projetvtbda.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import myteam.projetvtbda.Model.*;
import myteam.projetvtbda.Repository.*;
import myteam.projetvtbda.dto.RapportRequest;
import myteam.projetvtbda.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RapportService {

    @Autowired
    private RapportRepository rapportRepository;

    @Autowired
    private RapportArticleRepository rapportArticleRepository;

    @Autowired
    private AnalyseLlmRepository analyseLlmRepository;

    @Autowired
    private ValidationRepository validationRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private LlmService llmService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Générer un rapport automatique avec Perplexity
     */
    @Transactional
    public Rapport genererRapport(String token, RapportRequest request) {
        String jwt = token.substring(7);
        String email = jwtUtil.extractUsername(jwt);

        Utilisateur decideur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        System.out.println("\n Génération de rapport : " + request.getTitre());
        System.out.println(" Période : " + request.getPeriodeDebut() + " → " + request.getPeriodeFin());

        // ÉTAPE 1 : Sélectionner les analyses VALIDÉES correspondantes
        List<AnalyseLlm> analysesValidees = selectionnerAnalyses(request);

        if (analysesValidees.isEmpty()) {
            throw new RuntimeException("Aucune analyse validée trouvée pour cette période");
        }

        System.out.println("--" + analysesValidees.size() + " analyses sélectionnées");

        // ÉTAPE 2 : Calculer les statistiques
        Map<String, Object> statistiques = calculerStatistiques(analysesValidees);

        // ÉTAPE 3 : Générer le contenu avec Perplexity
        String contenuJson = genererContenuAvecLlm(analysesValidees, statistiques);

        // ÉTAPE 4 : Créer le rapport
        Rapport rapport = new Rapport();
        rapport.setTitre(request.getTitre());
        rapport.setPeriodeDebut(request.getPeriodeDebut());
        rapport.setPeriodeFin(request.getPeriodeFin());
        rapport.setTypeRapport(request.getTypeRapport());
        rapport.setContenu(contenuJson);
        rapport.setDecideur(decideur);

        Rapport savedRapport = rapportRepository.save(rapport);

        // ÉTAPE 5 : Lier les articles au rapport (relation N-N)
        for (AnalyseLlm analyse : analysesValidees) {
            RapportArticle rapportArticle = new RapportArticle();
            rapportArticle.setRapport(savedRapport);
            rapportArticle.setArticle(analyse.getArticle());
            rapportArticleRepository.save(rapportArticle);
        }

        System.out.println("🎉 Rapport généré avec succès : ID " + savedRapport.getIdRapport());

        return savedRapport;
    }

    /**
     * Sélectionner les analyses validées selon les critères
     */
    private List<AnalyseLlm> selectionnerAnalyses(RapportRequest request) {
        LocalDateTime dateDebut = request.getPeriodeDebut().atStartOfDay();
        LocalDateTime dateFin = request.getPeriodeFin().atTime(23, 59, 59);

        // Récupérer toutes les analyses de la période
        List<AnalyseLlm> analyses = analyseLlmRepository.findByDateAnalyseBetween(dateDebut, dateFin);

        // Filtrer : garder seulement les analyses VALIDÉES
        List<AnalyseLlm> analysesValidees = analyses.stream()
                .filter(a -> a.getValidation() != null)
                .filter(a -> a.getValidation().getStatut() == Validation.Statut.validé)
                .collect(Collectors.toList());

        // Appliquer les filtres optionnels
        if (request.getNiveauRisqueMin() != null) {
            analysesValidees = filtrerParNiveauRisque(analysesValidees, request.getNiveauRisqueMin());
        }

        if (request.getTypeAttaque() != null && !request.getTypeAttaque().isEmpty()) {
            analysesValidees = analysesValidees.stream()
                    .filter(a -> a.getTypeAttaque().equalsIgnoreCase(request.getTypeAttaque()))
                    .collect(Collectors.toList());
        }

        // Limiter le nombre d'articles si demandé
        if (request.getLimitArticles() != null && request.getLimitArticles() > 0) {
            analysesValidees = analysesValidees.stream()
                    .sorted(Comparator.comparing(AnalyseLlm::getNiveauRisque).reversed())
                    .limit(request.getLimitArticles())
                    .collect(Collectors.toList());
        }

        return analysesValidees;
    }

    /**
     * Filtrer par niveau de risque minimum
     */
    private List<AnalyseLlm> filtrerParNiveauRisque(List<AnalyseLlm> analyses, AnalyseLlm.NiveauRisque niveauMin) {
        return analyses.stream()
                .filter(a -> {
                    if (niveauMin == AnalyseLlm.NiveauRisque.élevé) {
                        return a.getNiveauRisque() == AnalyseLlm.NiveauRisque.élevé;
                    } else if (niveauMin == AnalyseLlm.NiveauRisque.moyen) {
                        return a.getNiveauRisque() == AnalyseLlm.NiveauRisque.élevé ||
                                a.getNiveauRisque() == AnalyseLlm.NiveauRisque.moyen;
                    }
                    return true; // faible inclut tout
                })
                .collect(Collectors.toList());
    }

    /**
     * Calculer les statistiques sur les analyses
     */
    private Map<String, Object> calculerStatistiques(List<AnalyseLlm> analyses) {
        long nbElevees = analyses.stream()
                .filter(a -> a.getNiveauRisque() == AnalyseLlm.NiveauRisque.élevé)
                .count();

        long nbMoyennes = analyses.stream()
                .filter(a -> a.getNiveauRisque() == AnalyseLlm.NiveauRisque.moyen)
                .count();

        long nbFaibles = analyses.stream()
                .filter(a -> a.getNiveauRisque() == AnalyseLlm.NiveauRisque.faible)
                .count();

        // Types d'attaques les plus fréquents
        Map<String, Long> typesFrequents = analyses.stream()
                .collect(Collectors.groupingBy(
                        AnalyseLlm::getTypeAttaque,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return Map.of(
                "nb_total", analyses.size(),
                "nb_elevees", nbElevees,
                "nb_moyennes", nbMoyennes,
                "nb_faibles", nbFaibles,
                "types_frequents", typesFrequents
        );
    }

    /**
     * Générer le contenu du rapport avec Perplexity
     */
    private String genererContenuAvecLlm(List<AnalyseLlm> analyses, Map<String, Object> stats) {
        // Préparer les données pour le prompt
        StringBuilder analysesResume = new StringBuilder();
        for (int i = 0; i < Math.min(analyses.size(), 10); i++) {
            AnalyseLlm analyse = analyses.get(i);
            analysesResume.append(String.format(
                    "\n- %s (Risque: %s, Type: %s)",
                    analyse.getResume(),
                    analyse.getNiveauRisque(),
                    analyse.getTypeAttaque()
            ));
        }

        String prompt = String.format("""
                Tu es un expert en cybersécurité. Génère un rapport de veille synthétique basé sur ces analyses d'attaques utilisant l'IA.
                
                STATISTIQUES :
                - Total d'analyses : %d
                - Menaces élevées : %d
                - Menaces moyennes : %d
                - Menaces faibles : %d
                
                PRINCIPALES ANALYSES :
                %s
                
                Fournis UNIQUEMENT un JSON avec cette structure exacte (sans markdown, sans ```json):
                {
                    "resume_global": "Un résumé global en 3-4 phrases des principales menaces détectées",
                    "tendances": ["Tendance 1", "Tendance 2", "Tendance 3"],
                    "recommandations": ["Recommandation 1", "Recommandation 2", "Recommandation 3"],
                    "menaces_prioritaires": ["Menace 1", "Menace 2"],
                    "niveau_alerte_global": "faible|moyen|élevé"
                }
                
                Réponds UNIQUEMENT avec le JSON, rien d'autre.
                """,
                stats.get("nb_total"),
                stats.get("nb_elevees"),
                stats.get("nb_moyennes"),
                stats.get("nb_faibles"),
                analysesResume.toString()
        );

        System.out.println(" Appel à Perplexity pour génération du rapport...");

        String reponse = llmService.poserQuestion(prompt);

        // Parser et enrichir le JSON
        try {
            String jsonStr = reponse.trim();
            if (jsonStr.startsWith("```json")) {
                jsonStr = jsonStr.substring(7);
            }
            if (jsonStr.startsWith("```")) {
                jsonStr = jsonStr.substring(3);
            }
            if (jsonStr.endsWith("```")) {
                jsonStr = jsonStr.substring(0, jsonStr.length() - 3);
            }
            jsonStr = jsonStr.trim();

            // Parser le JSON
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> contenuLlm = mapper.readValue(jsonStr, Map.class);

            // Ajouter les statistiques
            contenuLlm.put("statistiques", stats);

            // Retourner le JSON complet
            return mapper.writeValueAsString(contenuLlm);

        } catch (Exception e) {
            System.err.println(" Erreur parsing JSON de Perplexity, utilisation des stats seules");

            // JSON de secours
            Map<String, Object> contenuSecours = new HashMap<>();
            contenuSecours.put("resume_global", "Rapport généré automatiquement à partir des analyses validées");
            contenuSecours.put("statistiques", stats);
            contenuSecours.put("tendances", List.of("Données insuffisantes"));
            contenuSecours.put("recommandations", List.of("Consulter les analyses détaillées"));
            contenuSecours.put("niveau_alerte_global", "moyen");

            try {
                return new ObjectMapper().writeValueAsString(contenuSecours);
            } catch (Exception ex) {
                return "{}";
            }
        }
    }

    /**
     * Récupérer tous les rapports
     */
    public List<Rapport> getTousLesRapports() {
        return rapportRepository.findAll();
    }

    /**
     * Récupérer un rapport par ID
     */
    public Rapport getRapportById(Long id) {
        return rapportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapport non trouvé"));
    }

    /**
     * Récupérer les rapports d'un décideur
     */
    public List<Rapport> getRapportsParDecideur(String token) {
        String jwt = token.substring(7);
        String email = jwtUtil.extractUsername(jwt);

        Utilisateur decideur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return rapportRepository.findRapportsRecentsByDecideur(decideur.getIdUtilisateur());
    }

    /**
     * Récupérer les articles d'un rapport
     */
    public List<Article> getArticlesParRapport(Long idRapport) {
        List<RapportArticle> rapportArticles = rapportArticleRepository.findByRapportIdRapport(idRapport);
        return rapportArticles.stream()
                .map(RapportArticle::getArticle)
                .collect(Collectors.toList());
    }

    /**
     * Supprimer un rapport
     */
    @Transactional
    public void supprimerRapport(Long idRapport) {
        if (!rapportRepository.existsById(idRapport)) {
            throw new RuntimeException("Rapport non trouvé");
        }
        rapportRepository.deleteById(idRapport);
    }
}