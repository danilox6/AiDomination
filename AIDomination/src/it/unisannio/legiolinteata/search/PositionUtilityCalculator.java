package it.unisannio.legiolinteata.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.yura.domination.engine.core.AbstractContinent;
import net.yura.domination.engine.core.AbstractCountry;
import net.yura.domination.engine.core.AbstractRiskGame;
import net.yura.domination.engine.core.Country;

import it.unisannio.ai.graph.model.UtilityCalculator;

public class PositionUtilityCalculator implements UtilityCalculator<TreeRiskGame, GameState, TPlayer, FortificationAction> {

	@Override
	public float evaluateUtility(TreeRiskGame tGame, GameState state, TPlayer player) {
		AbstractRiskGame game = tGame.getGame();

		float utility = -1;
		if(tGame.isTerminal(state)){
			utility = 0; 
			AbstractContinent[] continents = game.getContinents();
			List<Country> allCountries = new ArrayList<Country>();
			for(AbstractContinent c: continents){
				allCountries.addAll(c.getTerritoriesContained());
			}

			int[] ownedCountries = state.getCountryOwners();
			for(AbstractCountry country : allCountries){
				if(ownedCountries[country.getColor()-1] == player.getColor()){
					Vector<Country> neighbours = country.getNeighbours();
					for(AbstractCountry neighbour: neighbours){
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
