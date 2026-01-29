package myteam.projetvtbda.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "utilisateur")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Long idUtilisateur;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @JsonIgnore
    @Column(name = "mot_de_passe", nullable = false, length = 255)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    // Relations
    @JsonIgnore
    @OneToMany(mappedBy = "veilleur", cascade = CascadeType.ALL)
    private List<FluxRss> fluxRssList;

    @JsonIgnore
    @OneToMany(mappedBy = "veilleur", cascade = CascadeType.ALL)
    private List<MotsCles> motsClesList;

    @JsonIgnore
    @OneToMany(mappedBy = "analyste", cascade = CascadeType.ALL)
    private List<Validation> validations;

    @JsonIgnore
    @OneToMany(mappedBy = "decideur", cascade = CascadeType.ALL)
    private List<Rapport> rapports;

    // Enum pour les rôles
    public enum Role {
        Visiteur,
        Veilleur,
        Analyste,
        Décideur
    }
}