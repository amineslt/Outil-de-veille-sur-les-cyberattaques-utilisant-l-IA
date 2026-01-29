package myteam.projetvtbda.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import myteam.projetvtbda.Model.AnalyseLlm;
import myteam.projetvtbda.Model.Validation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidationRequest {
    private Validation.Statut statut; // validé, rejeté
    private String commentaire;
    private String typeModif; // Description des modifications (optionnel)

    // Modifications possibles de l'analyse (optionnelles)
    private String nouveauTypeAttaque;
    private AnalyseLlm.NiveauRisque nouveauNiveauRisque;
    private AnalyseLlm.Sophistication nouvelleSophistication;
    private String nouveauResume;
}