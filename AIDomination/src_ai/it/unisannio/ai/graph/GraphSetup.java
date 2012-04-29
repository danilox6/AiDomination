package it.unisannio.ai.graph;

import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;

public class GraphSetup {
	private final RiskGame game;
	private final Player player;
	
	private boolean noInnerFortification = true;
	private float attackThreshold = 0.3f;
	
	public GraphSetup(RiskGame game, Player player) {
		this.game = game;
		this.player = player;
	}
	
	public RiskGame getGame() {
		return game;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean getNoInnerFortification() {
		return noInnerFortification;
	}
	
	public float getAttackThreshold() {
		return attackThreshold;
	}
	
	public void setNoInnerFortification(boolean value) {
		noInnerFortification = value;
	}
	
	public void setAttackThreshold(float value) {
		attackThreshold = value;
	}
}
