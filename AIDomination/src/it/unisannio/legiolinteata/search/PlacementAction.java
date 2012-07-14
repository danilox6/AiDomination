package it.unisannio.legiolinteata.search;

import aima.core.agent.Action;

public class PlacementAction implements Action{

	private int countryToOccupy;
	private int occupyingPlayer;
	private int armies;
	private String commandString;
	
	public PlacementAction(int countryToOccupy, int occupyingPlayer, int armies) {
		this.countryToOccupy = countryToOccupy;
		this.occupyingPlayer = occupyingPlayer;
		this.armies = armies;
		
		commandString = String.format("placearmies %d %d", countryToOccupy, armies);
	}

	public int getCountryToOccupy() {
		return countryToOccupy;
	}
	
	public int getOccupyingPlayer() {
		return occupyingPlayer;
	}
	
	public String getCommand(){
		return commandString;
	}
	
	public int getArmies() {
		return armies;
	}

	@Override
	public boolean isNoOp() {
		return false;
	}

}
