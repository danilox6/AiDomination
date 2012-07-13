package it.unisannio.legiolinteata.search;

import java.util.Vector;

import net.yura.domination.engine.ai.AI;
import net.yura.domination.engine.core.AbstractCountry;
import net.yura.domination.engine.core.AbstractPlayer;
import net.yura.domination.engine.core.Card;
import net.yura.domination.engine.core.Mission;

@SuppressWarnings({"rawtypes", "unchecked"})
public class TreePlayer implements AbstractPlayer<TreeCountry>{

	private AbstractPlayer absPlayer;
	private int armies = 0; //FIXME Non ricordo cosa indica... Tutte le armate possedute complessivamente dal player?
	private int extraArmies = 0;
	private Vector<Integer> ownedCountriesColor = new Vector<Integer>();
	private GameState gameState = null;
	
	private TreePlayer(AbstractPlayer absPlayer, int armies, int extraArmies, Vector<Integer> ownedCountries, GameState gameState) {
		this.absPlayer = absPlayer;
		this.armies = armies;
		this.extraArmies = extraArmies;
		this.ownedCountriesColor = (Vector<Integer>) ownedCountries;
		this.gameState = gameState;
	}

	public TreePlayer(AbstractPlayer player) {
		this.absPlayer = player;
		armies = absPlayer.getNoArmies();
		extraArmies = absPlayer.getExtraArmies();
		Vector<AbstractCountry> countries = player.getTerritoriesOwned();
		for (AbstractCountry c: countries){
			ownedCountriesColor.add(c.getColor());
		}
	}
	
	public TreePlayer(AbstractPlayer player, GameState gameState) {
		this.absPlayer = player;
		armies = absPlayer.getNoArmies();
		extraArmies = absPlayer.getExtraArmies();
		Vector<AbstractCountry> countries = player.getTerritoriesOwned();
		for (AbstractCountry c: countries){
			ownedCountriesColor.add(c.getColor());
		}
		this.gameState = gameState;
	}

	@Override
	public int getNoArmies() {
		return armies;
	}

	@Override
	public String getName() {
		return absPlayer.getName();
	}

	@Override
	public int getColor() {
		return absPlayer.getColor();
	}

	@Override
	public int getExtraArmies() {
		return extraArmies;
	}

	@Override
	public Vector<Card> getCards() {
		return absPlayer.getCards();
	}

	@Override
	public TreeCountry getCapital() {
		return gameState.getCountryInt(absPlayer.getCapital().getColor());
	}

	@Override
	public Mission getMission() {
		return absPlayer.getMission();
	}

	@Override
	public Vector<TreeCountry> getTerritoriesOwned() {
		Vector<TreeCountry> owned = new Vector<TreeCountry>();
		for(Integer i : ownedCountriesColor)
			owned.add(gameState.getCountries()[i-1]);
		return owned;
	}
	
	public TreePlayer addCountry(TreeCountry country){
		Vector<Integer> countries = (Vector<Integer>) ownedCountriesColor.clone();
		countries.add(country.getColor());
		return new TreePlayer(absPlayer, armies, extraArmies, countries, gameState);
	}

	public TreePlayer placeOnCountry(TreeCountry country, int armies){
		Vector<Integer> countries = (Vector<Integer>) ownedCountriesColor.clone();
		countries.add(country.getColor());
		return new TreePlayer(absPlayer, this.armies, extraArmies-armies, countries, gameState);
	}
	
	
	@Override
	public AI getAI() {
		return absPlayer.getAI();
	}

	@Override
	public boolean getAutoEndGo() {
		return absPlayer.getAutoEndGo();
	}

	@Override
	public boolean getAutoDefend() {
		return absPlayer.getAutoDefend();
	}

	@Override
	public int getTerritoriesOwnedSize() {
		return ownedCountriesColor.size();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractPlayer) {
			AbstractPlayer player = (AbstractPlayer) obj;
			if(this.getColor()==player.getColor());
		}
		return false;
	}
	
	public GameState getGameState() {
		return gameState;
	}
	
	public TreePlayer setGameState(GameState gameState) {
		return new TreePlayer(absPlayer, armies, extraArmies, ownedCountriesColor, gameState);
	}
}
