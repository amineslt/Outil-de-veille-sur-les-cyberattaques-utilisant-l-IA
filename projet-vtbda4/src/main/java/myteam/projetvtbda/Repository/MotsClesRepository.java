package myteam.projetvtbda.Repository;

import myteam.projetvtbda.Model.MotsCles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MotsClesRepository extends JpaRepository<MotsCles, Long> {

    Optional<MotsCles> findByMot(String mot);

    List<MotsCles> findByActif(Boolean actif);

    List<MotsCles> findByCategorie(String categorie);

    List<MotsCles> findByVeilleurIdUtilisateur(Long idVeilleur);

    boolean existsByMot(String mot);
}
