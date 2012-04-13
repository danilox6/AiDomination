package it.unisannio.ai;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.yura.domination.engine.ai.BaseAI;
import net.yura.domination.engine.ai.Discoverable;
import net.yura.domination.engine.ai.BaseAI.Attack;
import net.yura.domination.engine.core.Continent;
import net.yura.domination.engine.core.Country;

@Discoverable
public class AISimple2 extends BaseAI {

	@Override
	protected Country onCountrySelection() {
		Continent[] continents = game.getContinents();
		
		Country[] candidates = new Country[continents.length];
		int[] candidateScores = new int[continents.length];
		int[] freeCountries = new int[continents.length];
		
		for(int i = 0; i < continents.length; ++i){
			for(Country country:(Vector<Country>)continents[i].getTerritoriesContained()) {
				if(country.getOwner() == null) {
					freeCountries[i]++;
					
					int score = 0;
					for(Country neightbour: (Vector<Country>) country.getNeighbours()) {
						if(neightbour.getOwner() == player || neightbour.getContinent() != continents[i])
							score++;
					}
					
					if(score > candidateScores[i] || candidates[i] == null) {
						candidateScores[i] = score;
						candidates[i] = country;
					}	
				}
			}
		}
		
		int lowestCount = 100;
		int bestContinent = 0;
		for(int i = 0; i < freeCountries.length; ++i) {
			if(freeCountries[i] > 0 && freeCountries[i] < lowestCount) {
				lowestCount = freeCountries[i];
				bestContinent = i;
			}
		}
		
		return candidates[bestContinent];
	}
	
	@Override
	protected Attack onAttack() {
		Vector<Country> countries = player.getTerritoriesOwned();
		Country attacker = null;
		Country defender = null;
		for(Country c : countries){
			Vector<Country> neighbours = c.getNeighbours();
			for(Country n: neighbours){
				if(n.getOwner() != player) {
					if (
							(attacker == null && defender == null)
							|| ((c.getArmies()-1) - n.getArmies()) > ((attacker.getArmies()-1) - defender.getArmies())
							){
						attacker = c;
						defender = n;
					}
				}
			}	
		}
		
		if(attacker==null || ((float) attacker.getArmies() / (float) defender.getArmies()) < 1.5)//((attacker.getArmies()-1) - defender.getArmies()) < 1 )
			return null;
		
		return new Attack(attacker, defender);
	}

	@Override
	protected Country onCountryFortification() {
		Continent[] continents = game.getContinents();
		final Map<Continent, Set<Country>> accessPoints = new HashMap<Continent, Set<Country>>();
		
		
		for(Continent continent : continents) {
			accessPoints.put(continent, getConfiningTerritories(continent));
		}
		
		Arrays.sort(continents, new Comparator<Continent>() {

			@Override
			public int compare(Continent arg0, Continent arg1) {
				int comparation = new Float(getForceRatio(arg0)).compareTo(getForceRatio(arg1));
				return comparation == 0
					? new Integer(accessPoints.get(arg0).size()).compareTo(accessPoints.get(arg1).size())
					: comparation;
			}
			
		});
		
		int extraArmies = player.getExtraArmies();
		int defendableContinents = continents.length;
		float defendQuota = 0;
		
		for(int i = defendableContinents; i > 0; --i) {
			float totalThreatLevel = 0;
			float totalAccessPoints = 0;
			
			for(int j = 0; j < defendableContinents; ++j) {
				totalThreatLevel += getThreatLevel(accessPoints.get(continents[j]));
				totalAccessPoints += accessPoints.get(continents[j]).size();
			}
			
			defendQuota = totalThreatLevel / totalAccessPoints;
			if(defendQuota < 1.0)
				break;
		}
		
		
		
		List<Country> borders = new LinkedList<Country>();
		for(int i = 0; i < defendableContinents; ++i) {
			borders.addAll(accessPoints.get(continents[i]));
		}
		
		Collections.sort(borders, new Comparator<Country>() {

			@Override
			public int compare(Country arg0, Country arg1) {
				return getDefenseNeeded(arg1) - getDefenseNeeded(arg0);
			}
		});
		
		int armiesForDefense = (int) Math.floor(defendQuota * extraArmies);
		if(armiesForDefense > 0) {			
			Country mostNeedful = borders.get(0);
			return mostNeedful;// + Math.min(getDefenseNeeded(mostNeedful), armiesForDefense);
		}
		
		Country strongest = borders.get(borders.size() - 1);
		return strongest;// + extraArmies;
	}
	

	private int getThreatLevel(Set<Country> countries) {
		int threatLevel = 0;
		for(Country country : countries) {
			if(getDefenseNeeded(country) > 0)
				threatLevel++;
		}
		
		return threatLevel;
	}
	
	private int getDefenseNeeded(Country country) {
		if(country.getOwner() != player) 
			return 0;
		
		int neededArmies = 0, countryArmies = country.getArmies();
		for(Country neighbour : (Vector<Country>) country.getNeighbours()) {
			if(neighbour.getOwner() != player && neighbour.getArmies() >= countryArmies + neededArmies) 
				neededArmies = neighbour.getArmies() - countryArmies + 1;
		}
		
		return neededArmies;
	}
	
	private Set<Country> getConfiningTerritories(Continent continent) {
		LinkedList<Country> toVisit = new LinkedList<Country>();
		Set<Country> visited = new HashSet<Country>();
		Set<Country> confiningTerritories = new HashSet<Country>();
		
		toVisit.addAll(continent.getTerritoriesContained());
		while(toVisit.size() > 0) {
			Country country = toVisit.remove();
			if(country.getOwner() != player) 
				continue;
			
			Vector<Country> neighbours = country.getNeighbours();
			for(Country neighbour : neighbours) {
				visited.add(neighbour);
				if(neighbour.getOwner() != player)
					confiningTerritories.add(country);
				else if(!visited.contains(neighbour))
					toVisit.addLast(neighbour);
			}
		}
		
		return confiningTerritories;
	}
	
	private float getForceRatio(Continent arg0) {
		float myForces = 0.0f;
		float otherForces = 0.0f;
		for(Country c : (Vector<Country>) arg0.getTerritoriesContained()) {
			if(c.getOwner() == player) {
				myForces += c.getArmies();
			} else {
				otherForces += c.getArmies();
			}
		}
		
		return myForces / (myForces + otherForces);
	}

}
