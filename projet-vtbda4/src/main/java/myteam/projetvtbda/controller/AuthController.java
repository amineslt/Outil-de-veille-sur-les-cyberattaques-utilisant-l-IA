package myteam.projetvtbda.controller;

import myteam.projetvtbda.dto.AuthRequest;
import myteam.projetvtbda.dto.JwtResponse;
import myteam.projetvtbda.Model.Utilisateur;
import myteam.projetvtbda.Service.CustomUserDetailsService;
import myteam.projetvtbda.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            System.out.println(" Tentative de connexion pour : " + authRequest.getEmail());

            // Vérifier si l'utilisateur existe
            Utilisateur user = userDetailsService.findUserByEmail(authRequest.getEmail());
            if (user == null) {
                System.out.println(" Utilisateur non trouvé : " + authRequest.getEmail());
                return ResponseEntity.status(401).body(Map.of("erreur", "Email ou mot de passe incorrect"));
            }

            System.out.println(" Utilisateur trouvé : " + user.getEmail() + " (Rôle: " + user.getRole() + ")");

            // Tenter l'authentification
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getMotDePasse())
            );

            System.out.println(" Authentification réussie");

            final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
            final String jwt = jwtUtil.generateToken(userDetails);

            Utilisateur utilisateur = userDetailsService.findUserByEmail(authRequest.getEmail());
            Utilisateur.Role role = userDetailsService.getRoleByEmail(authRequest.getEmail());

            JwtResponse response = new JwtResponse(jwt, utilisateur, role);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println(" Erreur d'authentification : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).body(Map.of("erreur", "Email ou mot de passe incorrect"));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            String email = jwtUtil.extractUsername(jwt);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (jwtUtil.validateToken(jwt, userDetails)) {
                Utilisateur utilisateur = userDetailsService.findUserByEmail(email);
                return ResponseEntity.ok(Map.of(
                        "email", email,
                        "role", utilisateur.getRole().name()
                ));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erreur", "Token invalide"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erreur", "Token invalide"));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> request) {
        try {
            String jwt = token.substring(7);
            String email = jwtUtil.extractUsername(jwt);

            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            String result = userDetailsService.changePassword(email, oldPassword, newPassword);

            if (result.equals("Mot de passe modifié avec succès")) {
                return ResponseEntity.ok(Map.of("message", result));
            } else {
                return ResponseEntity.badRequest().body(Map.of("erreur", result));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erreur", "Erreur lors du changement de mot de passe"));
        }
    }
}
