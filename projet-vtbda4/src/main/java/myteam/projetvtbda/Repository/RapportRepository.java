package myteam.projetvtbda.Repository;

import myteam.projetvtbda.Model.Rapport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RapportRepository extends JpaRepository<Rapport, Long> {

    List<Rapport> findByDecideurIdUtilisateur(Long idDecideur);

    List<Rapport> findByTypeRapport(String typeRapport);

    List<Rapport> findByPeriodeDebutBetween(LocalDate debut, LocalDate fin);

    @Query("SELECT r FROM Rapport r WHERE r.decideur.idUtilisateur = :idDecideur ORDER BY r.dateCreation DESC")
    List<Rapport> findRapportsRecentsByDecideur(Long idDecideur);
}
