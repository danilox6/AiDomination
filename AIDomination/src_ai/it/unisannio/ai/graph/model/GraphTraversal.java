package it.unisannio.ai.graph.model;

public abstract class GraphTraversal {
	
	protected UtilityCalculator utilityCalculator;
	
	public GraphTraversal(UtilityCalculator utilityCalculator) {
		this.utilityCalculator = utilityCalculator;
	}
	
	public abstract void traverse(Node root);
}
