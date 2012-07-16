package it.unisannio.legiolinteata.advisor;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.yura.domination.engine.core.AbstractContinent;
import net.yura.domination.engine.core.AbstractPlayer;
import net.yura.domination.engine.core.AbstractRiskGame;

public class ContinentAdvisor extends Advisor<AbstractContinent<?, ?>> {

	private final AbstractRiskGame<?, ?, ?> game;
	private final AbstractPlayer<?> player;

	public ContinentAdvisor(AbstractRiskGame<?,?,?> game, AbstractPlayer<?> player) {
		super("fcl/fortification.fcl", "continent");
		this.game = game;
		this.player = player;
	}

	@Override
	protected List<AbstractContinent<?, ?>> generate() {
		return Arrays.asList((AbstractContinent<?,?>[]) game.getContinents());
	}

	@Override
	protected double evaluate(AbstractContinent<?, ?> continent) {
		FunctionBlock block = getFunctionBlock();
		block.setVariable("player_ownership", Indices.ownership(player, continent));
		
		double maxOwnership = 0.0;
		for(AbstractPlayer<?> p : game.getPlayers()) {
			if(p != player) 
				maxOwnership = Math.max(maxOwnership, Indices.ownership(p, continent));
		}
		
		block.setVariable("enemy_ownership",maxOwnership);
		block.evaluate();
		
		return block.getVariable("target").getDefuzzifier().defuzzify();
	}

}
