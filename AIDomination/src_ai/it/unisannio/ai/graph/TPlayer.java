package it.unisannio.ai.graph;

import java.util.HashSet;

public class TPlayer implements Comparable<TPlayer>{
	private int color;
	private HashSet<Integer> ownedCountries = new HashSet<Integer>();
	private int order;
	private int extraArmies;
	
	public TPlayer(int color, int order) {
		this.color = color;
		this.order = order;
		extraArmies = 0;
	}
	
	public TPlayer(int color, int order, int extraArmies) {
		this.color = color;
		this.order = order;
		this.extraArmies = extraArmies;
	}
	
	public void addCountry(int countryColor){
		ownedCountries.add(countryColor);
	}
	
	public void removeCountry(int countryColor){
		ownedCountries.remove(countryColor);
	}
	
	public boolean ownsCountry(int countryColor){
		return ownedCountries.contains(ownedCountries);
	}
	

	public int getColor() {
		return color;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int compareTo(TPlayer arg0) {
		return this.order - arg0.getOrder();
	}

	public int getExtraArmies() {
		return extraArmies;
	}

	public void setExtraArmies(int extraArmies) {
		this.extraArmies = extraArmies;
	}
	
	
	
	
}
