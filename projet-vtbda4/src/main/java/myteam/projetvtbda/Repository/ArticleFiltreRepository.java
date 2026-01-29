package myteam.projetvtbda.Repository;

import myteam.projetvtbda.Model.ArticleFiltre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleFiltreRepository extends JpaRepository<ArticleFiltre, Long> {

    Optional<ArticleFiltre> findByArticleIdArticle(Long idArticle);

    List<ArticleFiltre> findByPertinent(Boolean pertinent);

    List<ArticleFiltre> findByScoreMatchGreaterThanEqual(Integer scoreMin);

    List<ArticleFiltre> findByDateFiltrageBetween(LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT af FROM ArticleFiltre af WHERE af.pertinent = true ORDER BY af.scoreMatch DESC")
    List<ArticleFiltre> findArticlesPertinentsTriesParScore();

    //  Articles filtrés d'un flux RSS spécifique
    @Query("SELECT af FROM ArticleFiltre af WHERE af.article.fluxRss.idFlux = :idFlux")
    List<ArticleFiltre> findByFluxRssId(@Param("idFlux") Long idFlux);

    //  Articles filtrés pertinents d'un flux RSS
    @Query("SELECT af FROM ArticleFiltre af WHERE af.article.fluxRss.idFlux = :idFlux AND af.pertinent = true")
    List<ArticleFiltre> findPertinentsByFluxRssId(@Param("idFlux") Long idFlux);
}