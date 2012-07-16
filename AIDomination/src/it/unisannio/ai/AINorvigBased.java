package it.unisannio.ai;

import aima.core.search.adversarial.*;
import it.unisannio.ai.graph.model.UtilityHelper;
import it.unisannio.legiolinteata.search.GameState;
import it.unisannio.legiolinteata.search.PlacementAction;
import it.unisannio.legiolinteata.search.TreeRiskGame;
import net.yura.domination.engine.ai.Discoverable;

@Discoverable
public class AINorvigBased extends AISimple{
	
	@Override
	public String getPlaceArmies() {
		if(!game.NoEmptyCountries()){
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
			return action.getCommand();
		}
		else
			return super.getPlaceArmies();
	}
	
}
