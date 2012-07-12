package it.unisannio.legiolinteata.advisor;

import java.util.LinkedList;
import java.util.List;

import net.sourceforge.jFuzzyLogic.FIS;
import net.yura.domination.engine.ai.commands.Fortification;
import net.yura.domination.engine.core.AbstractContinent;
import net.yura.domination.engine.core.AbstractCountry;
import net.yura.domination.engine.core.AbstractPlayer;
import net.yura.domination.engine.core.AbstractRiskGame;

public class FortificationAdvisor extends Advisor<Fortification> {

	private final AbstractRiskGame<?, ?, ?> game;
	private final AbstractPlayer<?> player;
	
	public FortificationAdvisor(AbstractRiskGame<?, ?, ?> game, AbstractPlayer<?> player) {
		super("fcl/fortification.fcl");
		
		this.game = game;
		this.player = player;
	}
	
	
	@Override
	protected double evaluate(Fortification command) {
		FIS fis = getFuzzyInferenceSystem();
		double max = 0.0;
		
		AbstractCountry<?,?,?> own = command.getCountry();
		AbstractContinent<?, ?> ownContinent = own.getContinent();
		
		double ownPower = Indices.power(player, game);
		double thisContinentOwnership = Indices.ownership(player, ownContinent);
		
		for(AbstractCountry<?, ?, ?> other : own.getNeighbours()) {
			AbstractPlayer<?> enemy = other.getOwner();
			
			if(enemy == player)
				continue;
			
			AbstractContinent<?, ?> otherContinent  = other.getContinent();
			
			fis.setVariable("defeat", Indices.victory(other, own));
			fis.setVariable("enemy", Indices.power(enemy, game));
			fis.setVariable("player", ownPower);
			fis.setVariable("this_continent_ownership", thisContinentOwnership);
			fis.setVariable("that_continent_ownership", Indices.ownership(player, otherContinent));
			fis.setVariable("this_continent_enemy_ownership", Indices.ownership(enemy, ownContinent));
			fis.setVariable("that_continent_enemy_ownership", Indices.ownership(player, ownContinent));
			fis.setVariable("that_continent", 0); // FIXME
			
			fis.evaluate();
			double fortification = fis.getVariable("fortification").getDefuzzifier().defuzzify();
			if(fortification > max)
				max = fortification;
		}
		
		return max;
	}

	@Override
	protected List<Fortification> generate() {
		List<Fortification> candidates = new LinkedList<Fortification>();
		for(AbstractCountry<?,?,?> country : player.getTerritoriesOwned()) {
			for(AbstractCountry<?, ?, ?> neighbour : country.getNeighbours()) {
				if(neighbour.getOwner() != player)
					candidates.add(new Fortification(country, player.getExtraArmies()));
			}
		}
		
		return candidates;
	}



}
