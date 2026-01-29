package myteam.projetvtbda.Service;

import myteam.projetvtbda.Model.FluxRss;
import myteam.projetvtbda.Model.Utilisateur;
import myteam.projetvtbda.Repository.FluxRssRepository;
import myteam.projetvtbda.Repository.UtilisateurRepository;
import myteam.projetvtbda.dto.FluxRssRequest;
import myteam.projetvtbda.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FluxRssService {

    @Autowired
    private FluxRssRepository fluxRssRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Créer un nouveau flux RSS (Veilleur uniquement)
     */
    public FluxRss creerFluxRss(String token, FluxRssRequest request) {
        String jwt = token.substring(7);
        String email = jwtUtil.extractUsername(jwt);

        Utilisateur veilleur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si l'URL existe déjà
        if (fluxRssRepository.existsByUrlFlux(request.getUrlFlux())) {
            throw new RuntimeException("Cette URL de flux RSS existe déjà");
        }

        FluxRss fluxRss = new FluxRss();
        fluxRss.setNomFlux(request.getNomFlux());
        fluxRss.setUrlFlux(request.getUrlFlux());
        fluxRss.setDescription(request.getDescription());
        fluxRss.setStatut(FluxRss.Statut.actif);
        fluxRss.setVeilleur(veilleur);

        return fluxRssRepository.save(fluxRss);
    }

    /**
     * Récupérer tous les flux RSS
     */
    public List<FluxRss> getTousLesFlux() {
        return fluxRssRepository.findAll();
    }

    /**
     * Récupérer les flux par statut
     */
    public List<FluxRss> getFluxParStatut(FluxRss.Statut statut) {
        return fluxRssRepository.findByStatut(statut);
    }

    /**
     * Récupérer un flux par ID
     */
    public FluxRss getFluxById(Long id) {
        return fluxRssRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flux RSS non trouvé"));
    }

    /**
     * Récupérer les flux d'un veilleur
     */
    public List<FluxRss> getFluxParVeilleur(String token) {
        String jwt = token.substring(7);
        String email = jwtUtil.extractUsername(jwt);

        Utilisateur veilleur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return fluxRssRepository.findByVeilleurIdUtilisateur(veilleur.getIdUtilisateur());
    }

    /**
     * Modifier un flux RSS
     */
    public FluxRss modifierFluxRss(Long id, FluxRssRequest request) {
        FluxRss fluxRss = fluxRssRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flux RSS non trouvé"));

        fluxRss.setNomFlux(request.getNomFlux());
        fluxRss.setDescription(request.getDescription());

        // Ne pas modifier l'URL si elle est différente et existe déjà
        if (!fluxRss.getUrlFlux().equals(request.getUrlFlux())) {
            if (fluxRssRepository.existsByUrlFlux(request.getUrlFlux())) {
                throw new RuntimeException("Cette URL de flux RSS existe déjà");
            }
            fluxRss.setUrlFlux(request.getUrlFlux());
        }

        return fluxRssRepository.save(fluxRss);
    }

    /**
     * Activer/Désactiver un flux
     */
    public FluxRss changerStatutFlux(Long id, FluxRss.Statut statut) {
        FluxRss fluxRss = fluxRssRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flux RSS non trouvé"));

        fluxRss.setStatut(statut);
        return fluxRssRepository.save(fluxRss);
    }

    /**
     * Supprimer un flux RSS
     */
    public void supprimerFluxRss(Long id) {
        if (!fluxRssRepository.existsById(id)) {
            throw new RuntimeException("Flux RSS non trouvé");
        }
        fluxRssRepository.deleteById(id);
    }
}