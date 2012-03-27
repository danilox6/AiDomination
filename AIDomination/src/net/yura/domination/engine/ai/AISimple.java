package net.yura.domination.engine.ai;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import net.yura.domination.engine.core.Card;
import net.yura.domination.engine.core.Continent;
import net.yura.domination.engine.core.Country;

public class AISimple extends AI{

	@Override
	public String getTrade() {
		Vector<Card> cards = player.getCards();
		if(cards.size()<3) return "endtrade";
		String output= "";
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

	@Override
	public String getPlaceArmies() {
		Continent[] continents = game.getContinents();
		for(Continent c:continents){
			Vector<Country> countries = c.getTerritoriesContained();
			for(Country co:countries){
				
			}
		}
		
		return null;
	}

	@Override
	public String getAttack() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRoll() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCapital() {
		// TODO Auto-generated method stub
		return null;
	}

}
