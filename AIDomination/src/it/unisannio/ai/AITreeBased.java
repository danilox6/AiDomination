package it.unisannio.ai;

import it.unisannio.ai.graph.CommandTransition;
import it.unisannio.ai.graph.GraphSetup;
import it.unisannio.ai.graph.Scenario;
import it.unisannio.ai.graph.traversal.*;
import it.unisannio.ai.search.TreeSearcher;
import net.yura.domination.engine.ai.Discoverable;
import net.yura.domination.engine.ai.EnemyCommandsListener;
import net.yura.domination.engine.ai.core.AIHard;
import net.yura.domination.engine.core.Player;

@Discoverable
public class AITreeBased extends AIHard implements EnemyCommandsListener{
	TreeSearcher search = new TreeSearcher();

	@Override
	public String getPlaceArmies() {
		if(!game.NoEmptyCountries()){
			GraphSetup graphSetup = new GraphSetup(game, player);
			Scenario current = new Scenario.Builder(graphSetup).create();
			return ((CommandTransition) new MinMaxTraversal().traverse(current, new PositionUtilityCalculator(graphSetup))).getCommand();
		}
		else
			return super.getPlaceArmies();
	}

	@Override
	public void onEnemyCommand(Player enemy, String command) {
		// TODO Auto-generated method stub
	}
}
