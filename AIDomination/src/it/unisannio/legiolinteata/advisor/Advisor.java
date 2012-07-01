package it.unisannio.legiolinteata.advisor;

import java.util.List;

import net.yura.domination.engine.ai.commands.Command;


public interface Advisor<C extends Command> {
	List<C> getAdvices();
	List<C> getAdvices(int limit);
	C getBestAdvice();
}
