package it.unisannio.legiolinteata.advisor;

import java.util.*;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.yura.domination.engine.ai.commands.Attack;
import net.yura.domination.engine.core.AbstractCountry;
import net.yura.domination.engine.core.AbstractPlayer;
import net.yura.domination.engine.core.AbstractRiskGame;

public class AttackAdvisor extends Advisor<Attack> {

	private final AbstractRiskGame<?, ?, ?> game;
	private final AbstractPlayer<?> player;
	
	public AttackAdvisor(AbstractRiskGame<?, ?, ?> game, AbstractPlayer<?> player) {
		super("fcl/attack.fcl", "attack");
		
		this.game = game;
		this.player = player;
	}
	
	protected double evaluate(Attack a) {
		FunctionBlock fis = getFunctionBlock();
		fis.setVariable("enemy", Indices.power(a.getDestination().getOwner(), game));
		fis.setVariable("victory", Indices.victory(a.getOrigin(), a.getDestination()));

		fis.evaluate();
		return fis.getVariable("attack").getDefuzzifier().defuzzify();
	}
	
	@Override
	protected List<Attack> generate() {
		List<Attack> candidates = new ArrayList<Attack>();
		for(AbstractCountry<?, ?, ?> country : player.getTerritoriesOwned()) {
			@SuppressWarnings("unchecked")
			Vector<AbstractCountry<?, ?, ?>> neighbours = (Vector<AbstractCountry<?, ?, ?>>) country.getNeighbours();
			for(AbstractCountry<?, ?, ?> neighbour : neighbours) {
				if(neighbour.getOwner() != player) {
					candidates.add(new Attack(country, neighbour));
				}
			}
		}
		
		return candidates;
	}
}
