package it.unisannio.ai.search;

public class GameMutation implements Comparable<GameMutation> {
	private GameScenario origin;
	private String command;
	private GameScenario destination;
	private float likelihood;
	
	public float getUtility() {
		return 0.0f;
	}

	@Override
	public int compareTo(GameMutation arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}
