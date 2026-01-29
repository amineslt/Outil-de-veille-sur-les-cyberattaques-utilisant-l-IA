package myteam.projetvtbda.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "analyse_llm")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AnalyseLlm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_analyse")
    private Long idAnalyse;

    @Column(name = "resume", nullable = false, columnDefinition = "TEXT")
    private String resume;

    @Column(name = "type_attaque", length = 100)
    private String typeAttaque;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_risque", nullable = false)
    private NiveauRisque niveauRisque;

    @Enumerated(EnumType.STRING)
    @Column(name = "sophistication", nullable = false)
    private Sophistication sophistication;

    @CreationTimestamp
    @Column(name = "date_analyse", nullable = false, updatable = false)
    private LocalDateTime dateAnalyse;

    @Column(name = "modele_llm", length = 100)
    private String modeleLlm;

    // Relation 1:1 avec Article
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_article", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_analyse_llm_article"))
    @JsonIgnoreProperties({"analyseLlm", "articleFiltre"})
    private Article article;


    @OneToOne(mappedBy = "analyseLlm", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"analyseLlm"})
    private Validation validation;

    // Enums
    public enum NiveauRisque {
        faible,
        moyen,
        élevé
    }

    public enum Sophistication {
        faible,
        moyen,
        élevé
    }
}