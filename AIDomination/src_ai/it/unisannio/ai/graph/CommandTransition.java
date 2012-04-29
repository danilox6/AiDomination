package it.unisannio.ai.graph;

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
	protected float computeUtility() {
		return getNext().getUtility();
	}

}
