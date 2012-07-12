package it.unisannio.legiolinteata.advisor;

import java.util.Vector;

import net.yura.domination.engine.core.AbstractContinent;
import net.yura.domination.engine.core.AbstractCountry;
import net.yura.domination.engine.core.AbstractPlayer;
import net.yura.domination.engine.core.AbstractRiskGame;

public class Indices {
	private Indices() {}
	
	public static double victory(AbstractCountry<?, ?, ?> attacker, AbstractCountry<?, ?, ?> defender) {
		return Attacks.getVictoryProbability(attacker.getArmies(), defender.getArmies());
	}
	
	public static double power(AbstractPlayer<?> p, AbstractRiskGame<?, ?, ?> game) {
		return (double) 10.0 * p.getTerritoriesOwnedSize()/game.getNoCountries();
	}
	
	public static double ownership(AbstractPlayer<?> p, AbstractContinent<?, ?> c) {
		@SuppressWarnings("unchecked")
		Vector<AbstractCountry<?, ?, ?>> countries = (Vector<AbstractCountry<?, ?, ?>>) c.getTerritoriesContained();
		int count = 0;
		
		for(AbstractCountry<?, ?, ?> country : countries) {
			if(country.getOwner() == p) 
				count++;
		}
		
		return 10.0 * count / countries.size();
	}
}
