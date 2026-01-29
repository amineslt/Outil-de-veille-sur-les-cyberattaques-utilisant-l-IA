package myteam.projetvtbda.Service;

import myteam.projetvtbda.Model.ArticleFiltre;
import myteam.projetvtbda.Model.FluxRss;
import myteam.projetvtbda.Repository.ArticleFiltreRepository;
import myteam.projetvtbda.Repository.FluxRssRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ArticleFiltreService {

    @Autowired
    private ArticleFiltreRepository articleFiltreRepository;

    @Autowired
    private FluxRssRepository fluxRssRepository;

    /**
     * Récupérer tous les articles filtrés d'un flux RSS spécifique
     */
    public List<ArticleFiltre> getArticlesFiltresParFlux(Long idFlux) {
        // Vérifier que le flux existe
        FluxRss flux = fluxRssRepository.findById(idFlux)
                .orElseThrow(() -> new RuntimeException("Flux RSS non trouvé avec l'ID : " + idFlux));

        return articleFiltreRepository.findByFluxRssId(idFlux);
    }

    /**
     * Récupérer uniquement les articles PERTINENTS d'un flux RSS
     */
    public List<ArticleFiltre> getArticlesPertinentsParFlux(Long idFlux) {
        // Vérifier que le flux existe
        FluxRss flux = fluxRssRepository.findById(idFlux)
                .orElseThrow(() -> new RuntimeException("Flux RSS non trouvé avec l'ID : " + idFlux));

        return articleFiltreRepository.findPertinentsByFluxRssId(idFlux);
    }

    /**
     * Statistiques des articles filtrés d'un flux RSS
     */
    public Map<String, Object> getStatistiquesFiltresParFlux(Long idFlux) {
        // Vérifier que le flux existe
        FluxRss flux = fluxRssRepository.findById(idFlux)
                .orElseThrow(() -> new RuntimeException("Flux RSS non trouvé avec l'ID : " + idFlux));

        List<ArticleFiltre> tousFiltres = articleFiltreRepository.findByFluxRssId(idFlux);
        long pertinents = tousFiltres.stream().filter(ArticleFiltre::getPertinent).count();
        long nonPertinents = tousFiltres.size() - pertinents;

        // Calcul du score moyen des articles pertinents
        double scoreMoyen = tousFiltres.stream()
                .filter(ArticleFiltre::getPertinent)
                .mapToInt(ArticleFiltre::getScoreMatch)
                .average()
                .orElse(0.0);

        return Map.of(
                "flux", Map.of(
                        "idFlux", flux.getIdFlux(),
                        "nomFlux", flux.getNomFlux(),
                        "urlFlux", flux.getUrlFlux()
                ),
                "total_filtres", tousFiltres.size(),
                "pertinents", pertinents,
                "non_pertinents", nonPertinents,
                "taux_pertinence", tousFiltres.size() > 0 ? (pertinents * 100.0 / tousFiltres.size()) : 0,
                "score_moyen_pertinents", Math.round(scoreMoyen * 100.0) / 100.0
        );
    }
}