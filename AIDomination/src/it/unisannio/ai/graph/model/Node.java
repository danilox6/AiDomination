package it.unisannio.ai.graph.model;


import java.util.List;

public interface Node<T extends Edge> {
	
	public List<T> getEdges();
}
