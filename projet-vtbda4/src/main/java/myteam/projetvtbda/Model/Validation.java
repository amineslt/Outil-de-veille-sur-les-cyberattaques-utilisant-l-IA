package myteam.projetvtbda.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "validation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Validation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_validation")
    private Long idValidation;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private Statut statut;

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "type_modif", length = 200)
    private String typeModif;

    @CreationTimestamp
    @Column(name = "date_valid", nullable = false, updatable = false)
    private LocalDateTime dateValid;

    //  CORRECTION : Ignorer analyseLlm pour éviter la boucle infinie
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_analyse", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_validation_analyse_llm"))
    @JsonIgnoreProperties({"validation", "article"})
    private AnalyseLlm analyseLlm;

    // Relation N:1 avec Utilisateur (analyste)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_analyste", nullable = false, foreignKey = @ForeignKey(name = "fk_validation_analyste"))
    @JsonIgnoreProperties({"fluxRssList", "motsClesList", "validations", "rapports", "motDePasse"})
    private Utilisateur analyste;

    // Enum pour le statut
    public enum Statut {
        validé,
        rejeté
    }
}