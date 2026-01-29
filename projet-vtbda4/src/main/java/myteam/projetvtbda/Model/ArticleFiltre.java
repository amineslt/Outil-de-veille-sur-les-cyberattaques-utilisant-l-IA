package myteam.projetvtbda.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "article_filtre")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleFiltre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_filtre")
    private Long idFiltre;

    @Column(name = "pertinent", nullable = false)
    private Boolean pertinent;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "mots_cles", columnDefinition = "json")
    private List<String> motsCles;

    @Column(name = "score_match")
    private Integer scoreMatch;

    @CreationTimestamp
    @Column(name = "date_filtrage", nullable = false)
    private LocalDateTime dateFiltrage;

    // Relation 1:1 avec Article
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_article", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_article_filtre_article"))
    private Article article;

    // Contrainte de validation
    @PrePersist
    @PreUpdate
    private void validateScoreMatch() {
        if (scoreMatch != null && (scoreMatch < 0 || scoreMatch > 100)) {
            throw new IllegalArgumentException("Le score_match doit être entre 0 et 100");
        }
    }
}
