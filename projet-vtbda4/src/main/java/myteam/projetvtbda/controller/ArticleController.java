package myteam.projetvtbda.controller;

import myteam.projetvtbda.Model.Article;
import myteam.projetvtbda.Repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*")
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    /**
     * Lister tous les articles
     */
    @GetMapping
    public ResponseEntity<List<Article>> getTousLesArticles() {
        return ResponseEntity.ok(articleRepository.findAll());
    }

    /**
     * Récupérer un article par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getArticleById(@PathVariable Long id) {
        return articleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lister les articles d'un flux spécifique
     */
    @GetMapping("/flux/{idFlux}")
    public ResponseEntity<List<Article>> getArticlesParFlux(@PathVariable Long idFlux) {
        return ResponseEntity.ok(articleRepository.findByFluxRssIdFlux(idFlux));
    }

    /**
     * Lister les articles collectés dans une période
     */
    @GetMapping("/periode")
    public ResponseEntity<List<Article>> getArticlesParPeriode(
            @RequestParam String debut,
            @RequestParam String fin) {
        LocalDateTime dateDebut = LocalDateTime.parse(debut);
        LocalDateTime dateFin = LocalDateTime.parse(fin);
        return ResponseEntity.ok(articleRepository.findByDateCollecteBetween(dateDebut, dateFin));
    }

    /**
     * Lister les articles non filtrés
     */
    @GetMapping("/non-filtres")
    public ResponseEntity<List<Article>> getArticlesNonFiltres() {
        return ResponseEntity.ok(articleRepository.findArticlesNonFiltres());
    }

    /**
     * Lister les articles pertinents non analysés
     */
    @GetMapping("/pertinents-non-analyses")
    public ResponseEntity<List<Article>> getArticlesPertinentsNonAnalyses() {
        return ResponseEntity.ok(articleRepository.findArticlesPertinentsNonAnalyses());
    }

    /**
     * Compter les articles
     */
    @GetMapping("/count")
    public ResponseEntity<?> compterArticles() {
        return ResponseEntity.ok(Map.of("total", articleRepository.count()));
    }
}