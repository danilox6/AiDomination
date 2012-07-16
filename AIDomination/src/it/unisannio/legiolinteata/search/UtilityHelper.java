package it.unisannio.legiolinteata.search;


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
