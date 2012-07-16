package it.unisannio.legiolinteata.search;

import java.util.Arrays;
import java.util.Comparator;
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
			TreeContinent[] orderedContinents = state.getContinents();
			Arrays.sort(orderedContinents, new Comparator<TreeContinent>() {

				@Override
				public int compare(TreeContinent o1, TreeContinent o2) {
					return o1.getTerritoriesContained().size() - o2.getTerritoriesContained().size();
				}
			});
		
			for(int i = 0; i<orderedContinents.length; i++){
				TreeContinent continent = orderedContinents[i];
				int ownedCountries = 0;
				Vector<TreeCountry> territoriesContained = continent.getTerritoriesContained();
				for(TreeCountry country : territoriesContained){
					if(country.getOwner().getColor()==myPlayer.getColor())
						ownedCountries++;
				}
				
//				utility += (float) (ownedCountries/(territoriesContained.size()*territoriesContained.size()))*100;
				
				if(ownedCountries==territoriesContained.size()){
					utility += 1000;
//					utility += (float) (20000*0.1)/((i+1)*2000);
				}
				else
					if(ownedCountries==0)
					utility -= 2000;
			}
			UtilityHelper.setGameState(state, utility);
		}
		else
			throw new IllegalArgumentException("State is not terminal.");
		return utility;
	}

}
