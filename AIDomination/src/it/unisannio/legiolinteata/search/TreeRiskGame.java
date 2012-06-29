package it.unisannio.legiolinteata.search;


import java.util.ArrayList;
import java.util.List;

import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;

import aima.core.search.adversarial.Game;

//FIXME qualcuno ha un nome migliore?
public class TreeRiskGame implements Game<GameState, PlacementAction, TPlayer>{
	
	private static TPlayer[] players = null;
	private final RiskGame game;
//	private final Player myPlayer;
	private static int myPlayerIndex;
	
	public TreeRiskGame(RiskGame game, Player myPlayer){ 
		this.game = game;
//		this.myPlayer = myPlayer;
		
		if(players == null){
			players = new TPlayer[game.getPlayers().size()];

			for(int i = 0; i < game.getPlayers().size();i++){
				Player p = game.getPlayers().get(i);
				players[i] = new TPlayer(p);
				if (p == myPlayer)
					myPlayerIndex = i;
			}
		}
	}

	@Override
	public List<PlacementAction> getActions(GameState state) {
		List<PlacementAction> actions = new ArrayList<PlacementAction>();
		for(Integer country : state.getFreeCountries())
			actions.add(new PlacementAction(country, getPlayer(state).getColor()));
		return actions;
	}

	@Override
	public GameState getInitialState() {
		int[] countryArmies = new int[game.getCountries().length];
		int[] countryOwner = new int[game.getCountries().length];
		for(Country country: game.getCountries()){
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
	public GameState getResult(GameState state, PlacementAction action) {
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
	
	public RiskGame getGame() {
		return game;
	}
	
//	public Player getMyPlayer() {
//		return myPlayer;
//	}

	//FIXME Roba di GraphSetup. Serve ancora? 
	private boolean noInnerFortification = true;
	private float attackThreshold = 0.3f;
	
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
