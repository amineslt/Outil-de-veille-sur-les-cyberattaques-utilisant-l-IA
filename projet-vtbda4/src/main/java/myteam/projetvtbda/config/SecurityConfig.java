package myteam.projetvtbda.config;

import myteam.projetvtbda.filter.JwtRequestFilter;
import myteam.projetvtbda.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        //  Endpoints publics (sans authentification)
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/utilisateurs/inscription",
                                "/api/llm/**",
                                "/h2-console/**"
                        ).permitAll()

                        //  Endpoints réservés au Décideur SEULEMENT
                        .requestMatchers(
                                "/api/utilisateurs/profiles",
                                "/api/utilisateurs/role/**",
                                "/api/utilisateurs/*/role",
                                "/api/utilisateurs/creer-decideur"
                        ).hasAnyAuthority("ROLE_Décideur")

                        //  Endpoints FluxRss et MotsCles
                        .requestMatchers(
                                "/api/flux-rss/**",
                                "/api/mots-cles/**"
                        ).hasAnyAuthority("ROLE_Veilleur", "ROLE_Décideur", "ROLE_Analyste","ROLE_Visiteur")

                        //  Endpoints de veille automatique
                        .requestMatchers(
                                "/api/veille/**"
                        ).hasAnyAuthority("ROLE_Veilleur", "ROLE_Analyste", "ROLE_Décideur")

                        //  Endpoints de validation et rapports (NOUVEAU)
                        .requestMatchers(
                                "/api/validations/**",
                                "/api/rapports/**"
                        ).hasAnyAuthority("ROLE_Analyste", "ROLE_Décideur")

                        //  Endpoints de consultation (Articles, Filtres, Analyses)
                        .requestMatchers(
                                "/api/articles/**",
                                "/api/filtres/**",
                                "/api/analyses/**"
                        ).authenticated()

                        //  Tous les autres endpoints nécessitent une authentification
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000", "http://localhost:5173", "*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}