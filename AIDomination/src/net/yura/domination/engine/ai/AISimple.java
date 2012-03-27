package net.yura.domination.engine.ai;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import net.yura.domination.engine.core.Card;
import net.yura.domination.engine.core.Continent;
import net.yura.domination.engine.core.Country;

public class AISimple extends AI{
	private static final long serialVersionUID = -5026514597239780686L;

	@SuppressWarnings("unchecked")
	@Override
	public String getTrade() {
		Vector<Card> cards = player.getCards();
		if(cards.size()<3) return "endtrade";

		List<Card> wildcards = new LinkedList<Card>();
		List<Card> cannons = new LinkedList<Card>();
		List<Card> infantry = new LinkedList<Card>();
		List<Card> cavalry = new LinkedList<Card>();
		for(Card c:cards){
			if(c.getName().equals(Card.WILDCARD)) wildcards.add(c);
			else if(c.getName().equals(Card.CANNON)) cannons.add(c);
			else if(c.getName().equals(Card.CAVALRY)) cavalry.add(c);
			else if(c.getName().equals(Card.INFANTRY)) infantry.add(c);
			
		}
		if (wildcards.size()>0){
			if(cannons.size()>1) return "trade wildcard "+ cannons.get(0).getCountry().getColor() + " " + cannons.get(1).getCountry().getColor();
			if(cavalry.size()>1) return "trade wildcard "+ cavalry.get(0).getCountry().getColor() + " " + cavalry.get(1).getCountry().getColor();
			if(infantry.size()>1) return "trade wildcard "+ infantry.get(0).getCountry().getColor() + " " + infantry.get(1).getCountry().getColor();
		}
		if(cannons.size()>0 && cavalry.size()>0 && infantry.size()>0)
			return "trade "                                	
				+ cannons.get(0).getCountry().getColor()+" "+ cavalry.get(0).getCountry().getColor()+" " + infantry.get(0).getCountry().getColor();
		if(cavalry.size()>2) 
			return "trade "+cavalry.get(0).getCountry().getColor() + " " + cavalry.get(1).getCountry().getColor()+ " " + cavalry.get(2).getCountry().getColor();
		if(infantry.size()>2)
			return "trade "+infantry.get(0).getCountry().getColor() + " " + infantry.get(1).getCountry().getColor()+ " "+infantry.get(2).getCountry().getColor();
		if(cannons.size()>2)
			return "trade "+cannons.get(0).getCountry().getColor() + " " + cannons.get(1).getCountry().getColor()+" "+cannons.get(2).getCountry().getColor();
		return "endtrade";
	}
	
	@SuppressWarnings("unchecked")
	public String getInitialArmyPlacement() {
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
		
		return "placearmies " + candidates[bestContinent].getColor() + " 1";
	}

	@Override
	public String getPlaceArmies() {
		return game.NoEmptyCountries() ? getArmyPlacement() : getInitialArmyPlacement();

	}

	private String getArmyPlacement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAttack() {
		
		return null;
	}

	@Override
	public String getRoll() {
		return "roll "+ Math.min(game.getAttacker().getArmies(), 3);
	}

	@Override
	public String getBattleWon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTacMove() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAutoDefendString() {
		int n=((Country)game.getDefender()).getArmies();
        if (n > game.getMaxDefendDice()) {
            return "roll "+game.getMaxDefendDice();
        }
    return "roll "+n;
	}

	@Override
	public String getCapital() {
		// TODO Auto-generated method stub
		return null;
	}

}
