package myteam.projetvtbda.Service;

import myteam.projetvtbda.Model.Utilisateur;
import myteam.projetvtbda.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Utilisateur> userOptional = utilisateurRepository.findByEmail(email);

        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("Utilisateur non trouvé : " + email);
        }

        Utilisateur user = userOptional.get();

        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getMotDePasse())
                .roles(user.getRole().name())
                .build();
    }

    public Utilisateur findUserByEmail(String email) {
        return utilisateurRepository.findByEmail(email).orElse(null);
    }

    public Utilisateur.Role getRoleByEmail(String email) {
        Optional<Utilisateur> userOptional = utilisateurRepository.findByEmail(email);
        return userOptional.map(Utilisateur::getRole).orElse(null);
    }

    public String changePassword(String email, String oldPassword, String newPassword) {
        Optional<Utilisateur> userOptional = utilisateurRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            return "Utilisateur non trouvé";
        }

        Utilisateur user = userOptional.get();

        if (!passwordEncoder.matches(oldPassword, user.getMotDePasse())) {
            return "Ancien mot de passe incorrect";
        }

        user.setMotDePasse(passwordEncoder.encode(newPassword));
        utilisateurRepository.save(user);

        return "Mot de passe modifié avec succès";
    }
}