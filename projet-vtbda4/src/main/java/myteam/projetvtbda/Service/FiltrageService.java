package myteam.projetvtbda.Service;

import myteam.projetvtbda.Model.Article;
import myteam.projetvtbda.Model.ArticleFiltre;
import myteam.projetvtbda.Model.MotsCles;
import myteam.projetvtbda.Repository.ArticleFiltreRepository;
import myteam.projetvtbda.Repository.ArticleRepository;
import myteam.projetvtbda.Repository.MotsClesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class FiltrageService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleFiltreRepository articleFiltreRepository;

    @Autowired
    private MotsClesRepository motsClesRepository;

    /**
     * Filtrer tous les articles non filtrés
     */
    public List<ArticleFiltre> filtrerTousLesArticles() {
        List<Article> articlesNonFiltres = articleRepository.findArticlesNonFiltres();
        List<ArticleFiltre> filtres = new ArrayList<>();

        System.out.println(" Début du filtrage de " + articlesNonFiltres.size() + " articles");

        for (Article article : articlesNonFiltres) {
            ArticleFiltre filtre = filtrerArticle(article);
            filtres.add(filtre);
        }

        System.out.println("Filtrage terminé : " + filtres.size() + " articles filtrés");
        return filtres;
    }

    /**
     * Filtrer un article spécifique
     */
    public ArticleFiltre filtrerArticle(Article article) {
        List<MotsCles> motsClesActifs = motsClesRepository.findByActif(true);

        List<String> motsTrouves = new ArrayList<>();
        int scoreMatch = 0;

        String texteArticle = (article.getTitre() + " " + article.getDescription()).toLowerCase();

        // Recherche des mots-clés dans le texte
        for (MotsCles motCle : motsClesActifs) {
            String mot = motCle.getMot().toLowerCase();

            // Chercher le mot complet avec des limites de mots
            // \b = word boundary (début ou fin de mot)
            String regex = "\\b" + Pattern.quote(mot) + "\\b";
            Pattern pattern = Pattern.compile(regex);

            if (pattern.matcher(texteArticle).find()) {
                motsTrouves.add(motCle.getMot());
                // Score : +20 points par mot-clé trouvé
                scoreMatch += 20;
            }
        }

        // Calculer le score final (max 100)
        scoreMatch = Math.min(scoreMatch, 100);

        // Déterminer si l'article est pertinent (score > 50)
        boolean pertinent = scoreMatch > 30;

        // Créer l'enregistrement de filtrage
        ArticleFiltre filtre = new ArticleFiltre();
        filtre.setArticle(article);
        filtre.setPertinent(pertinent);
        filtre.setMotsCles(motsTrouves);
        filtre.setScoreMatch(scoreMatch);

        ArticleFiltre savedFiltre = articleFiltreRepository.save(filtre);

        String titreCoupe = article.getTitre().length() > 50 ?
                article.getTitre().substring(0, 50) + "..." :
                article.getTitre();

        System.out.println((pertinent ? "OUI" : "NON") + " Article '" + titreCoupe + "' - Score: " + scoreMatch + " - Mots: " + motsTrouves);

        return savedFiltre;
    }

    /**
     * Obtenir les articles pertinents non analysés
     */
    public List<Article> getArticlesPertinentsNonAnalyses() {
        return articleRepository.findArticlesPertinentsNonAnalyses();
    }
}