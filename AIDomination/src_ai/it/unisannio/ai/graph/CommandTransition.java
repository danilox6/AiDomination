package it.unisannio.ai.graph;

import it.unisannio.ai.graph.model.GraphTraversal;
import it.unisannio.ai.graph.model.UtilityCalculator;

public abstract class CommandTransition extends Transition {
	private final String command;
	
	public CommandTransition(String command) {
		this.command = command;
	}
	
	public CommandTransition(String commandFormat, Object... args) {
		this(String.format(commandFormat, args));
	}
	
	public String getCommand() {
		return command;
	}
	
	@Override
	protected float computeUtility(UtilityCalculator<Scenario> calculator, GraphTraversal<Scenario, Transition> traversal) {
		return getChild().getUtility(calculator, traversal);
	}


}
