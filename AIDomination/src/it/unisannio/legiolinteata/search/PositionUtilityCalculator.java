package it.unisannio.legiolinteata.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.yura.domination.engine.core.Continent;
import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.RiskGame;

import it.unisannio.ai.graph.model.UtilityCalculator;

public class PositionUtilityCalculator implements UtilityCalculator<TreeRiskGame, GameState, TPlayer, PlacementAction> {

	@Override
	public float evaluateUtility(TreeRiskGame tGame, GameState state, TPlayer player) {
		RiskGame game = tGame.getGame();

		float utility = -1;
		if(tGame.isTerminal(state)){
			utility = 0; 
			Continent[] continents = game.getContinents();
			List<Country> allCountries = new ArrayList<Country>();
			for(Continent c: continents){
				allCountries.addAll(c.getTerritoriesContained());
			}

			int[] ownedCountries = state.getCountryOwners();
			for(Country country : allCountries){
				if(ownedCountries[country.getColor()-1] == player.getColor()){
					Vector<Country> neighbours = country.getNeighbours();
					for(Country neighbour: neighbours){
						if(ownedCountries[neighbour.getColor()-1] == player.getColor())
							utility++;
					}

				}
			}
		}
		else
			throw new IllegalArgumentException("State is not terminal.");
		return utility;
	}


}
