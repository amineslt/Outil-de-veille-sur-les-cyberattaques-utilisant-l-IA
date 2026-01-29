package myteam.projetvtbda.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "article")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_article")
    private Long idArticle;

    @Column(name = "titre", nullable = false, length = 500)
    private String titre;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "url_source", nullable = false, unique = true, length = 500)
    private String urlSource;

    @Column(name = "date_pub")
    private LocalDateTime datePub;

    @CreationTimestamp
    @Column(name = "date_collecte", nullable = false, updatable = false)
    private LocalDateTime dateCollecte;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_flux", nullable = false, foreignKey = @ForeignKey(name = "fk_article_flux_rss"))
    @JsonIgnoreProperties({"articles", "veilleur"})
    private FluxRss fluxRss;

    @OneToOne(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"article"})
    private ArticleFiltre articleFiltre;

    @OneToOne(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"article"})
    private AnalyseLlm analyseLlm;

    //  Ignorer article dans rapportArticles pour éviter boucle
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"article", "rapport"})
    private List<RapportArticle> rapportArticles;
}