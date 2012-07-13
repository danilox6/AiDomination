package it.unisannio.legiolinteata.search;


import java.util.ArrayList;
import java.util.List;

import net.yura.domination.engine.core.AbstractPlayer;
import net.yura.domination.engine.core.AbstractRiskGame;

import aima.core.search.adversarial.Game;

//FIXME qualcuno ha un nome migliore?
@SuppressWarnings("rawtypes")
public class TreeRiskGame implements Game<GameState, PlacementAction, TreePlayer>{
	
	private static TreePlayer[] players = null;
	
	private final AbstractRiskGame game;
	
	public TreeRiskGame(AbstractRiskGame game, AbstractPlayer myPlayer){ 
		this.game = game;
		
		if(players == null){
			players = new TreePlayer[game.getPlayers().size()];
		}
	}

	@Override
	public List<PlacementAction> getActions(GameState state) {
		List<PlacementAction> actions = new ArrayList<PlacementAction>();
		for(Integer country : state.getFreeCountriesColor())
			actions.add(new PlacementAction(country, getPlayer(state).getColor(), 1));
		return actions;
	}

	@Override
	public GameState getInitialState() {
		return new GameState(players, game);
	}

	@Override
	public TreePlayer getPlayer(GameState state) {
		return state.getCurrentPlayer();
	}

	@Override
	public TreePlayer[] getPlayers() {
		return players;
	}

	@Override
	public GameState getResult(GameState state, PlacementAction action) {
		GameState newState = null;
		try {
			newState = (GameState) state.clone();
			newState.place(action);
			newState.updateCurrentPlayerIndex();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return newState;
	}

	@Override
	public double getUtility(GameState state, TreePlayer player) {
		return new PositionUtilityCalculator().evaluateUtility(this, state, player);
	}

	@Override
	public boolean isTerminal(GameState state) {
		return (state.getFreeCountriesColor().isEmpty());
	}
	
	public AbstractRiskGame getGame() {
		return game;
	}
	
}
