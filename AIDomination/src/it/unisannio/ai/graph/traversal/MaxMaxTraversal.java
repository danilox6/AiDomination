package it.unisannio.ai.graph.traversal;

import java.util.Collections;
import java.util.Comparator;

import it.unisannio.ai.graph.Scenario;
import it.unisannio.ai.graph.Transition;
import it.unisannio.ai.graph.model.GraphTraversal;
import it.unisannio.ai.graph.model.UtilityCalculator;

public class MaxMaxTraversal implements GraphTraversal<Scenario, Transition>{

	@Override
	public Transition traverse(Scenario root, final UtilityCalculator<Scenario> calculator) {
		
		System.out.println(root);
		
		return Collections.max(root.getEdges(), new Comparator<Transition>() {
			@Override
			public int compare(Transition arg0, Transition arg1) {
				return arg0.compareTo(arg1, calculator, MaxMaxTraversal.this);
			}
		});
	}

}
