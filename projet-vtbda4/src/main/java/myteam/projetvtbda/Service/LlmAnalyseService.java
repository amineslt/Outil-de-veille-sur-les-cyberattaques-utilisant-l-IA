package myteam.projetvtbda.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import myteam.projetvtbda.Model.AnalyseLlm;
import myteam.projetvtbda.Model.Article;
import myteam.projetvtbda.Model.FluxRss;
import myteam.projetvtbda.Repository.AnalyseLlmRepository;
import myteam.projetvtbda.Repository.ArticleRepository;
import myteam.projetvtbda.Repository.FluxRssRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LlmAnalyseService {

    @Autowired
    private LlmService llmService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private AnalyseLlmRepository analyseLlmRepository;

    @Autowired
    private FiltrageService filtrageService;

    @Autowired
    private FluxRssRepository fluxRssRepository;

    /**
     * Analyser tous les articles pertinents non analysés
     */
    public List<AnalyseLlm> analyserTousLesArticlesPertinents() {
        List<Article> articles = filtrageService.getArticlesPertinentsNonAnalyses();
        List<AnalyseLlm> analyses = new ArrayList<>();

        System.out.println(" Début de l'analyse LLM de " + articles.size() + " articles pertinents");

        for (Article article : articles) {
            try {
                AnalyseLlm analyse = analyserArticle(article);
                analyses.add(analyse);
                Thread.sleep(1000); // Pause pour éviter le rate limiting
            } catch (Exception e) {
                System.err.println("Erreur lors de l'analyse de l'article " + article.getIdArticle() + " : " + e.getMessage());
            }
        }

        System.out.println("Analyse terminée : " + analyses.size() + " articles analysés");
        return analyses;
    }

    /**
     * Analyser un article spécifique
     */
    public AnalyseLlm analyserArticle(Article article) {
        // Créer le prompt pour Perplexity
        String prompt = creerPromptAnalyse(article);

        // Appeler l'API Perplexity
        String reponse = llmService.poserQuestion(prompt);

        // Parser la réponse
        AnalyseLlm analyse = parserReponseAnalyse(reponse, article);

        // Sauvegarder l'analyse
        AnalyseLlm savedAnalyse = analyseLlmRepository.save(analyse);

        System.out.println("Analyse créée pour l'article : " + article.getTitre().substring(0, Math.min(50, article.getTitre().length())));

        return savedAnalyse;
    }

    /**
     * Créer le prompt d'analyse
     */
    private String creerPromptAnalyse(Article article) {
        return String.format("""
            Analyse cet article de cybersécurité sur les cyberattaques utilisant l'IA.
            
            Titre: %s
            Description: %s
            
            Fournis UNIQUEMENT un JSON avec cette structure exacte (sans markdown, sans ```json):
            {
                "resume": "résumé en 2-3 phrases",
                "type_attaque": "type de l'attaque (ex: Deepfake phishing, Phishing automatisé, etc.)",
                "niveau_risque": "faible|moyen|élevé",
                "sophistication": "faible|moyen|élevé"
            }
            
            Réponds UNIQUEMENT avec le JSON, rien d'autre.
            """, article.getTitre(), article.getDescription());
    }

    /**
     * Parser la réponse LLM en objet AnalyseLlm
     */
    private AnalyseLlm parserReponseAnalyse(String reponse, Article article) {
        try {
            // Nettoyer la réponse (enlever les balises markdown si présentes)
            String jsonStr = reponse.trim();
            if (jsonStr.startsWith("```json")) {
                jsonStr = jsonStr.substring(7);
            }
            if (jsonStr.startsWith("```")) {
                jsonStr = jsonStr.substring(3);
            }
            if (jsonStr.endsWith("```")) {
                jsonStr = jsonStr.substring(0, jsonStr.length() - 3);
            }
            jsonStr = jsonStr.trim();

            // Parser le JSON
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> data = mapper.readValue(jsonStr, Map.class);

            // Créer l'analyse
            AnalyseLlm analyse = new AnalyseLlm();
            analyse.setArticle(article);
            analyse.setResume(data.get("resume"));
            analyse.setTypeAttaque(data.get("type_attaque"));
            analyse.setNiveauRisque(parseNiveauRisque(data.get("niveau_risque")));
            analyse.setSophistication(parseSophistication(data.get("sophistication")));
            analyse.setModeleLlm("perplexity-sonar");

            return analyse;

        } catch (Exception e) {
            System.err.println(" Erreur lors du parsing de la réponse LLM, utilisation de valeurs par défaut");

            // Valeurs par défaut en cas d'erreur
            AnalyseLlm analyse = new AnalyseLlm();
            analyse.setArticle(article);
            analyse.setResume(extraireResumeSimple(reponse));
            analyse.setTypeAttaque("Cyberattaque IA");
            analyse.setNiveauRisque(AnalyseLlm.NiveauRisque.moyen);
            analyse.setSophistication(AnalyseLlm.Sophistication.moyen);
            analyse.setModeleLlm("perplexity-sonar");

            return analyse;
        }
    }

    /**
     * Parser le niveau de risque
     */
    private AnalyseLlm.NiveauRisque parseNiveauRisque(String niveau) {
        if (niveau == null) return AnalyseLlm.NiveauRisque.moyen;

        switch (niveau.toLowerCase()) {
            case "faible":
                return AnalyseLlm.NiveauRisque.faible;
            case "élevé":
            case "eleve":
            case "elevé":
            case "high":
                return AnalyseLlm.NiveauRisque.élevé;
            default:
                return AnalyseLlm.NiveauRisque.moyen;
        }
    }

    /**
     * Parser la sophistication
     */
    private AnalyseLlm.Sophistication parseSophistication(String sophistication) {
        if (sophistication == null) return AnalyseLlm.Sophistication.moyen;

        switch (sophistication.toLowerCase()) {
            case "faible":
            case "low":
                return AnalyseLlm.Sophistication.faible;
            case "élevé":
            case "eleve":
            case "elevé":
            case "high":
                return AnalyseLlm.Sophistication.élevé;
            default:
                return AnalyseLlm.Sophistication.moyen;
        }
    }

    /**
     * Extraire un résumé simple si le parsing JSON échoue
     */
    private String extraireResumeSimple(String texte) {
        if (texte.length() > 300) {
            return texte.substring(0, 297) + "...";
        }
        return texte;
    }

    /**
     * Récupérer toutes les analyses VALIDÉES d'un flux RSS spécifique
     */
    public List<AnalyseLlm> getAnalysesValideesParFlux(Long idFlux) {
        // Vérifier que le flux existe
        FluxRss flux = fluxRssRepository.findById(idFlux)
                .orElseThrow(() -> new RuntimeException("Flux RSS non trouvé avec l'ID : " + idFlux));

        return analyseLlmRepository.findAnalysesValideesParFluxRss(idFlux);
    }

    /**
     * Récupérer TOUTES les analyses d'un flux RSS (validées + non validées)
     */
    public List<AnalyseLlm> getToutesLesAnalysesParFlux(Long idFlux) {
        // Vérifier que le flux existe
        FluxRss flux = fluxRssRepository.findById(idFlux)
                .orElseThrow(() -> new RuntimeException("Flux RSS non trouvé avec l'ID : " + idFlux));

        return analyseLlmRepository.findAnalysesParFluxRss(idFlux);
    }

    /**
     * Récupérer les analyses validées d'un flux RSS par niveau de risque
     */
    public List<AnalyseLlm> getAnalysesValideesParFluxEtRisque(Long idFlux, AnalyseLlm.NiveauRisque niveauRisque) {
        // Vérifier que le flux existe
        FluxRss flux = fluxRssRepository.findById(idFlux)
                .orElseThrow(() -> new RuntimeException("Flux RSS non trouvé avec l'ID : " + idFlux));

        return analyseLlmRepository.findAnalysesValideesParFluxRssEtNiveauRisque(idFlux, niveauRisque);
    }
}