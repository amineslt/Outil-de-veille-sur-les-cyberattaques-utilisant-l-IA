package myteam.projetvtbda.Service;

import myteam.projetvtbda.Model.AnalyseLlm;
import myteam.projetvtbda.Model.Utilisateur;
import myteam.projetvtbda.Model.Validation;
import myteam.projetvtbda.Repository.AnalyseLlmRepository;
import myteam.projetvtbda.Repository.UtilisateurRepository;
import myteam.projetvtbda.Repository.ValidationRepository;
import myteam.projetvtbda.dto.ValidationRequest;
import myteam.projetvtbda.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ValidationService {

    @Autowired
    private ValidationRepository validationRepository;

    @Autowired
    private AnalyseLlmRepository analyseLlmRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Valider une analyse LLM
     */
    @Transactional
    public Validation validerAnalyse(String token, Long idAnalyse, ValidationRequest request) {
        String jwt = token.substring(7);
        String email = jwtUtil.extractUsername(jwt);

        Utilisateur analyste = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        AnalyseLlm analyseLlm = analyseLlmRepository.findById(idAnalyse)
                .orElseThrow(() -> new RuntimeException("Analyse non trouvée"));

        // Vérifier si déjà validée
        if (analyseLlm.getValidation() != null) {
            throw new RuntimeException("Cette analyse a déjà été validée");
        }

        // Appliquer les modifications si demandées
        if (request.getTypeModif() != null && !request.getTypeModif().isEmpty()) {
            appliquerModifications(analyseLlm, request);
        }

        // Créer la validation
        Validation validation = new Validation();
        validation.setAnalyseLlm(analyseLlm);
        validation.setAnalyste(analyste);
        validation.setStatut(request.getStatut());
        validation.setCommentaire(request.getCommentaire());
        validation.setTypeModif(request.getTypeModif());

        Validation savedValidation = validationRepository.save(validation);

        System.out.println(" Analyse " + idAnalyse + " " + request.getStatut() + " par " + analyste.getPrenom());

        return savedValidation;
    }

    /**
     * Appliquer les modifications à l'analyse avant validation
     */
    private void appliquerModifications(AnalyseLlm analyseLlm, ValidationRequest request) {
        if (request.getNouveauTypeAttaque() != null) {
            analyseLlm.setTypeAttaque(request.getNouveauTypeAttaque());
        }
        if (request.getNouveauNiveauRisque() != null) {
            analyseLlm.setNiveauRisque(request.getNouveauNiveauRisque());
        }
        if (request.getNouvelleSophistication() != null) {
            analyseLlm.setSophistication(request.getNouvelleSophistication());
        }
        if (request.getNouveauResume() != null) {
            analyseLlm.setResume(request.getNouveauResume());
        }

        analyseLlmRepository.save(analyseLlm);
    }

    /**
     * Modifier une validation existante
     */
    @Transactional
    public Validation modifierValidation(String token, Long idValidation, ValidationRequest request) {
        String jwt = token.substring(7);
        String email = jwtUtil.extractUsername(jwt);

        Utilisateur analyste = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Validation validation = validationRepository.findById(idValidation)
                .orElseThrow(() -> new RuntimeException("Validation non trouvée"));

        // Vérifier que c'est le même analyste
        if (!validation.getAnalyste().getIdUtilisateur().equals(analyste.getIdUtilisateur())) {
            throw new RuntimeException("Vous ne pouvez modifier que vos propres validations");
        }

        // Mettre à jour
        validation.setStatut(request.getStatut());
        validation.setCommentaire(request.getCommentaire());

        if (request.getTypeModif() != null) {
            validation.setTypeModif(request.getTypeModif());
            appliquerModifications(validation.getAnalyseLlm(), request);
        }

        return validationRepository.save(validation);
    }

    /**
     * Récupérer toutes les validations
     */
    public List<Validation> getToutesLesValidations() {
        return validationRepository.findAll();
    }

    /**
     * Récupérer les validations par statut
     */
    public List<Validation> getValidationsParStatut(Validation.Statut statut) {
        return validationRepository.findByStatut(statut);
    }

    /**
     * Récupérer les validations d'un analyste
     */
    public List<Validation> getValidationsParAnalyste(String token) {
        String jwt = token.substring(7);
        String email = jwtUtil.extractUsername(jwt);

        Utilisateur analyste = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return validationRepository.findByAnalysteIdUtilisateur(analyste.getIdUtilisateur());
    }

    /**
     * Récupérer une validation par ID d'analyse
     */
    public Validation getValidationParAnalyse(Long idAnalyse) {
        return validationRepository.findByAnalyseLlmIdAnalyse(idAnalyse)
                .orElseThrow(() -> new RuntimeException("Aucune validation trouvée pour cette analyse"));
    }

    /**
     * Supprimer une validation
     */
    @Transactional
    public void supprimerValidation(Long idValidation) {
        Validation validation = validationRepository.findById(idValidation)
                .orElseThrow(() -> new RuntimeException("Validation non trouvée"));

        validationRepository.deleteById(idValidation);
    }

    /**
     * Statistiques de validation
     */
    public java.util.Map<String, Object> getStatistiquesValidation() {
        long total = validationRepository.count();
        long validees = validationRepository.countByStatut(Validation.Statut.validé);
        long rejetees = validationRepository.countByStatut(Validation.Statut.rejeté);

        return java.util.Map.of(
                "total_validations", total,
                "validees", validees,
                "rejetees", rejetees,
                "taux_validation", total > 0 ? (validees * 100.0 / total) : 0
        );
    }
}