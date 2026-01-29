package myteam.projetvtbda.Repository;

import myteam.projetvtbda.Model.RapportArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RapportArticleRepository extends JpaRepository<RapportArticle, Long> {

    List<RapportArticle> findByRapportIdRapport(Long idRapport);

    List<RapportArticle> findByArticleIdArticle(Long idArticle);

    boolean existsByRapportIdRapportAndArticleIdArticle(Long idRapport, Long idArticle);

    @Query("SELECT ra.article FROM RapportArticle ra WHERE ra.rapport.idRapport = :idRapport")
    List<Object> findArticlesByRapport(Long idRapport);
}
