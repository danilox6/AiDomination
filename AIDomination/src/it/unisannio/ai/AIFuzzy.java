package it.unisannio.ai;

import aima.core.search.adversarial.AdversarialSearch;
import aima.core.search.adversarial.AlphaBetaSearch;
import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;
import it.unisannio.ai.graph.model.UtilityHelper;
import it.unisannio.legiolinteata.advisor.AttackAdvisor;
import it.unisannio.legiolinteata.advisor.FortificationAdvisor;
import it.unisannio.legiolinteata.search.GameState;
import it.unisannio.legiolinteata.search.PlacementAction;
import it.unisannio.legiolinteata.search.TreeRiskGame;
import net.yura.domination.engine.ai.AIPlayer;
import net.yura.domination.engine.ai.Discoverable;
import net.yura.domination.engine.ai.commands.Attack;
import net.yura.domination.engine.ai.commands.Fortification;
import net.yura.domination.engine.core.Country;

@Discoverable
public class AIFuzzy extends AISimple2 {
	
	@Override
	protected Attack onAttack() {
        AttackAdvisor aa = new AttackAdvisor(game, player);
        Attack best = aa.getBestAdvice(5.0);
        //if(best != null)
        //	System.out.println(best.getOrigin() + "-->" + best.getDestination());
        return best;
	}

	@Override
	protected Fortification onFortification() {
		FortificationAdvisor fa = new FortificationAdvisor(game, player);
		Fortification best = fa.getBestAdvice(Double.NEGATIVE_INFINITY);
		return best;
	}
	
	@Override
	protected Country onCountrySelection() {
		TreeRiskGame tRiskGame = new TreeRiskGame(game, player);
		
		/**
		 * Algoritimi di ricerca disponibili:
		 * 	- MinimaxSearch.createFor(tRiskGame);
		 *  - AlphaBetaSearch.createFor(tRiskGame);
		 *  - IterativeDeepeningAlphaBetaSearch.createFor(tRiskGame, double utilMin, double utilMax, int time); ???
		 *  	 (In un esempio sul gioco del Tris, vengono usati i seguenti valori .createFor(game, 0.0, 1.0, 1000); )	
		 */
		AdversarialSearch<GameState, PlacementAction> search = IterativeDeepeningAlphaBetaSearch.createFor(tRiskGame, 0, Double.MAX_VALUE, AIPlayer.getTimeout()-3);
		PlacementAction action = search.makeDecision(tRiskGame.getInitialState());
		System.out.println(search.getMetrics().toString());
		System.out.println(UtilityHelper.bestUtility +"\n"+UtilityHelper.bestState.dump(false));
		UtilityHelper.clear();
		return game.getCountryInt(action.getCountryToOccupy());
	}
}
