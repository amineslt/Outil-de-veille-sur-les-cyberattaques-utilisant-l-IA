package myteam.projetvtbda.Repository;

import myteam.projetvtbda.Model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findByFluxRssIdFlux(Long idFlux);

    List<Article> findByDateCollecteBetween(LocalDateTime debut, LocalDateTime fin);

    Optional<Article> findByUrlSource(String urlSource);

    // Vérifier si un article avec cette URL existe déjà
    boolean existsByUrlSource(String urlSource);

    @Query("SELECT a FROM Article a WHERE a.articleFiltre IS NULL")
    List<Article> findArticlesNonFiltres();

    @Query("SELECT a FROM Article a WHERE a.analyseLlm IS NULL AND a.articleFiltre.pertinent = true")
    List<Article> findArticlesPertinentsNonAnalyses();
}