package myteam.projetvtbda.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mots_cles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MotsCles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mot_cle")
    private Long idMotCle;

    @Column(name = "mot", nullable = false, unique = true, length = 100)
    private String mot;

    @Column(name = "categorie", length = 100)
    private String categorie;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    @CreationTimestamp
    @Column(name = "date_ajout", nullable = false, updatable = false)
    private LocalDateTime dateAjout;

    // Relation N:1 avec Utilisateur (veilleur)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veilleur", nullable = false, foreignKey = @ForeignKey(name = "fk_mots_cles_veilleur"))
    @JsonIgnoreProperties({"fluxRssList", "motsClesList", "validations", "rapports", "motDePasse"})
    private Utilisateur veilleur;
}