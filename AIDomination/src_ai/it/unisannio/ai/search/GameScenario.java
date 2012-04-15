package it.unisannio.ai.search;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


import net.yura.domination.engine.core.Country;

public class GameScenario implements Comparable<GameScenario> {
	public static enum State { FORTIFY, ATTACK, ROLL, MOVE; }
	
	private Set<Country> possessions;
	private Map<Country, Integer> armies;
	private State state;
	
	public float getUtility() {
		return 0.0f;
	}
	
	public List<GameMutation> getMutations() {
		List<GameMutation> mutations = new LinkedList<GameMutation>();
		switch(state) {
		case FORTIFY:
			break;
			
		case ATTACK:
			break;
			
		case ROLL:
			break;
			
		case MOVE:
			break;
		}
		
		return mutations;
	}

	@Override
	public int compareTo(GameScenario o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
