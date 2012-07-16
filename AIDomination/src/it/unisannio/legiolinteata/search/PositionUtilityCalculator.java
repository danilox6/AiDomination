package it.unisannio.legiolinteata.search;

import java.util.Vector;

import it.unisannio.ai.graph.model.UtilityCalculator;
import it.unisannio.ai.graph.model.UtilityHelper;

public class PositionUtilityCalculator implements UtilityCalculator<TreeRiskGame, GameState, TreePlayer, PlacementAction> {

	@Override
	public float evaluateUtility(TreeRiskGame tGame, GameState state, TreePlayer player) {

		float utility = -1;
		if(tGame.isTerminal(state)){
			utility = 0; 
			TreePlayer myPlayer = state.getPlayerInt(player.getColor());
			Vector<TreeCountry> countries = myPlayer.getTerritoriesOwned();
			for(TreeCountry country: countries){
				Vector<TreeCountry> neighbours = country.getNeighbours();
				for(TreeCountry neighbour: neighbours ){
					if(neighbour.getOwner().getColor()==player.getColor())
						utility++;
					if (!neighbour.getContinent().equals(country.getContinent()))
						utility++;
				}
			}
			for(TreeContinent continent: state.getContinents()){
				int ownedCountries = 0;
				Vector<TreeCountry> territoriesContained = continent.getTerritoriesContained();
				for(TreeCountry country : territoriesContained){
					if(country.getOwner().getColor()==myPlayer.getColor())
						ownedCountries++;
				}
				if(ownedCountries==territoriesContained.size())
					utility += 1000;
				
				else if(ownedCountries==0)
					utility -= 1500;
			}
			UtilityHelper.setGameState(state, utility);
		}
		else
			throw new IllegalArgumentException("State is not terminal.");
		return utility;
	}

}
