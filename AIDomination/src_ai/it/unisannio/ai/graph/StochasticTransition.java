package it.unisannio.ai.graph;

public abstract class StochasticTransition extends Transition {
	private final float likelihood;
	
	public StochasticTransition(float likelihood) {
		this.likelihood = likelihood;
	}
	
	public float getLikelihood() {
		return likelihood;
	}

	@Override
	protected float computeUtility() {
		return getNext().getUtility() * likelihood;
	}

}
