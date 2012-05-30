package it.unisannio.ai.graph.traversal;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.yura.domination.engine.core.Continent;
import net.yura.domination.engine.core.Country;

import it.unisannio.ai.graph.GraphSetup;
import it.unisannio.ai.graph.Scenario;
import it.unisannio.ai.graph.model.UtilityCalculator;

public class PositionUtilityCalculator implements UtilityCalculator<Scenario> {

	
	private GraphSetup graphSetup;

	
	public PositionUtilityCalculator(GraphSetup graphSetup){
		this.graphSetup = graphSetup;
	}
	
	@Override
	public float evaluateUtility(Scenario node) {
		float utility = 0; 
		Continent[] continents = graphSetup.getGame().getContinents();
		List<Country> allCountries = new ArrayList<Country>();
		for(Continent c: continents){
			allCountries.addAll(c.getTerritoriesContained());
		}
		
		int[] ownedCountries = node.getOwnedCountries();
		for(Country country : allCountries){
			if(ownedCountries[country.getColor()-1] == graphSetup.getPlayer().getColor()){
				Vector<Country> neighbours = country.getNeighbours();
				for(Country neighbour: neighbours){
					if(ownedCountries[neighbour.getColor()-1] == graphSetup.getPlayer().getColor())
						utility++;
				}
				
			}
		}
		
		
//		
//		ArrayList<Integer> myCountries = new ArrayList<Integer>();
//		for(int i = 0; i < ownedCountries.length ; i++)
//			if(ownedCountries[i] == graphSetup.getPlayer().getColor())
//				myCountries.add(i+1);
		
		
		return utility;
	}

}
