package it.unisannio.ai.graph.model;

public interface GraphTraversal<T extends Node, V extends Edge> {
	
	public V traverse(T root, UtilityCalculator<T> calculator);
}
