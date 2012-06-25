package it.unisannio.ai.graph.model;

public interface Edge<T extends Node<?>, E extends Edge<T,E> > {
	
	public T getChild();
	
	public float getUtility(UtilityCalculator<T> calculator, GraphTraversal<T, E> traversal); 
	
}
