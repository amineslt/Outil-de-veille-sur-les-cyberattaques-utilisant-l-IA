package myteam.projetvtbda.Repository;

import myteam.projetvtbda.Model.FluxRss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FluxRssRepository extends JpaRepository<FluxRss, Long> {

    List<FluxRss> findByStatut(FluxRss.Statut statut);

    Optional<FluxRss> findByUrlFlux(String urlFlux);

    List<FluxRss> findByVeilleurIdUtilisateur(Long idVeilleur);

    boolean existsByUrlFlux(String urlFlux);
}

