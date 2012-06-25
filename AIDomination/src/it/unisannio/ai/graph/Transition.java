package it.unisannio.ai.graph;

import it.unisannio.ai.graph.model.Edge;
import it.unisannio.ai.graph.model.GraphTraversal;
import it.unisannio.ai.graph.model.UtilityCalculator;

public abstract class Transition implements /*Comparable<Transition>,*/ Edge<Scenario, Transition> {
	private Scenario next;
	private float utility = Float.MIN_VALUE;
	
	public Scenario getChild() {
		if(next == null) {
			next = buildNext();
		}
		
		return next;
	}
	
	public final float getUtility(UtilityCalculator<Scenario> utilityCalculator, GraphTraversal<Scenario, Transition> traversal) {
		//FIXME
		return utility == Float.MIN_VALUE ? utility = computeUtility(utilityCalculator, traversal) : utility;
	}
	
	protected abstract float computeUtility(UtilityCalculator<Scenario> calculator, GraphTraversal<Scenario, Transition> traversal);
	
	protected abstract Scenario buildNext();
	
	public int compareTo(Transition other, UtilityCalculator<Scenario> calculator, GraphTraversal<Scenario, Transition> traversal) {
		float mine = getUtility(calculator, traversal);
		float others = other.getUtility(calculator, traversal);
		
		return (mine == others) ? 0 : (mine < others) ? -1 : 1;
	}
}
