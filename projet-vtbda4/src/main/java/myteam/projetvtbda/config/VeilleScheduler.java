package myteam.projetvtbda.config;

import myteam.projetvtbda.Model.AnalyseLlm;
import myteam.projetvtbda.Model.Article;
import myteam.projetvtbda.Model.ArticleFiltre;
import myteam.projetvtbda.Service.FiltrageService;
import myteam.projetvtbda.Service.LlmAnalyseService;
import myteam.projetvtbda.Service.RssCollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VeilleScheduler {

    @Autowired
    private RssCollectorService rssCollectorService;

    @Autowired
    private FiltrageService filtrageService;

    @Autowired
    private LlmAnalyseService llmAnalyseService;

    /**
     * ❌ SCHEDULER DÉSACTIVÉ
     * Utilisez POST /api/veille/cycle-complet pour lancer manuellement
     */

    /**
     * Cycle complet de veille (appelé uniquement par le controller)
     */
    public void executerCycleComplet() {
        System.out.println("\n🚀 ========== DÉBUT DU CYCLE DE VEILLE AUTOMATIQUE ==========");

        try {
            // ÉTAPE 1 : Collecte RSS
            System.out.println("\n📥 ÉTAPE 1 : Collecte des flux RSS");
            List<Article> articles = rssCollectorService.collecterTousLesFlux();
            System.out.println("✅ " + articles.size() + " nouveaux articles collectés\n");

            // ÉTAPE 2 : Filtrage
            System.out.println("🔍 ÉTAPE 2 : Filtrage par mots-clés");
            List<ArticleFiltre> filtres = filtrageService.filtrerTousLesArticles();
            long pertinents = filtres.stream().filter(ArticleFiltre::getPertinent).count();
            System.out.println("✅ " + pertinents + " articles pertinents (score > 50)\n");

            // ÉTAPE 3 : Analyse LLM
            System.out.println("🤖 ÉTAPE 3 : Analyse par LLM");
            List<AnalyseLlm> analyses = llmAnalyseService.analyserTousLesArticlesPertinents();
            System.out.println("✅ " + analyses.size() + " articles analysés par LLM\n");

            System.out.println("🎉 ========== CYCLE DE VEILLE TERMINÉ AVEC SUCCÈS ==========\n");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du cycle de veille : " + e.getMessage());
            e.printStackTrace();
        }
    }
}