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
@Table(name = "flux_rss")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "articles"})
public class FluxRss {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_flux")
    private Long idFlux;

    @Column(name = "nom_flux", nullable = false, length = 200)
    private String nomFlux;

    @Column(name = "url_flux", nullable = false, unique = true, length = 500)
    private String urlFlux;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private Statut statut = Statut.actif;

    @CreationTimestamp
    @Column(name = "date_ajout", nullable = false, updatable = false)
    private LocalDateTime dateAjout;

    @Column(name = "derniere_maj")
    private LocalDateTime derniereMaj;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veilleur", nullable = false, foreignKey = @ForeignKey(name = "fk_flux_rss_veilleur"))
    @JsonIgnoreProperties({"fluxRssList", "motsClesList", "validations", "rapports", "motDePasse"})
    private Utilisateur veilleur;

    @OneToMany(mappedBy = "fluxRss", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"fluxRss"})
    private List<Article> articles;

    // Enum
    public enum Statut {
        actif,
        inactif
    }
}