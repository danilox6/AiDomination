package it.unisannio.legiolinteata.search;

import java.util.ArrayList;
import java.util.List;

import aima.core.agent.State;

public class GameState implements State, Cloneable{
	
	private int[] countryArmies;
	private int[] countryOwners;
	private int currentPlayer;
	
	public GameState(int[] countries, int[] ownedCountries, int currentPlayer) {
		this.countryArmies = countries;
		this.countryOwners = ownedCountries;
		this.currentPlayer = currentPlayer;
	}

	public List<Integer> getFreeCountries(){
		List<Integer> freeCountries = new ArrayList<Integer>();
		for(int i = 0; i<countryArmies.length; i++)
			if (countryArmies[i] == 0)
				freeCountries.add(i);
		return freeCountries;
	}
	
	public void place(PlacementAction placamentAction){
		countryArmies[placamentAction.getCountryToOccupy()] = 1;
		countryOwners[placamentAction.getCountryToOccupy()] = placamentAction.getOccupyingPlayer();
	}
	
	public void updateCurrentPlayerIndex(TPlayer[] players){
		currentPlayer = ++currentPlayer % players.length;
	}
	
	public int getCurrentPlayerIndex(){
		return currentPlayer;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new GameState(countryArmies.clone(), countryOwners.clone(), currentPlayer);
	}

	public int[] getCountryOwners() {
		return countryOwners;
	}
}
