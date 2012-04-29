package it.unisannio.ai.graph;

import it.unisannio.ai.graph.Scenario.State;
import it.unisannio.ai.graph.Scenario.Builder;

public class StateTransition extends Transition {
	private final State next;
	private final Scenario origin;
	
	public StateTransition(Scenario origin, State next) {
		this.next = next;
		this.origin = origin;
	}
	
	@Override
	protected float computeUtility() {
		return getNext().getUtility();
	}

	@Override
	protected Scenario buildNext() {
		return new Builder(origin).setState(next).create();
	}

}
