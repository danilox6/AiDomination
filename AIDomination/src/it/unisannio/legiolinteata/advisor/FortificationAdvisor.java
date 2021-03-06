package it.unisannio.legiolinteata.advisor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.yura.domination.engine.ai.commands.Fortification;
import net.yura.domination.engine.core.AbstractContinent;
import net.yura.domination.engine.core.AbstractCountry;
import net.yura.domination.engine.core.AbstractPlayer;
import net.yura.domination.engine.core.AbstractRiskGame;

public class FortificationAdvisor extends Advisor<Fortification> {

	private class TargettedFortification extends Fortification {
		private Advice<AbstractContinent<?,?>> target;
		private double cost = -1;

		public TargettedFortification(Advice<AbstractContinent<?,?>> target, 
				AbstractCountry<?, ?, ?> country2, int armies) {
			super(country2, armies);
			this.target = target;
		}
		
		public double getCost() {
			if(cost == -1) {
				int totalArmies = getCountry().getArmies() + getArmies();
				int distance = getDistanceToContinent(getCountry(), target.getObject(), new ArrayList<AbstractCountry<?,?,?>>(), totalArmies, 4, true);
				cost = (distance == Integer.MAX_VALUE) ? Double.POSITIVE_INFINITY : (double) distance / totalArmies;
			}
			
			return cost;
		}

		public Advice<AbstractContinent<?,?>> getTarget() {
			return target;
		}
		
		private int getDistanceToContinent(AbstractCountry<?,?,?> origin, 
				AbstractContinent<?,?> destination, Collection<AbstractCountry<?,?,?>> visited, 
				int maxCost, int maxHops, boolean mine) {
			return Attacks.getDistanceToContinent(origin, destination, player, visited, maxCost, maxHops, mine);
		}
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof TargettedFortification))
				return false;
			
			TargettedFortification other = (TargettedFortification) obj;
			return other.getTarget().equals(target) && other.getCountry() == getCountry() && getArmies() == other.getArmies();
		}
		
		@Override
		public String toString() { // TODO delete this
			return super.toString() + " " + target.getObject();
		}
	}

	private final AbstractRiskGame<?, ?, ?> game;
	private final AbstractPlayer<?> player;
	private final int extraArmies;
	
	public FortificationAdvisor(AbstractRiskGame<?, ?, ?> game, AbstractPlayer<?> player, int extraArmies) {
		super("fcl/fortification.fcl", "country");
		
		this.game = game;
		this.player = player;
		this.extraArmies = extraArmies;
	}
	
	public FortificationAdvisor(AbstractRiskGame<?, ?, ?> game, AbstractPlayer<?> player) {
		this(game, player, player.getExtraArmies());
	}
	
	
	@Override
	protected double evaluate(Fortification command) {
		TargettedFortification tf = (TargettedFortification) command;
		
		double cost = tf.getCost();
		if(cost == Double.POSITIVE_INFINITY)
			return Double.NEGATIVE_INFINITY;
		
		FunctionBlock block = getFunctionBlock();
		block.reset(true);
		block.setVariable("continent", tf.getTarget().getValue());
		block.setVariable("cost", tf.getCost());
		block.evaluate();
		double val = block.getVariable("fortification").getDefuzzifier().defuzzify();
		
		/*System.out.println("[" + val + "] continent: " + tf.getTarget().getObject() + " " + tf.getTarget().getValue()
				+ " cost: " + tf.getCost()
				+ " player: " + Indices.power(player, game)
				+ " country: " + tf.getCountry());*/
		
		return val;
	}

	@Override
	protected List<Fortification> generate() {
		List<Advice<AbstractContinent<?,?>>> targets = new ContinentAdvisor(game, player).getAdvices();
		
		
		List<Fortification> candidates = new LinkedList<Fortification>();
		for(AbstractCountry<?,?,?> country : player.getTerritoriesOwned()) {
			for(AbstractCountry<?, ?, ?> neighbour : country.getNeighbours()) {
				if(neighbour.getOwner() != player) {
					for(Advice<AbstractContinent<?,?>> t : targets) {
						TargettedFortification tf = new TargettedFortification(t, country, extraArmies);
						if(tf.getCost() != Double.POSITIVE_INFINITY)
							candidates.add(tf);
					}
					break;
				}
			}
		}
		//System.out.println(candidates.size() + " " + candidates);
		return candidates;
	}
}
