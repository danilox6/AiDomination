package it.unisannio.legiolinteata.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import aima.core.search.adversarial.AdversarialSearch;
import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;
import it.unisannio.legiolinteata.advisor.Advisor.Advice;
import it.unisannio.legiolinteata.advisor.AttackAdvisor;
import it.unisannio.legiolinteata.advisor.Attacks;
import it.unisannio.legiolinteata.advisor.ContinentAdvisor;
import it.unisannio.legiolinteata.advisor.FortificationAdvisor;
import it.unisannio.legiolinteata.search.GameState;
import it.unisannio.legiolinteata.search.PlacementAction;
import it.unisannio.legiolinteata.search.TreeRiskGame;
import net.yura.domination.engine.ai.api.AIPlayer;
import net.yura.domination.engine.ai.api.Discoverable;
import net.yura.domination.engine.ai.commands.Attack;
import net.yura.domination.engine.ai.commands.Fortification;
import net.yura.domination.engine.ai.commands.Move;
import net.yura.domination.engine.core.AbstractContinent;
import net.yura.domination.engine.core.AbstractCountry;
import net.yura.domination.engine.core.Country;

@Discoverable
public class AIFuzzy extends FallbackAI {
	private Executor executor;
	
	protected Attack onAttack() {
        AttackAdvisor aa = new AttackAdvisor(game, player);
        Attack best = aa.getBestAdvice(5.0);
        //if(best != null)
        //	System.out.println(best.getOrigin() + "-->" + best.getDestination());
        return best;
	}

	protected Fortification onFortification() {
		FortificationAdvisor fa = new FortificationAdvisor(game, player);
		Fortification best = fa.getBestAdvice(Double.NEGATIVE_INFINITY);
		return best;
	}
	
	@Override
	protected Country onCountrySelection() {
		if(executor == null) {
			executor = new ThreadPoolExecutor(0,1,10,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(1));
		}
		
		FutureTask<Country> task = new FutureTask<Country>(new Callable<Country>() {

			@Override
			public Country call() throws Exception {
				TreeRiskGame tRiskGame = new TreeRiskGame(game, player);
				
				AdversarialSearch<GameState, PlacementAction> search = IterativeDeepeningAlphaBetaSearch.createFor(tRiskGame, 0.0, Double.MAX_VALUE, (int) Math.floor(AIPlayer.getTimeout() * 0.9));
				PlacementAction action = search.makeDecision(tRiskGame.getInitialState());
				Country selection = game.getCountryInt(action.getCountryToOccupy());
				return selection;
			}
			
		});
		
		try {
			executor.execute(task);
			return task.get((long) (AIPlayer.getTimeout() * 0.9), TimeUnit.MILLISECONDS);
		} catch (ExecutionException e) {
			throw new RuntimeException(e.getCause());
		} catch (Exception e) {
			return super.onCountrySelection();
		} 
	}
	
}
