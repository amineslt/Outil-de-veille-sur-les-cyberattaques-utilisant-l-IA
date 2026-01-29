package myteam.projetvtbda.controller;

import myteam.projetvtbda.Model.Article;
import myteam.projetvtbda.Model.Rapport;
import myteam.projetvtbda.Service.RapportService;
import myteam.projetvtbda.dto.RapportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rapports")
@CrossOrigin(origins = "*")
public class RapportController {

    @Autowired
    private RapportService rapportService;

    /**
     *  Générer un nouveau rapport (Analyste et Décideur)
     */
    @PostMapping("/generer")
    @PreAuthorize("hasAnyRole('Analyste', 'Décideur')")
    public ResponseEntity<?> genererRapport(
            @RequestHeader("Authorization") String token,
            @RequestBody RapportRequest request) {
        try {
            Rapport rapport = rapportService.genererRapport(token, request);
            return ResponseEntity.ok(Map.of(
                    "message", "Rapport généré avec succès",
                    "rapport", rapport
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Récupérer tous les rapports
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('Analyste', 'Décideur')")
    public ResponseEntity<List<Rapport>> getTousLesRapports() {
        return ResponseEntity.ok(rapportService.getTousLesRapports());
    }

    /**
     * Récupérer un rapport par ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Analyste', 'Décideur')")
    public ResponseEntity<?> getRapportById(@PathVariable Long id) {
        try {
            Rapport rapport = rapportService.getRapportById(id);
            return ResponseEntity.ok(rapport);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Récupérer mes rapports (Décideur)
     */
    @GetMapping("/mes-rapports")
    @PreAuthorize("hasAnyRole('Analyste', 'Décideur')")
    public ResponseEntity<?> getMesRapports(@RequestHeader("Authorization") String token) {
        try {
            List<Rapport> rapports = rapportService.getRapportsParDecideur(token);
            return ResponseEntity.ok(rapports);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Récupérer les articles d'un rapport (relation N-N)
     */
    @GetMapping("/{id}/articles")
    @PreAuthorize("hasAnyRole('Analyste', 'Décideur')")
    public ResponseEntity<?> getArticlesParRapport(@PathVariable Long id) {
        try {
            List<Article> articles = rapportService.getArticlesParRapport(id);
            return ResponseEntity.ok(Map.of(
                    "nb_articles", articles.size(),
                    "articles", articles
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Supprimer un rapport (Décideur uniquement)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Décideur')")
    public ResponseEntity<?> supprimerRapport(@PathVariable Long id) {
        try {
            rapportService.supprimerRapport(id);
            return ResponseEntity.ok(Map.of("message", "Rapport supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }
}