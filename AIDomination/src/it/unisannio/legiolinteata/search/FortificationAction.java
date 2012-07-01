package it.unisannio.legiolinteata.search;

import aima.core.agent.Action;

public class FortificationAction implements Action{

	private int countryToOccupy;
	private int occupyingPlayer;
	private String commandString;
	
	public FortificationAction(int countryToOccupy, int occupyingPlayer) {
		this.countryToOccupy = countryToOccupy;
		this.occupyingPlayer = occupyingPlayer;
		
		commandString = String.format("placearmies %d 1", countryToOccupy+1);
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

	@Override
	public boolean isNoOp() {
		return false;
	}
	

}
