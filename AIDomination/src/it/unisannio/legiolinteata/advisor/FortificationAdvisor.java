package it.unisannio.legiolinteata.advisor;

import java.util.LinkedList;
import java.util.List;

import net.sourceforge.jFuzzyLogic.FIS;
import net.yura.domination.engine.ai.commands.Fortification;
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
		
		// TODO
		
		fis.evaluate();
		return fis.getVariable("attack").getDefuzzifier().defuzzify();
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
