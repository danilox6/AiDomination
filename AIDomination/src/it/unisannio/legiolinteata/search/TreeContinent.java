package it.unisannio.legiolinteata.search;

import java.util.Vector;

import net.yura.domination.engine.core.AbstractContinent;
import net.yura.domination.engine.core.AbstractCountry;

@SuppressWarnings({"rawtypes", "unchecked"})
public class TreeContinent implements AbstractContinent<TreePlayer, TreeCountry>{
	
	private AbstractContinent absContinent;
	private Vector<Integer> containedCountriesColors = new Vector<Integer>();
	private GameState gameState;

	
	
	public TreeContinent(AbstractContinent continent, GameState gameState){
		absContinent = continent;
		Vector<AbstractCountry> countries = absContinent.getTerritoriesContained();
		for(AbstractCountry c: countries){
			this.containedCountriesColors.add(c.getColor());
		}
		this.gameState = gameState;
	}

	private TreeContinent(AbstractContinent absContinent, Vector<Integer> containedCountriesColors, GameState gameState) {
		this.absContinent = absContinent;
		this.containedCountriesColors = containedCountriesColors;
		this.gameState = gameState;
	}

	@Override
	public String getIdString() {
		return absContinent.getIdString();
	}

	@Override
	public String getName() {
		return absContinent.getName();
	}

	@Override
	public int getColor() {
		return absContinent.getColor();
	}

	@Override
	public int getArmyValue() {
		return absContinent.getArmyValue();
	}

	@Override
	public boolean isOwned(TreePlayer player) {
		for (TreeCountry c: getTerritoriesContained())
			if(c.getOwner()!=player)
				return false;
		return true;
	}

	@Override
	public TreePlayer getOwner() {
		TreePlayer owner = gameState.getCountries()[containedCountriesColors.get(0)-1].getOwner();
		return isOwned(owner)? owner : null;
	}

	@Override
	public Vector<TreeCountry> getTerritoriesContained() {
		Vector<TreeCountry> countries = new Vector<TreeCountry>();
		for(Integer i : containedCountriesColors)
			countries.add(gameState.getCountries()[i-1]);
		return countries;
	}

	public TreeContinent setGameState(GameState gameState) {
		return new TreeContinent(absContinent, containedCountriesColors, gameState);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractContinent) {
			AbstractContinent continent = (AbstractContinent) obj;
			if(this.getColor()==continent.getColor());
		}
		return false;
	}

}
