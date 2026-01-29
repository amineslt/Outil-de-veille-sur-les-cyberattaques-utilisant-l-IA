package myteam.projetvtbda.controller;

import myteam.projetvtbda.Model.AnalyseLlm;
import myteam.projetvtbda.Repository.AnalyseLlmRepository;
import myteam.projetvtbda.Service.LlmAnalyseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analyses")
@CrossOrigin(origins = "*")
public class AnalyseLlmController {

    @Autowired
    private AnalyseLlmRepository analyseLlmRepository;

    @Autowired
    private LlmAnalyseService analyseLlmService;

    /**
     * Lister toutes les analyses
     */
    @GetMapping
    public ResponseEntity<List<AnalyseLlm>> getToutesLesAnalyses() {
        return ResponseEntity.ok(analyseLlmRepository.findAll());
    }

    /**
     * Récupérer une analyse par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAnalyseById(@PathVariable Long id) {
        return analyseLlmRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lister les analyses par niveau de risque
     */
    @GetMapping("/niveau-risque/{niveau}")
    public ResponseEntity<List<AnalyseLlm>> getAnalysesParNiveauRisque(@PathVariable String niveau) {
        AnalyseLlm.NiveauRisque niveauRisque;
        try {
            niveauRisque = AnalyseLlm.NiveauRisque.valueOf(niveau);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(analyseLlmRepository.findByNiveauRisque(niveauRisque));
    }

    /**
     * Lister les analyses par type d'attaque
     */
    @GetMapping("/type-attaque/{type}")
    public ResponseEntity<List<AnalyseLlm>> getAnalysesParTypeAttaque(@PathVariable String type) {
        return ResponseEntity.ok(analyseLlmRepository.findByTypeAttaque(type));
    }

    /**
     * Lister les analyses par période
     */
    @GetMapping("/periode")
    public ResponseEntity<List<AnalyseLlm>> getAnalysesParPeriode(
            @RequestParam String debut,
            @RequestParam String fin) {
        LocalDateTime dateDebut = LocalDateTime.parse(debut);
        LocalDateTime dateFin = LocalDateTime.parse(fin);
        return ResponseEntity.ok(analyseLlmRepository.findByDateAnalyseBetween(dateDebut, dateFin));
    }

    /**
     * Lister les analyses non validées
     */
    @GetMapping("/non-validees")
    public ResponseEntity<List<AnalyseLlm>> getAnalysesNonValidees() {
        return ResponseEntity.ok(analyseLlmRepository.findAnalysesNonValidees());
    }

    @GetMapping("/validees")
    public ResponseEntity<List<AnalyseLlm>> getAnalysesValidees() {
        return ResponseEntity.ok(analyseLlmRepository.findAnalysesValidees());
    }

    /**
     * Statistiques par type d'attaque
     */
    @GetMapping("/statistiques/types")
    public ResponseEntity<?> getStatistiquesParType() {
        List<Object[]> stats = analyseLlmRepository.countByTypeAttaque();

        List<Map<String, Object>> result = stats.stream()
                .map(row -> Map.of(
                        "type_attaque", row[0],
                        "count", row[1]
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /**
     *  Lister toutes les analyses d'un flux RSS
     */
    @GetMapping("/flux/{idFlux}")
    public ResponseEntity<?> getToutesLesAnalysesParFlux(@PathVariable Long idFlux) {
        try {
            List<AnalyseLlm> analyses = analyseLlmService.getToutesLesAnalysesParFlux(idFlux);
            return ResponseEntity.ok(Map.of(
                    "nb_analyses", analyses.size(),
                    "analyses", analyses
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     *  Lister uniquement les analyses VALIDÉES d'un flux RSS
     */
    @GetMapping("/flux/{idFlux}/validees")
    public ResponseEntity<?> getAnalysesValideesParFlux(@PathVariable Long idFlux) {
        try {
            List<AnalyseLlm> analyses = analyseLlmService.getAnalysesValideesParFlux(idFlux);
            return ResponseEntity.ok(Map.of(
                    "nb_validees", analyses.size(),
                    "analyses", analyses
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     *  Analyses validées d'un flux RSS par niveau de risque
     */
    @GetMapping("/flux/{idFlux}/validees/niveau-risque/{niveau}")
    public ResponseEntity<?> getAnalysesValideesParFluxEtRisque(
            @PathVariable Long idFlux,
            @PathVariable String niveau) {
        try {
            AnalyseLlm.NiveauRisque niveauRisque = AnalyseLlm.NiveauRisque.valueOf(niveau);
            List<AnalyseLlm> analyses = analyseLlmService.getAnalysesValideesParFluxEtRisque(idFlux, niveauRisque);
            return ResponseEntity.ok(Map.of(
                    "flux_id", idFlux,
                    "niveau_risque", niveau,
                    "nb_analyses", analyses.size(),
                    "analyses", analyses
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", "Niveau de risque invalide. Valeurs possibles: faible, moyen, élevé"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * Statistiques globales
     */
    @GetMapping("/statistiques")
    public ResponseEntity<?> getStatistiquesGlobales() {
        long total = analyseLlmRepository.count();
        long faible = analyseLlmRepository.findByNiveauRisque(AnalyseLlm.NiveauRisque.faible).size();
        long moyen = analyseLlmRepository.findByNiveauRisque(AnalyseLlm.NiveauRisque.moyen).size();
        long eleve = analyseLlmRepository.findByNiveauRisque(AnalyseLlm.NiveauRisque.élevé).size();
        long nonValidees = analyseLlmRepository.findAnalysesNonValidees().size();

        return ResponseEntity.ok(Map.of(
                "total_analyses", total,
                "niveau_risque", Map.of(
                        "faible", faible,
                        "moyen", moyen,
                        "élevé", eleve
                ),
                "non_validees", nonValidees
        ));
    }
}