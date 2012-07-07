package it.unisannio.legiolinteata.search;


import java.util.ArrayList;
import java.util.List;

import net.yura.domination.engine.core.AbstractCountry;
import net.yura.domination.engine.core.AbstractPlayer;
import net.yura.domination.engine.core.AbstractRiskGame;

import aima.core.search.adversarial.Game;

//FIXME qualcuno ha un nome migliore?
@SuppressWarnings("rawtypes")
public class TreeRiskGame implements Game<GameState, FortificationAction, TPlayer>{
	
	private static TPlayer[] players = null;
	
	private final AbstractRiskGame game;
//	private final Player myPlayer;
	private static int myPlayerIndex;
	
	public TreeRiskGame(AbstractRiskGame game, AbstractPlayer myPlayer){ 
		this.game = game;
//		this.myPlayer = myPlayer;
		
		if(players == null){
			players = new TPlayer[game.getPlayers().size()];

			for(int i = 0; i < game.getPlayers().size();i++){
				AbstractPlayer p = (AbstractPlayer) game.getPlayers().get(i);
				players[i] = new TPlayer(p);
				if (p == myPlayer)
					myPlayerIndex = i;
			}
		}
	}

	@Override
	public List<FortificationAction> getActions(GameState state) {
		List<FortificationAction> actions = new ArrayList<FortificationAction>();
		for(Integer country : state.getFreeCountries())
			actions.add(new FortificationAction(country, getPlayer(state).getColor()));
		return actions;
	}

	@Override
	public GameState getInitialState() {
		int[] countryArmies = new int[game.getCountries().length];
		int[] countryOwner = new int[game.getCountries().length];
		for(AbstractCountry country: game.getCountries()){
			countryArmies[country.getColor()-1] = country.getArmies();
			countryOwner[country.getColor()-1] = country.getOwner() != null ? country.getOwner().getColor(): 0; //FIXME un Player può avere color 0?
		}
		return new GameState(countryArmies, countryOwner,  myPlayerIndex);
	}

	@Override
	public TPlayer getPlayer(GameState state) {
		return players[state.getCurrentPlayerIndex()];
	}

	@Override
	public TPlayer[] getPlayers() {
		return players;
	}

	@Override
	public GameState getResult(GameState state, FortificationAction action) {
		GameState newState = null;
		try {
			newState = (GameState) state.clone();
			newState.place(action);
			newState.updateCurrentPlayerIndex(players);
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return newState;
	}

	@Override
	public double getUtility(GameState state, TPlayer player) {
		return new PositionUtilityCalculator().evaluateUtility(this, state, player);
	}

	@Override
	public boolean isTerminal(GameState state) {
		return (state.getFreeCountries().isEmpty());
	}
	
	public AbstractRiskGame getGame() {
		return game;
	}
	
}
