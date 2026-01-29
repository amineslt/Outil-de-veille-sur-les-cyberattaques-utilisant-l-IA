package myteam.projetvtbda.config;

import myteam.projetvtbda.Model.Utilisateur;
import myteam.projetvtbda.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Créer le premier décideur si aucun n'existe
        List<Utilisateur> decideurs = utilisateurRepository.findByRole(Utilisateur.Role.Décideur);

        if (decideurs.isEmpty()) {
            Utilisateur admin = new Utilisateur();
            admin.setNom("Admin");
            admin.setPrenom("Super");
            admin.setEmail("admin@veille.dz");
            admin.setMotDePasse(passwordEncoder.encode("Admin123!"));
            admin.setRole(Utilisateur.Role.Décideur);

            utilisateurRepository.save(admin);
            System.out.println("Premier décideur créé avec succès : admin@veille.dz");
        } else {
            System.out.println("Un décideur existe déjà dans la base de données");
        }
    }
}
