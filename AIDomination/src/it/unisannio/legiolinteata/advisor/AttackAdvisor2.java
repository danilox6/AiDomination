package it.unisannio.legiolinteata.advisor;

import it.unisannio.legiolinteata.advisor.Advisor.Advice;

import java.util.*;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.yura.domination.engine.ai.commands.Attack;
import net.yura.domination.engine.ai.commands.Fortification;
import net.yura.domination.engine.core.AbstractContinent;
import net.yura.domination.engine.core.AbstractCountry;
import net.yura.domination.engine.core.AbstractPlayer;
import net.yura.domination.engine.core.AbstractRiskGame;

public class AttackAdvisor2 extends Advisor<Attack> {
	
	private class TargettedAttack extends Attack {
		private Advice<AbstractContinent<?,?>> target;
		private double cost = -1;

		public TargettedAttack(Advice<AbstractContinent<?,?>> target, 
				AbstractCountry<?, ?, ?> origin, AbstractCountry<?, ?, ?> destination) {
			super(origin, destination);
			this.target = target;
		}
		
		public double getCost() {
			if(cost == -1) {
				int totalArmies = getOrigin().getArmies();
				int distance = getDistanceToContinent(getOrigin(), target.getObject(), new ArrayList<AbstractCountry<?,?,?>>(), totalArmies, 4);
				cost = (distance == Integer.MAX_VALUE) ? Double.POSITIVE_INFINITY : (double) distance / totalArmies;
			}
			
			return cost;
		}
		
		@SuppressWarnings("unchecked")
		private int getDistanceToContinent(AbstractCountry<?,?,?> origin, 
				AbstractContinent<?,?> destination, Collection<AbstractCountry<?,?,?>> visited, 
				int maxCost, int maxHops) {
			if(origin.getContinent() == destination && origin.getOwner() != player)
				return 0;
			
			if(maxCost < 0 || maxHops < 0)
				return Integer.MAX_VALUE;
			
			Collection<AbstractCountry<?,?,?>> visits = new ArrayList<AbstractCountry<?,?,?>>(visited);
			visits.add(origin);
			int currentCost = origin.getOwner() == player ? 0 : origin.getArmies();
			int minDistance = Integer.MAX_VALUE;
			for(AbstractCountry<?,?,?> neighbour : (Vector<AbstractCountry<?,?,?>>) origin.getNeighbours()) {
				if(neighbour.getOwner() != player && !visits.contains(neighbour)) {
					minDistance = Math.min(minDistance, getDistanceToContinent(neighbour, destination, visits, maxCost - currentCost, maxHops - 1));
				}
			}
			
			return minDistance == Integer.MAX_VALUE ? Integer.MAX_VALUE : currentCost + minDistance;
		}

		public Advice<AbstractContinent<?,?>> getTarget() {
			return target;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof TargettedAttack))
				return false;
			
			TargettedAttack other = (TargettedAttack) obj;
			return other.getTarget().equals(target) && other.getOrigin() == getOrigin() && getDestination() == other.getDestination();
		}
		
	}

	private final AbstractRiskGame<?, ?, ?> game;
	private final AbstractPlayer<?> player;
	
	public AttackAdvisor2(AbstractRiskGame<?, ?, ?> game, AbstractPlayer<?> player) {
		super("fcl/attack.fcl", "attack2");
		
		this.game = game;
		this.player = player;
	}
	
	protected double evaluate(Attack a) {
		TargettedAttack ta = (TargettedAttack) a;
		FunctionBlock fis = getFunctionBlock();
		
		fis.setVariable("enemy", Indices.power(a.getDestination().getOwner(), game));
		fis.setVariable("victory", Indices.victory(a.getOrigin(), a.getDestination()));
		fis.setVariable("cost", ta.getCost());
		fis.setVariable("continent", ta.getTarget().getValue());

		fis.evaluate();
		/*
		System.out.println("[" + fis.getVariable("attack").getValue() + "] " + a 
				+ " enemy: " + fis.getVariable("enemy").getValue()
				+ " victory: " + fis.getVariable("victory").getValue()
				+ " cost: " + fis.getVariable("cost").getValue()
				+ " continent: " + fis.getVariable("continent").getValue());*/
		return fis.getVariable("attack").getDefuzzifier().defuzzify();
	}
	
	@Override
	protected List<Attack> generate() {
		List<Advice<AbstractContinent<?,?>>> targets = new ContinentAdvisor(game, player).getAdvices();
		
		
		List<Attack> candidates = new LinkedList<Attack>();
		for(AbstractCountry<?,?,?> country : player.getTerritoriesOwned()) {
			if(country.getArmies() > 1) {
				for(AbstractCountry<?, ?, ?> neighbour : country.getNeighbours()) {
					if(neighbour.getOwner() != player) {
						for(Advice<AbstractContinent<?,?>> t : targets) {
							TargettedAttack tf = new TargettedAttack(t, country, neighbour);
							if(tf.getCost() != Double.POSITIVE_INFINITY)
								candidates.add(tf);
						}
						break;
					}
				}
			}
		}
		
		return candidates;
	}
}
