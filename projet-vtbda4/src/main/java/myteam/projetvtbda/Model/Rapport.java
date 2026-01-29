package myteam.projetvtbda.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rapport")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rapport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rapport")
    private Long idRapport;

    @Column(name = "titre", nullable = false, length = 255)
    private String titre;

    @Column(name = "periode_debut", nullable = false)
    private LocalDate periodeDebut;

    @Column(name = "periode_fin", nullable = false)
    private LocalDate periodeFin;

    @Column(name = "contenu", columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "type_rapport", length = 50)
    private String typeRapport;

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    // Relation N:1 avec Utilisateur (décideur)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_decideur", nullable = false, foreignKey = @ForeignKey(name = "fk_rapport_decideur"))
    private Utilisateur decideur;

    // Relation N:M avec Article via RapportArticle
    @OneToMany(mappedBy = "rapport", cascade = CascadeType.ALL)
    private List<RapportArticle> rapportArticles;
}

