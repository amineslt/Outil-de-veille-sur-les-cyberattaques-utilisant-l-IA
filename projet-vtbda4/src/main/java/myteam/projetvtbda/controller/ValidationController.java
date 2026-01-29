package myteam.projetvtbda.controller;

import myteam.projetvtbda.Model.Validation;
import myteam.projetvtbda.Service.ValidationService;
import myteam.projetvtbda.dto.ValidationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/validations")
@CrossOrigin(origins = "*")
public class ValidationController {

    @Autowired
    private ValidationService validationService;

    /**
     * Valider une analyse LLM (Analyste uniquement)
     */
    @PostMapping("/analyse/{idAnalyse}")
    @PreAuthorize("hasAnyRole('Analyste', 'Décideur')")
    public ResponseEntity<?> validerAnalyse(
            @RequestHeader("Authorization") String token,
            @PathVariable Long idAnalyse,
            @RequestBody ValidationRequest request) {
        try {
            Validation validation = validationService.validerAnalyse(token, idAnalyse, request);
            return ResponseEntity.ok(Map.of(
                    "message", "Analyse " + request.getStatut() + " avec succès",
                    "validation", validation
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Modifier une validation existante (Analyste uniquement)
     */
    @PutMapping("/{idValidation}")
    @PreAuthorize("hasAnyRole('Analyste', 'Décideur')")
    public ResponseEntity<?> modifierValidation(
            @RequestHeader("Authorization") String token,
            @PathVariable Long idValidation,
            @RequestBody ValidationRequest request) {
        try {
            Validation validation = validationService.modifierValidation(token, idValidation, request);
            return ResponseEntity.ok(Map.of(
                    "message", "Validation modifiée avec succès",
                    "validation", validation
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Récupérer toutes les validations (Analyste et Décideur)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('Analyste', 'Décideur')")
    public ResponseEntity<List<Validation>> getToutesLesValidations() {
        return ResponseEntity.ok(validationService.getToutesLesValidations());
    }

    /**
     * Récupérer les validations par statut
     */
    @GetMapping("/statut/{statut}")
    @PreAuthorize("hasAnyRole('Analyste', 'Décideur')")
    public ResponseEntity<List<Validation>> getValidationsParStatut(@PathVariable Validation.Statut statut) {
        return ResponseEntity.ok(validationService.getValidationsParStatut(statut));
    }

    /**
     * Récupérer mes validations (Analyste)
     */
    @GetMapping("/mes-validations")
    @PreAuthorize("hasAnyRole('Analyste', 'Décideur')")
    public ResponseEntity<?> getMesValidations(@RequestHeader("Authorization") String token) {
        try {
            List<Validation> validations = validationService.getValidationsParAnalyste(token);
            return ResponseEntity.ok(validations);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Récupérer la validation d'une analyse spécifique
     */
    @GetMapping("/analyse/{idAnalyse}")
    @PreAuthorize("hasAnyRole('Analyste', 'Décideur')")
    public ResponseEntity<?> getValidationParAnalyse(@PathVariable Long idAnalyse) {
        try {
            Validation validation = validationService.getValidationParAnalyse(idAnalyse);
            return ResponseEntity.ok(validation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Supprimer une validation (seulement si en_attente)
     */
    @DeleteMapping("/{idValidation}")
    @PreAuthorize("hasAnyRole('Analyste', 'Décideur')")
    public ResponseEntity<?> supprimerValidation(@PathVariable Long idValidation) {
        try {
            validationService.supprimerValidation(idValidation);
            return ResponseEntity.ok(Map.of("message", "Validation supprimée avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Statistiques de validation
     */
    @GetMapping("/statistiques")
    @PreAuthorize("hasAnyRole('Analyste', 'Décideur')")
    public ResponseEntity<?> getStatistiques() {
        return ResponseEntity.ok(validationService.getStatistiquesValidation());
    }
}