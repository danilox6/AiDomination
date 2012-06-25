package it.unisannio.ai.graph.model;

public interface UtilityCalculator<T extends Node> {
	
	float evaluateUtility(T node);
}
