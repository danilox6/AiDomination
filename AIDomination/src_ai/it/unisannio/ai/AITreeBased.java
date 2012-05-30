package it.unisannio.ai;

import it.unisannio.ai.graph.CommandTransition;
import it.unisannio.ai.graph.GraphSetup;
import it.unisannio.ai.graph.Scenario;
import it.unisannio.ai.graph.traversal.*;
import it.unisannio.ai.graph.traversal.PositionUtilityCalculator;
import it.unisannio.ai.search.TreeSearcher;
import net.yura.domination.engine.ai.Discoverable;

@Discoverable
public class AITreeBased extends AISimple{
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
}
