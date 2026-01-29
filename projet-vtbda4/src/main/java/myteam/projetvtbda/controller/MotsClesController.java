package myteam.projetvtbda.controller;

import myteam.projetvtbda.Model.MotsCles;
import myteam.projetvtbda.Service.MotsClesService;
import myteam.projetvtbda.dto.MotsClesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mots-cles")
@CrossOrigin(origins = "*")
public class MotsClesController {

    @Autowired
    private MotsClesService motsClesService;

    /**
     * Ajouter un mot-clé (Veilleur uniquement)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('Veilleur', 'Décideur')")
    public ResponseEntity<?> ajouterMotCle(
            @RequestHeader("Authorization") String token,
            @RequestBody MotsClesRequest request) {
        try {
            MotsCles motCle = motsClesService.ajouterMotCle(token, request);
            return ResponseEntity.ok(Map.of(
                    "message", "Mot-clé ajouté avec succès",
                    "motCle", motCle
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Récupérer tous les mots-clés (Tous les utilisateurs authentifiés)
     */
    @GetMapping
    public ResponseEntity<List<MotsCles>> getTousLesMotsCles() {
        return ResponseEntity.ok(motsClesService.getTousLesMotsCles());
    }

    /**
     * Récupérer les mots-clés actifs uniquement
     */
    @GetMapping("/actifs")
    public ResponseEntity<List<MotsCles>> getMotsClesActifs() {
        return ResponseEntity.ok(motsClesService.getMotsClesActifs());
    }

    /**
     * Récupérer les mots-clés par catégorie
     */
    @GetMapping("/categorie/{categorie}")
    public ResponseEntity<List<MotsCles>> getMotsClesParCategorie(@PathVariable String categorie) {
        return ResponseEntity.ok(motsClesService.getMotsClesParCategorie(categorie));
    }

    /**
     * Récupérer un mot-clé par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMotCleById(@PathVariable Long id) {
        try {
            MotsCles motCle = motsClesService.getMotCleById(id);
            return ResponseEntity.ok(motCle);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Récupérer mes mots-clés (Veilleur)
     */
    @GetMapping("/mes-mots-cles")
    @PreAuthorize("hasAnyRole('Veilleur', 'Décideur')")
    public ResponseEntity<?> getMesMotsCles(@RequestHeader("Authorization") String token) {
        try {
            List<MotsCles> motsCles = motsClesService.getMotsClesParVeilleur(token);
            return ResponseEntity.ok(motsCles);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Modifier un mot-clé (Veilleur uniquement)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Veilleur', 'Décideur')")
    public ResponseEntity<?> modifierMotCle(
            @PathVariable Long id,
            @RequestBody MotsClesRequest request) {
        try {
            MotsCles motCle = motsClesService.modifierMotCle(id, request);
            return ResponseEntity.ok(Map.of(
                    "message", "Mot-clé modifié avec succès",
                    "motCle", motCle
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Activer/Désactiver un mot-clé (Veilleur uniquement)
     */
    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('Veilleur', 'Décideur')")
    public ResponseEntity<?> changerStatut(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        try {
            Boolean actif = request.get("actif");
            MotsCles motCle = motsClesService.changerStatutMotCle(id, actif);
            return ResponseEntity.ok(Map.of(
                    "message", "Statut modifié avec succès",
                    "motCle", motCle
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Supprimer un mot-clé (Veilleur uniquement)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('Veilleur', 'Décideur')")
    public ResponseEntity<?> supprimerMotCle(@PathVariable Long id) {
        try {
            motsClesService.supprimerMotCle(id);
            return ResponseEntity.ok(Map.of("message", "Mot-clé supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }
}