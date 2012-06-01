package net.yura.domination.engine.ai;

import net.yura.domination.engine.core.Player;

public interface EnemyCommandsListener {
	public void onEnemyCommand(Player enemy, String command);
}
