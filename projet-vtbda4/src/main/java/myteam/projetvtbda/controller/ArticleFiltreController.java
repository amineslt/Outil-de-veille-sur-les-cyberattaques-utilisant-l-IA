package myteam.projetvtbda.controller;

import myteam.projetvtbda.Model.ArticleFiltre;
import myteam.projetvtbda.Repository.ArticleFiltreRepository;
import myteam.projetvtbda.Service.ArticleFiltreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/filtres")
@CrossOrigin(origins = "*")
public class ArticleFiltreController {

    @Autowired
    private ArticleFiltreRepository articleFiltreRepository;

    @Autowired
    private ArticleFiltreService articleFiltreService;

    /**
     * Lister tous les filtres
     */
    @GetMapping
    public ResponseEntity<List<ArticleFiltre>> getTousLesFiltres() {
        return ResponseEntity.ok(articleFiltreRepository.findAll());
    }

    /**
     * Récupérer un filtre par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFiltreById(@PathVariable Long id) {
        return articleFiltreRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lister les articles pertinents (score > 30)
     */
    @GetMapping("/pertinents")
    public ResponseEntity<List<ArticleFiltre>> getArticlesPertinents() {
        return ResponseEntity.ok(articleFiltreRepository.findByPertinent(true));
    }

    /**
     * Lister les articles non pertinents
     */
    @GetMapping("/non-pertinents")
    public ResponseEntity<List<ArticleFiltre>> getArticlesNonPertinents() {
        return ResponseEntity.ok(articleFiltreRepository.findByPertinent(false));
    }

    /**
     * Lister les filtres avec score minimum
     */
    @GetMapping("/score-min/{score}")
    public ResponseEntity<List<ArticleFiltre>> getFiltresParScoreMin(@PathVariable Integer score) {
        return ResponseEntity.ok(articleFiltreRepository.findByScoreMatchGreaterThanEqual(score));
    }

    /**
     * Lister les filtres par période
     */
    @GetMapping("/periode")
    public ResponseEntity<List<ArticleFiltre>> getFiltresParPeriode(
            @RequestParam String debut,
            @RequestParam String fin) {
        LocalDateTime dateDebut = LocalDateTime.parse(debut);
        LocalDateTime dateFin = LocalDateTime.parse(fin);
        return ResponseEntity.ok(articleFiltreRepository.findByDateFiltrageBetween(dateDebut, dateFin));
    }

    /**
     * Lister les articles pertinents triés par score
     */
    @GetMapping("/pertinents/tries")
    public ResponseEntity<List<ArticleFiltre>> getArticlesPertinentsTries() {
        return ResponseEntity.ok(articleFiltreRepository.findArticlesPertinentsTriesParScore());
    }

    /**
     *  Lister tous les articles filtrés d'un flux RSS
     */
    @GetMapping("/flux/{idFlux}")
    public ResponseEntity<?> getArticlesFiltresParFlux(@PathVariable Long idFlux) {
        try {
            List<ArticleFiltre> filtres = articleFiltreService.getArticlesFiltresParFlux(idFlux);
            return ResponseEntity.ok(Map.of(
                    "nb_filtres", filtres.size(),
                    "filtres", filtres
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     *  Lister uniquement les articles PERTINENTS d'un flux RSS
     */
    @GetMapping("/flux/{idFlux}/pertinents")
    public ResponseEntity<?> getArticlesPertinentsParFlux(@PathVariable Long idFlux) {
        try {
            List<ArticleFiltre> filtres = articleFiltreService.getArticlesPertinentsParFlux(idFlux);
            return ResponseEntity.ok(Map.of(
                    "nb_pertinents", filtres.size(),
                    "filtres", filtres
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     *  Statistiques des filtres d'un flux RSS
     */
    @GetMapping("/flux/{idFlux}/statistiques")
    public ResponseEntity<?> getStatistiquesFiltresParFlux(@PathVariable Long idFlux) {
        try {
            Map<String, Object> stats = articleFiltreService.getStatistiquesFiltresParFlux(idFlux);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Statistiques de filtrage globales
     */
    @GetMapping("/statistiques")
    public ResponseEntity<?> getStatistiquesFiltrage() {
        long total = articleFiltreRepository.count();
        long pertinents = articleFiltreRepository.findByPertinent(true).size();
        long nonPertinents = articleFiltreRepository.findByPertinent(false).size();

        return ResponseEntity.ok(Map.of(
                "total_filtres", total,
                "pertinents", pertinents,
                "non_pertinents", nonPertinents,
                "taux_pertinence", total > 0 ? (pertinents * 100.0 / total) : 0
        ));
    }
}