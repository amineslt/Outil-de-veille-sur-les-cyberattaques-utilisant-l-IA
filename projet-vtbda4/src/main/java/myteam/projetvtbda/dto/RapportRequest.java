package myteam.projetvtbda.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import myteam.projetvtbda.Model.AnalyseLlm;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RapportRequest {
    private String titre;
    private LocalDate periodeDebut;
    private LocalDate periodeFin;
    private String typeRapport; // quotidien, hebdomadaire, mensuel, personnalisé

    // Filtres optionnels
    private AnalyseLlm.NiveauRisque niveauRisqueMin; // Inclure seulement les menaces >= ce niveau
    private String typeAttaque; // Filtrer par type d'attaque spécifique
    private Integer limitArticles; // Nombre max d'articles à inclure (ex: top 10)
}