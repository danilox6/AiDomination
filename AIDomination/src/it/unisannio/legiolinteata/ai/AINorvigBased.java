package it.unisannio.legiolinteata.ai;

import aima.core.search.adversarial.*;
import it.unisannio.legiolinteata.search.GameState;
import it.unisannio.legiolinteata.search.PlacementAction;
import it.unisannio.legiolinteata.search.TreeRiskGame;
import it.unisannio.legiolinteata.search.UtilityHelper;
import net.yura.domination.engine.ai.api.Discoverable;
import net.yura.domination.engine.core.Country;

@Discoverable
public class AINorvigBased extends FallbackAI {
	
	@Override
	public Country onCountrySelection() {
		TreeRiskGame tRiskGame = new TreeRiskGame(game, player);
		
		/**
		 * Algoritimi di ricerca disponibili:
		 * 	- MinimaxSearch.createFor(tRiskGame);
		 *  - AlphaBetaSearch.createFor(tRiskGame);
		 *  - IterativeDeepeningAlphaBetaSearch.createFor(tRiskGame, double utilMin, double utilMax, int time); ???
		 *  	 (In un esempio sul gioco del Tris, vengono usati i seguenti valori .createFor(game, 0.0, 1.0, 1000); )	
		 */
		AdversarialSearch<GameState, PlacementAction> search = AlphaBetaSearch.createFor(tRiskGame);
		PlacementAction action = search.makeDecision(tRiskGame.getInitialState());
		System.out.println(search.getMetrics().toString());
		System.out.println(UtilityHelper.bestUtility +"\n"+UtilityHelper.bestState.dump(false));
		UtilityHelper.clear();
		return game.getCountryInt(action.getCountryToOccupy());

	}
	
}
