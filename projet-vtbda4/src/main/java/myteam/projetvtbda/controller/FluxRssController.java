package myteam.projetvtbda.controller;

import myteam.projetvtbda.Model.FluxRss;
import myteam.projetvtbda.Service.FluxRssService;
import myteam.projetvtbda.dto.FluxRssRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flux-rss")
@CrossOrigin(origins = "*")
public class FluxRssController {

    @Autowired
    private FluxRssService fluxRssService;

    /**
     * Créer un nouveau flux RSS (Veilleur uniquement)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('Veilleur', 'Décideur')")
    public ResponseEntity<?> creerFlux(
            @RequestHeader("Authorization") String token,
            @RequestBody FluxRssRequest request) {
        try {
            FluxRss fluxRss = fluxRssService.creerFluxRss(token, request);
            return ResponseEntity.ok(Map.of(
                    "message", "Flux RSS créé avec succès",
                    "flux", fluxRss
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Récupérer tous les flux RSS (Tous les utilisateurs authentifiés)
     */
    @GetMapping
    public ResponseEntity<List<FluxRss>> getTousLesFlux() {
        return ResponseEntity.ok(fluxRssService.getTousLesFlux());
    }

    /**
     * Récupérer les flux par statut
     */
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<FluxRss>> getFluxParStatut(@PathVariable FluxRss.Statut statut) {
        return ResponseEntity.ok(fluxRssService.getFluxParStatut(statut));
    }

    /**
     * Récupérer un flux par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFluxById(@PathVariable Long id) {
        try {
            FluxRss fluxRss = fluxRssService.getFluxById(id);
            return ResponseEntity.ok(fluxRss);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Récupérer mes flux RSS (Veilleur)
     */
    @GetMapping("/mes-flux")
    @PreAuthorize("hasAnyRole('Veilleur', 'Décideur')")
    public ResponseEntity<?> getMesFlux(@RequestHeader("Authorization") String token) {
        try {
            List<FluxRss> flux = fluxRssService.getFluxParVeilleur(token);
            return ResponseEntity.ok(flux);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Modifier un flux RSS (Veilleur uniquement)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Veilleur', 'Décideur')")
    public ResponseEntity<?> modifierFlux(
            @PathVariable Long id,
            @RequestBody FluxRssRequest request) {
        try {
            FluxRss fluxRss = fluxRssService.modifierFluxRss(id, request);
            return ResponseEntity.ok(Map.of(
                    "message", "Flux RSS modifié avec succès",
                    "flux", fluxRss
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Activer/Désactiver un flux (Veilleur uniquement)
     */
    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('Veilleur', 'Décideur')")
    public ResponseEntity<?> changerStatut(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            FluxRss.Statut statut = FluxRss.Statut.valueOf(request.get("statut"));
            FluxRss fluxRss = fluxRssService.changerStatutFlux(id, statut);
            return ResponseEntity.ok(Map.of(
                    "message", "Statut modifié avec succès",
                    "flux", fluxRss
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Supprimer un flux RSS (Veilleur uniquement)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('Veilleur', 'Décideur')")
    public ResponseEntity<?> supprimerFlux(@PathVariable Long id) {
        try {
            fluxRssService.supprimerFluxRss(id);
            return ResponseEntity.ok(Map.of("message", "Flux RSS supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }
}