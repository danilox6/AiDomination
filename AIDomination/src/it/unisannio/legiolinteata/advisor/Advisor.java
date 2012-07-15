package it.unisannio.legiolinteata.advisor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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
			return (int) Math.round(arg0.value - value);
		}
		
		@Override
		public String toString() {
			return "\"" + command + "\" (" + getValue() + ")";
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
	
	protected SortedSet<Advice<C>> buildAdvices() {
		List<C> candidates = generate();
		TreeSet<Advice<C>> advices = new TreeSet<Advice<C>>();
		
		for(C candidate : candidates) {
			advices.add(new Advice<C>(this, candidate));
		}
		
		return advices;
	}
	
	public List<Advice<C>> getAdvices() {
		return new ArrayList<Advice<C>>(buildAdvices());
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
		SortedSet<Advice<C>> advices = buildAdvices();
		System.out.println(advices);
		Advice<C> best = advices.first();
		System.out.println("Picked " + best);
		
		return (best.getValue() < cutoff) ? null : best.getCommand();
	}
}
