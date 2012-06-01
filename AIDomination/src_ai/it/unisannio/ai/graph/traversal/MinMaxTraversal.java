package it.unisannio.ai.graph.traversal;

import java.util.Collections;
import java.util.Comparator;

import it.unisannio.ai.graph.Scenario;
import it.unisannio.ai.graph.Transition;
import it.unisannio.ai.graph.model.GraphTraversal;
import it.unisannio.ai.graph.model.UtilityCalculator;

public class MinMaxTraversal implements GraphTraversal<Scenario, Transition>{

	@Override
	public Transition traverse(Scenario root, final UtilityCalculator<Scenario> calculator) {
		
		Comparator<Transition> comparator = new Comparator<Transition>() {
			@Override
			public int compare(Transition arg0, Transition arg1) {
				return arg0.compareTo(arg1, calculator, MinMaxTraversal.this);
			}
		};
		
		return (root.getGraphSetup().getPlayer().getColor()==root.getPlayers().getFirst())?
			Collections.max(root.getEdges(), comparator) : Collections.min(root.getEdges(), comparator);
	}

}
