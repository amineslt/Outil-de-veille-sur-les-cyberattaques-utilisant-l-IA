package myteam.projetvtbda.controller;

import myteam.projetvtbda.dto.CreerUtilisateurRequest;
import myteam.projetvtbda.dto.ChangerRoleRequest;
import myteam.projetvtbda.Model.Utilisateur;
import myteam.projetvtbda.Service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "*")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    /**
     * Inscription publique (TOUJOURS rôle Visiteur)
     */
    @PostMapping("/inscription")
    public ResponseEntity<?> inscription(@RequestBody CreerUtilisateurRequest request) {
        try {
            // FORCER le rôle à Visiteur (ignore ce que l'utilisateur envoie)
            Utilisateur utilisateur = utilisateurService.inscrireVisiteur(request);

            return ResponseEntity.ok(Map.of(
                    "message", "Inscription réussie. Votre compte a été créé avec le rôle Visiteur.",
                    "utilisateur", Map.of(
                            "id", utilisateur.getIdUtilisateur(),
                            "nom", utilisateur.getNom(),
                            "prenom", utilisateur.getPrenom(),
                            "email", utilisateur.getEmail(),
                            "role", utilisateur.getRole()
                    )
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }
    /**
     * Attribuer/Modifier le rôle d'un utilisateur (Décideur uniquement)
     * Ne peut PAS créer de Décideur via cette route
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('Décideur')")
    public ResponseEntity<?> changerRole(
            @PathVariable Long id,
            @RequestBody ChangerRoleRequest request) {
        try {
            Utilisateur utilisateur = utilisateurService.changerRole(id, request.getRole());
            return ResponseEntity.ok(Map.of(
                    "message", "Rôle modifié avec succès",
                    "utilisateur", utilisateur
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Créer un nouveau Décideur (Décideur uniquement)
     * Route spéciale et sécurisée
     */
   /** @PostMapping("/creer-decideur")
    @PreAuthorize("hasRole('Décideur')")
    public ResponseEntity<?> creerDecideur(@RequestBody CreerUtilisateurRequest request) {
        try {
            //  FORCER le rôle à Décideur
            request.setRole(Utilisateur.Role.Décideur);

            Utilisateur utilisateur = utilisateurService.creerUtilisateur(request);
            return ResponseEntity.ok(Map.of(
                    "message", "Nouveau Décideur créé avec succès",
                    "utilisateur", utilisateur
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }
    **/
    /**
     * Récupérer tous les utilisateurs (Décideur uniquement)
     */
    @GetMapping("/profiles")
    @PreAuthorize("hasRole('Décideur')")
    public ResponseEntity<List<Utilisateur>> getTousLesUtilisateurs() {
        return ResponseEntity.ok(utilisateurService.getTousLesUtilisateurs());
    }

    /**
     * Récupérer les utilisateurs par rôle (Décideur uniquement)
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('Décideur')")
    public ResponseEntity<List<Utilisateur>> getUtilisateursParRole(@PathVariable Utilisateur.Role role) {
        return ResponseEntity.ok(utilisateurService.getUtilisateursParRole(role));
    }

    /**
     * Modifier son propre profil (tout utilisateur authentifié)
     */
    @PutMapping("/profil")
    public ResponseEntity<?> modifierProfil(
            @RequestHeader("Authorization") String token,
            @RequestBody CreerUtilisateurRequest request) {
        try {
            Utilisateur utilisateur = utilisateurService.modifierProfilUtilisateur(token, request);
            return ResponseEntity.ok(Map.of(
                    "message", "Profil modifié avec succès",
                    "utilisateur", utilisateur
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Supprimer un utilisateur (Décideur uniquement)
     * Ne peut PAS supprimer un Décideur
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Décideur')")
    public ResponseEntity<?> supprimerUtilisateur(@PathVariable Long id) {
        try {
            utilisateurService.supprimerUtilisateur(id);
            return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }
}