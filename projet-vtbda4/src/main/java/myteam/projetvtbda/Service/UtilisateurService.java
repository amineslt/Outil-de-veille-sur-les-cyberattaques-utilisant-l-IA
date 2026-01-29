package myteam.projetvtbda.Service;

import myteam.projetvtbda.dto.CreerUtilisateurRequest;
import myteam.projetvtbda.Model.Utilisateur;
import myteam.projetvtbda.Repository.UtilisateurRepository;
import myteam.projetvtbda.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Inscription publique (TOUJOURS Visiteur)
     */
    public Utilisateur inscrireVisiteur(CreerUtilisateurRequest request) {
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));

        //  TOUJOURS Visiteur pour l'inscription publique
        utilisateur.setRole(Utilisateur.Role.Visiteur);

        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Créer un utilisateur avec un rôle spécifique (Décideur uniquement)
     */
    public Utilisateur creerUtilisateur(CreerUtilisateurRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setRole(request.getRole());

        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Changer le rôle d'un utilisateur
     */
    public Utilisateur changerRole(Long idUtilisateur, Utilisateur.Role nouveauRole) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        //  Empêcher de changer un Décideur
        if (utilisateur.getRole() == Utilisateur.Role.Décideur) {
            throw new RuntimeException("Impossible de modifier le rôle d'un Décideur");
        }

        utilisateur.setRole(nouveauRole);
        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Récupérer tous les utilisateurs
     */
    public List<Utilisateur> getTousLesUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    /**
     * Récupérer les utilisateurs par rôle
     */
    public List<Utilisateur> getUtilisateursParRole(Utilisateur.Role role) {
        return utilisateurRepository.findByRole(role);
    }

    /**
     * Supprimer un utilisateur
     */
    public void supprimerUtilisateur(Long idUtilisateur) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        //  Empêcher de supprimer un Décideur
        if (utilisateur.getRole() == Utilisateur.Role.Décideur) {
            throw new RuntimeException("Impossible de supprimer un Décideur");
        }

        utilisateurRepository.deleteById(idUtilisateur);
    }

    /**
     * Modifier son propre profil (sans changer le rôle)
     */
    public Utilisateur modifierProfilUtilisateur(String token, CreerUtilisateurRequest request) {
        String jwt = token.substring(7);
        String email = jwtUtil.extractUsername(jwt);

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());

        if (!utilisateur.getEmail().equals(request.getEmail())) {
            if (utilisateurRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Cet email est déjà utilisé");
            }
            utilisateur.setEmail(request.getEmail());
        }

        if (request.getMotDePasse() != null && !request.getMotDePasse().isEmpty()) {
            utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        }

        // Le rôle ne change JAMAIS via cette route
        return utilisateurRepository.save(utilisateur);
    }
}


