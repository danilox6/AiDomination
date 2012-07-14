package it.unisannio.legiolinteata.search;

import java.util.Vector;

import it.unisannio.ai.graph.model.UtilityCalculator;

public class PositionUtilityCalculator implements UtilityCalculator<TreeRiskGame, GameState, TreePlayer, PlacementAction> {

	@Override
	public float evaluateUtility(TreeRiskGame tGame, GameState state, TreePlayer player) {

		float utility = -1;
		if(tGame.isTerminal(state)){
			utility = 0; 
			TreePlayer myPlayer = state.getPlayerInt(player.getColor());
			Vector<TreeCountry> countries = myPlayer.getTerritoriesOwned();
			for(TreeCountry c: countries){
				Vector<TreeCountry> neighbours = c.getNeighbours();
				for(TreeCountry n: neighbours ){
					if(n.getOwner().getColor()==player.getColor())
						utility++;
				}
			}
		}
		else
			throw new IllegalArgumentException("State is not terminal.");
		return utility;
	}

}
