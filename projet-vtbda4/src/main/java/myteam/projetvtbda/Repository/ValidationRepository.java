package myteam.projetvtbda.Repository;

import myteam.projetvtbda.Model.Validation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ValidationRepository extends JpaRepository<Validation, Long> {

    Optional<Validation> findByAnalyseLlmIdAnalyse(Long idAnalyse);

    List<Validation> findByStatut(Validation.Statut statut);

    List<Validation> findByAnalysteIdUtilisateur(Long idAnalyste);

    List<Validation> findByDateValidBetween(LocalDateTime debut, LocalDateTime fin);

    long countByStatut(Validation.Statut statut);
}

