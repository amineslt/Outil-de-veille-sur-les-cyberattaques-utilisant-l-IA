package myteam.projetvtbda.Repository;

import myteam.projetvtbda.Model.AnalyseLlm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyseLlmRepository extends JpaRepository<AnalyseLlm, Long> {

    Optional<AnalyseLlm> findByArticleIdArticle(Long idArticle);

    List<AnalyseLlm> findByNiveauRisque(AnalyseLlm.NiveauRisque niveauRisque);

    List<AnalyseLlm> findByTypeAttaque(String typeAttaque);

    List<AnalyseLlm> findByDateAnalyseBetween(LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT al FROM AnalyseLlm al WHERE al.validation IS NULL")
    List<AnalyseLlm> findAnalysesNonValidees();

    @Query("SELECT al FROM AnalyseLlm al WHERE al.validation IS NOT NULL")
    List<AnalyseLlm> findAnalysesValidees();

    @Query("SELECT al.typeAttaque, COUNT(al) FROM AnalyseLlm al GROUP BY al.typeAttaque")
    List<Object[]> countByTypeAttaque();

    // Analyses validées d'un flux RSS
    @Query("SELECT al FROM AnalyseLlm al WHERE al.article.fluxRss.idFlux = :idFlux AND al.validation IS NOT NULL AND al.validation.statut = 'validé'")
    List<AnalyseLlm> findAnalysesValideesParFluxRss(@Param("idFlux") Long idFlux);

    // Toutes les analyses d'un flux RSS (validées + non validées)
    @Query("SELECT al FROM AnalyseLlm al WHERE al.article.fluxRss.idFlux = :idFlux")
    List<AnalyseLlm> findAnalysesParFluxRss(@Param("idFlux") Long idFlux);

    // Analyses validées par niveau de risque pour un flux RSS
    @Query("SELECT al FROM AnalyseLlm al WHERE al.article.fluxRss.idFlux = :idFlux AND al.validation IS NOT NULL AND al.validation.statut = 'validé' AND al.niveauRisque = :niveauRisque")
    List<AnalyseLlm> findAnalysesValideesParFluxRssEtNiveauRisque(@Param("idFlux") Long idFlux, @Param("niveauRisque") AnalyseLlm.NiveauRisque niveauRisque);
}