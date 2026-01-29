package myteam.projetvtbda.Service;

import myteam.projetvtbda.Model.MotsCles;
import myteam.projetvtbda.Model.Utilisateur;
import myteam.projetvtbda.Repository.MotsClesRepository;
import myteam.projetvtbda.Repository.UtilisateurRepository;
import myteam.projetvtbda.dto.MotsClesRequest;
import myteam.projetvtbda.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MotsClesService {

    @Autowired
    private MotsClesRepository motsClesRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Ajouter un mot-clé (Veilleur uniquement)
     */
    public MotsCles ajouterMotCle(String token, MotsClesRequest request) {
        String jwt = token.substring(7);
        String email = jwtUtil.extractUsername(jwt);

        Utilisateur veilleur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si le mot-clé existe déjà
        if (motsClesRepository.existsByMot(request.getMot())) {
            throw new RuntimeException("Ce mot-clé existe déjà");
        }

        MotsCles motCle = new MotsCles();
        motCle.setMot(request.getMot());
        motCle.setCategorie(request.getCategorie());
        motCle.setActif(true);
        motCle.setVeilleur(veilleur);

        return motsClesRepository.save(motCle);
    }

    /**
     * Récupérer tous les mots-clés
     */
    public List<MotsCles> getTousLesMotsCles() {
        return motsClesRepository.findAll();
    }

    /**
     * Récupérer les mots-clés actifs uniquement
     */
    public List<MotsCles> getMotsClesActifs() {
        return motsClesRepository.findByActif(true);
    }

    /**
     * Récupérer les mots-clés par catégorie
     */
    public List<MotsCles> getMotsClesParCategorie(String categorie) {
        return motsClesRepository.findByCategorie(categorie);
    }

    /**
     * Récupérer un mot-clé par ID
     */
    public MotsCles getMotCleById(Long id) {
        return motsClesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mot-clé non trouvé"));
    }

    /**
     * Récupérer les mots-clés d'un veilleur
     */
    public List<MotsCles> getMotsClesParVeilleur(String token) {
        String jwt = token.substring(7);
        String email = jwtUtil.extractUsername(jwt);

        Utilisateur veilleur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return motsClesRepository.findByVeilleurIdUtilisateur(veilleur.getIdUtilisateur());
    }

    /**
     * Modifier un mot-clé
     */
    public MotsCles modifierMotCle(Long id, MotsClesRequest request) {
        MotsCles motCle = motsClesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mot-clé non trouvé"));

        // Ne pas modifier le mot si il est différent et existe déjà
        if (!motCle.getMot().equals(request.getMot())) {
            if (motsClesRepository.existsByMot(request.getMot())) {
                throw new RuntimeException("Ce mot-clé existe déjà");
            }
            motCle.setMot(request.getMot());
        }

        motCle.setCategorie(request.getCategorie());
        return motsClesRepository.save(motCle);
    }

    /**
     * Activer/Désactiver un mot-clé
     */
    public MotsCles changerStatutMotCle(Long id, Boolean actif) {
        MotsCles motCle = motsClesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mot-clé non trouvé"));

        motCle.setActif(actif);
        return motsClesRepository.save(motCle);
    }

    /**
     * Supprimer un mot-clé
     */
    public void supprimerMotCle(Long id) {
        if (!motsClesRepository.existsById(id)) {
            throw new RuntimeException("Mot-clé non trouvé");
        }
        motsClesRepository.deleteById(id);
    }
}