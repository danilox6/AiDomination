package it.unisannio.legiolinteata.advisor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.jFuzzyLogic.FIS;
import net.yura.domination.engine.ai.commands.Command;


public abstract class Advisor<C extends Command> {
	public static final class Advice<C extends Command> implements Comparable<Advice<C>>{
		private final C command;
		private final double value;
		
		private Advice(Advisor<C> advisor, C command) {
			this.command = command;
			this.value = advisor.evaluate(command);
		}
		
		public final C getCommand() {
			return command;
		}
		
		public final double getValue() {
			return value;
		}
		
		@Override
		public int compareTo(Advice<C> arg0) {
			return (int) (arg0.value - value);
		}
		
	}
	
	private FIS logic;
	
	public Advisor(String fcl) {
		logic = FIS.load(fcl);
		if(logic == null) {
			throw new RuntimeException("Cannot load fuzzy inference system '" + fcl + "'");
		}
	}
	
	protected abstract List<C> generate();
	protected abstract double evaluate(C command);
	
	public FIS getFuzzyInferenceSystem() {
		return logic;
	}
	
	protected List<Advice<C>> buildAdvices() {
		List<C> candidates = generate();
		List<Advice<C>> advices = new ArrayList<Advice<C>>(candidates.size());
		
		for(C candidate : candidates) {
			advices.add(new Advice<C>(this, candidate));
		}
		
		return advices;
	}
	
	public List<Advice<C>> getAdvices() {
		List<Advice<C>> advices = buildAdvices();
		Collections.sort(advices);
		
		return advices;
	}
	
	public List<C> getBestAdvices(int limit, double cutoff) {
		List<Advice<C>> advices = getAdvices();

		limit = Math.min(limit, advices.size());
		List<C> best = new ArrayList<C>(limit);
		
		
		for(int i = 0; i < limit; ++i) {
			Advice<C> a = advices.get(i);
			if(a.getValue() < cutoff) 
				break;
			
			best.add(advices.get(i).getCommand());
		}
		
		return best;
	}
	
	public C getBestAdvice(double cutoff) {
		List<Advice<C>> advices = buildAdvices();
		Advice<C> best = Collections.max(advices);
		
		return (best.getValue() < cutoff) ? null : best.getCommand();
	}
}
