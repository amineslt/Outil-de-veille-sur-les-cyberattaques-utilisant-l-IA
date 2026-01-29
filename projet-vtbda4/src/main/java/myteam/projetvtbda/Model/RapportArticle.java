package myteam.projetvtbda.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "rapport_article",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_rapport_article",
                columnNames = {"id_rapport", "id_article"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RapportArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //  Ignorer rapportArticles dans rapport pour éviter boucle
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rapport", nullable = false, foreignKey = @ForeignKey(name = "fk_rapport_article_rapport"))
    @JsonIgnoreProperties({"rapportArticles", "decideur"})
    private Rapport rapport;

    //  Ignorer rapportArticles dans article pour éviter boucle
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_article", nullable = false, foreignKey = @ForeignKey(name = "fk_rapport_article_article"))
    @JsonIgnoreProperties({"rapportArticles", "articleFiltre", "analyseLlm", "fluxRss"})
    private Article article;
}