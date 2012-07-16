package it.unisannio.ai.graph.model;

import it.unisannio.legiolinteata.search.GameState;

public class UtilityHelper {
	public static GameState bestState = null;
	public static float bestUtility = Float.MIN_VALUE;
	
	public static void setGameState(GameState state, float utility){
		if (utility > bestUtility){
			bestUtility = utility;
			bestState = state;
		}
	}
	
	public static void clear(){
		bestState = null;
		bestUtility = Float.MIN_VALUE;
	}
}
