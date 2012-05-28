package it.unisannio.ai.graph.model;


import java.util.List;

public abstract class Node {
	
	abstract public List<Node> getChilds();
	
	abstract public List<Edge> getEdges();
}
