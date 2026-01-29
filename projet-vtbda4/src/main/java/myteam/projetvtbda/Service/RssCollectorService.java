package myteam.projetvtbda.Service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import myteam.projetvtbda.Model.Article;
import myteam.projetvtbda.Model.FluxRss;
import myteam.projetvtbda.Repository.ArticleRepository;
import myteam.projetvtbda.Repository.FluxRssRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class RssCollectorService {

    @Autowired
    private FluxRssRepository fluxRssRepository;

    @Autowired
    private ArticleRepository articleRepository;

    /**
     * Collecter les articles de tous les flux RSS actifs
     */
    public List<Article> collecterTousLesFlux() {
        List<FluxRss> fluxActifs = fluxRssRepository.findByStatut(FluxRss.Statut.actif);
        List<Article> articlesCollectes = new ArrayList<>();

        System.out.println(" Début de la collecte RSS pour " + fluxActifs.size() + " flux actifs");

        for (FluxRss flux : fluxActifs) {
            try {
                List<Article> articles = collecterFlux(flux);
                articlesCollectes.addAll(articles);

                // Mettre à jour la date de dernière collecte
                flux.setDerniereMaj(LocalDateTime.now());
                fluxRssRepository.save(flux);

                System.out.println(" Flux '" + flux.getNomFlux() + "' : " + articles.size() + " articles collectés");
            } catch (Exception e) {
                System.err.println(" Erreur lors de la collecte du flux '" + flux.getNomFlux() + "' : " + e.getMessage());
            }
        }

        System.out.println(" Collecte terminée : " + articlesCollectes.size() + " articles au total");
        return articlesCollectes;
    }

    /**
     * Collecter les articles d'un flux RSS spécifique
     */
    public List<Article> collecterFlux(FluxRss flux) throws Exception {
        List<Article> nouveauxArticles = new ArrayList<>();

        // Lire le flux RSS
        URL feedUrl = new URL(flux.getUrlFlux());
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedUrl));

        for (SyndEntry entry : feed.getEntries()) {
            try {
                String urlSource = entry.getLink();

                // Vérifier si l'article existe déjà
                if (articleRepository.existsByUrlSource(urlSource)) {
                    continue;
                }

                // Créer le nouvel article
                Article article = new Article();
                article.setTitre(entry.getTitle());
                article.setDescription(entry.getDescription() != null ? entry.getDescription().getValue() : "");
                article.setUrlSource(urlSource);
                article.setFluxRss(flux);

                // Date de publication
                if (entry.getPublishedDate() != null) {
                    article.setDatePub(entry.getPublishedDate().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDateTime());
                }

                // Sauvegarder l'article
                Article savedArticle = articleRepository.save(article);
                nouveauxArticles.add(savedArticle);

            } catch (Exception e) {
                System.err.println(" Erreur lors du traitement d'un article : " + e.getMessage());
            }
        }

        return nouveauxArticles;
    }

    /**
     * Collecter un flux spécifique par son ID
     */
    public List<Article> collecterFluxParId(Long idFlux) {
        FluxRss flux = fluxRssRepository.findById(idFlux)
                .orElseThrow(() -> new RuntimeException("Flux RSS non trouvé"));

        try {
            return collecterFlux(flux);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la collecte : " + e.getMessage());
        }
    }
}