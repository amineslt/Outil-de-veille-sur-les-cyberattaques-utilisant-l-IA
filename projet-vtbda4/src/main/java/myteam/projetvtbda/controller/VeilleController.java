package myteam.projetvtbda.controller;

import myteam.projetvtbda.Model.AnalyseLlm;
import myteam.projetvtbda.Model.Article;
import myteam.projetvtbda.Model.ArticleFiltre;
import myteam.projetvtbda.Repository.AnalyseLlmRepository;
import myteam.projetvtbda.Repository.ArticleFiltreRepository;
import myteam.projetvtbda.Repository.ArticleRepository;
import myteam.projetvtbda.Service.FiltrageService;
import myteam.projetvtbda.Service.LlmAnalyseService;
import myteam.projetvtbda.Service.RssCollectorService;
import myteam.projetvtbda.config.VeilleScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/veille")
@CrossOrigin(origins = "*")
public class VeilleController {

    @Autowired
    private RssCollectorService rssCollectorService;

    @Autowired
    private FiltrageService filtrageService;

    @Autowired
    private LlmAnalyseService llmAnalyseService;

    @Autowired
    private VeilleScheduler veilleScheduler;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleFiltreRepository articleFiltreRepository;

    @Autowired
    private AnalyseLlmRepository analyseLlmRepository;

    /**
     * LANCER LE CYCLE COMPLET (Collecte + Filtrage + Analyse)
     * C'est LA requête principale à utiliser
     */
    @PostMapping("/cycle-complet")
    @PreAuthorize("hasAnyRole('Veilleur', 'Décideur')")
    public ResponseEntity<?> lancerCycleComplet() {
        try {
            System.out.println("\n Lancement manuel du cycle complet de veille...\n");

            // Compter AVANT l'exécution
            long nbArticlesAvant = articleRepository.count();
            long nbFiltresAvant = articleFiltreRepository.count();
            long nbAnalysesAvant = analyseLlmRepository.count();

            // Appeler la méthode du scheduler
            veilleScheduler.executerCycleComplet();

            // Compter APRÈS l'exécution
            long nbArticlesApres = articleRepository.count();
            long nbFiltresApres = articleFiltreRepository.count();
            long nbAnalysesApres = analyseLlmRepository.count();

            // Calculer les nouveaux éléments
            long nouveauxArticles = nbArticlesApres - nbArticlesAvant;
            long nouveauxFiltres = nbFiltresApres - nbFiltresAvant;
            long nouvellesAnalyses = nbAnalysesApres - nbAnalysesAvant;

            // Compter les articles pertinents
            long nbPertinents = articleFiltreRepository.findByPertinent(true).size();

            return ResponseEntity.ok(Map.of(
                    "message", "Cycle complet terminé avec succès",
                    "collecte", Map.of(
                            "nouveaux_articles", nouveauxArticles,
                            "total_articles", nbArticlesApres
                    ),
                    "filtrage", Map.of(
                            "nouveaux_filtres", nouveauxFiltres,
                            "total_filtres", nbFiltresApres,
                            "total_pertinents", nbPertinents
                    ),
                    "analyse", Map.of(
                            "nouvelles_analyses", nouvellesAnalyses,
                            "total_analyses", nbAnalysesApres
                    )
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Lancer manuellement la collecte RSS
     */
    @PostMapping("/collecter")
    @PreAuthorize("hasAnyRole('Veilleur', 'Décideur')")
    public ResponseEntity<?> lancerCollecte() {
        try {
            List<Article> articles = rssCollectorService.collecterTousLesFlux();
            return ResponseEntity.ok(Map.of(
                    "message", "Collecte terminée avec succès",
                    "nb_articles", articles.size(),
                    "total_articles", articleRepository.count()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Lancer manuellement le filtrage
     */
    @PostMapping("/filtrer")
    @PreAuthorize("hasAnyRole('Veilleur', 'Décideur')")
    public ResponseEntity<?> lancerFiltrage() {
        try {
            List<ArticleFiltre> filtres = filtrageService.filtrerTousLesArticles();
            long pertinents = filtres.stream().filter(ArticleFiltre::getPertinent).count();
            return ResponseEntity.ok(Map.of(
                    "message", "Filtrage terminé avec succès",
                    "nb_articles_filtres", filtres.size(),
                    "nb_pertinents", pertinents,
                    "total_filtres", articleFiltreRepository.count()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Lancer manuellement l'analyse LLM
     */
    @PostMapping("/analyser")
    @PreAuthorize("hasAnyRole('Veilleur', 'Analyste', 'Décideur')")
    public ResponseEntity<?> lancerAnalyse() {
        try {
            List<AnalyseLlm> analyses = llmAnalyseService.analyserTousLesArticlesPertinents();
            return ResponseEntity.ok(Map.of(
                    "message", "Analyse LLM terminée avec succès",
                    "nb_analyses", analyses.size(),
                    "total_analyses", analyseLlmRepository.count()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Collecter un flux spécifique
     */
    @PostMapping("/collecter/{idFlux}")
    @PreAuthorize("hasAnyRole('Veilleur', 'Décideur')")
    public ResponseEntity<?> collecterFlux(@PathVariable Long idFlux) {
        try {
            List<Article> articles = rssCollectorService.collecterFluxParId(idFlux);
            return ResponseEntity.ok(Map.of(
                    "message", "Flux collecté avec succès",
                    "nb_articles", articles.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Obtenir les statistiques globales
     */
    @GetMapping("/statistiques")
    public ResponseEntity<?> getStatistiques() {
        long totalArticles = articleRepository.count();
        long totalFiltres = articleFiltreRepository.count();
        long totalPertinents = articleFiltreRepository.findByPertinent(true).size();
        long totalAnalyses = analyseLlmRepository.count();

        return ResponseEntity.ok(Map.of(
                "total_articles", totalArticles,
                "total_filtres", totalFiltres,
                "total_pertinents", totalPertinents,
                "total_analyses", totalAnalyses,
                "taux_pertinence", totalFiltres > 0 ? (totalPertinents * 100.0 / totalFiltres) : 0
        ));
    }
}