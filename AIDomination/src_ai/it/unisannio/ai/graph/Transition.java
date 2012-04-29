package it.unisannio.ai.graph;

public abstract class Transition implements Comparable<Transition> {
	private Scenario next;
	private float utility = Float.MIN_VALUE;
	
	public Scenario getNext() {
		if(next == null) {
			next = buildNext();
		}
		
		return next;
	}
	
	public final float getUtility() {
		return utility == Float.MIN_VALUE ? utility = computeUtility() : utility;
	}
	
	protected abstract float computeUtility();
	
	protected abstract Scenario buildNext();
	
	@Override
	public int compareTo(Transition other) {
		float mine = getUtility();
		float others = other.getUtility();
		
		return (mine == others) ? 0 : (mine < others) ? -1 : 1;
	}
}
