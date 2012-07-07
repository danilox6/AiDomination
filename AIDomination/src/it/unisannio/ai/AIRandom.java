package it.unisannio.ai;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import net.yura.domination.engine.ai.AI;
import net.yura.domination.engine.ai.Discoverable;
import net.yura.domination.engine.core.AbstractContinent;
import net.yura.domination.engine.core.AbstractCountry;
import net.yura.domination.engine.core.Card;
import net.yura.domination.engine.core.Country;

@SuppressWarnings({"rawtypes", "unchecked"})
@Discoverable
public class AIRandom extends AI {


	@Override
	public String getTrade() {
		Vector<Card> cards = player.getCards();
		if(cards.size()<3) return "endtrade";
		Collections.shuffle(cards);
		for(int i=0;i<cards.size();i++){
			for(int j=i+1;j<cards.size();j++){
				for(int k=j+1;k<cards.size();k++){
					Card card1 = cards.get(i);
					Card card2 = cards.get(j);
					Card card3 = cards.get(k);
					if(game.checkTrade(card1,card2,card3)){
						String output = "trade ";
						if(card1.getName().equals("wildcard")) output += "wildcard ";
						else output+=card1.getCountry().getColor()+" ";
						if(card2.getName().equals("wildcard")) output += "wildcard ";
						else output+=card2.getCountry().getColor()+" ";
						if(card3.getName().equals("wildcard")) output += "wildcard ";
						else output+=card3.getCountry().getColor()+" ";
						return output;

					}
				}
			}
		}
		return "endtrade";
	}

	

	@Override
	public String getPlaceArmies() {
		AbstractContinent[] continents = game.getContinents();
		List<Country> countries = new ArrayList<Country>();
		for(AbstractContinent c:continents){
			countries.addAll(c.getTerritoriesContained());
		}
		Collections.shuffle(countries);
		if(game.NoEmptyCountries()==false){
			for(AbstractCountry c:countries){
				if(c.getOwner()==null){
					return "placearmies "+c.getColor()+" 1";
				}
			}
		}
		for(AbstractCountry c: countries){
			if(c.getOwner()==player){
				return "placearmies "+c.getColor()+" 1";
			}
		}
		return null;
	}

	@Override
	public String getAttack() {
		Vector<Country> countries = player.getTerritoriesOwned();
		Collections.shuffle(countries);
		for(AbstractCountry c:countries){
			Vector<Country> neighbours = c.getNeighbours();
			Collections.shuffle(neighbours);
			for(AbstractCountry ncountry:neighbours){
				if(ncountry.getOwner()!=player && c.getArmies()>1)
					return "attack "+c.getColor()+" "+ ncountry.getColor();
			}
		}
		return "endattack";
	}

	@Override
	public String getRoll() {
		double r = Math.random();
		long dices =(long) Math.floor(r*Math.min(game.getAttacker().getArmies()-1, 3)+ 1) ;
		return "roll "+ dices;
	}

	@Override
	public String getBattleWon() {
		long armies =(long)Math.floor(Math.random()*(game.getAttacker().getArmies()-1)+ 1) ;
		return "move "+armies;
	}

	@Override
	public String getTacMove() {
		Vector<Country> countries = player.getTerritoriesOwned();
		Collections.shuffle(countries);
		for(AbstractCountry c:countries){
			Vector<Country> neighbours = c.getNeighbours();
			Collections.shuffle(neighbours);
			for(AbstractCountry ncountry:neighbours){
				if(ncountry.getOwner()==player && c.getArmies()>1){
					long armies =(long)Math.floor(Math.random()*(c.getArmies()-1)+ 1) ;
					return "movearmies "+c.getColor()+" "+ ncountry.getColor()+" "+armies;
				}

			}
		}
		return "nomove";
	}

	@Override
	public String getAutoDefendString() {
		long dices =(long)Math.floor(Math.random()*Math.min(game.getDefender().getArmies()-1, game.getMaxDefendDice())+ 1) ;
		return "roll "+ dices;
	}

	@Override
	public String getCapital() {
		return null;
	}

}
