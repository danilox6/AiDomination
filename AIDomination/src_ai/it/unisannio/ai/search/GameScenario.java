package it.unisannio.ai.search;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import net.yura.domination.engine.core.Continent;
import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.RiskGame;

public class GameScenario implements Comparable<GameScenario>, Cloneable {
	public static enum State {INITIAL_PLACEMENT, INITIAL_FORTIFY, FORTIFY, ATTACK, ROLL, BATTLEWON, MOVE, DEFEND, DEFENDROLL, END; } 

	private static HashMap<GameScenario, List<GameMutation>> mutationCache = new HashMap<GameScenario, List<GameMutation>>();
	private static HashMap<GameScenario, Float> utilityCache = new HashMap<GameScenario, Float>();
	
	private static RiskGame game;
	protected HashMap<Integer, Integer> countries = new HashMap<Integer, Integer>();
	protected HashSet<Integer> possessions = new HashSet<Integer>();
	protected int extraArmies = 0;
	protected int enemyExtraArmies = 0;
	private State state;
	
	protected boolean enemyTurn = false;

	private int attackerId = 0, defenderId = 0;


	public GameScenario(RiskGame game){
		GameScenario.game = game;
		int gamestate = game.getState();
		switch (gamestate) {
		case RiskGame.STATE_PLACE_ARMIES:
			if(!game.NoEmptyCountries()){
				state = State.INITIAL_PLACEMENT;
			}else if(!game.getSetup())
				state = State.INITIAL_FORTIFY;
			else state = State.FORTIFY;
			break;
		case RiskGame.STATE_TRADE_CARDS:
			break;
		case RiskGame.STATE_ATTACKING:
			state = State.ATTACK;
			break;
		case RiskGame.STATE_ROLLING:
			state = State.ROLL;
			break;
		case RiskGame.STATE_FORTIFYING:
			state = State.MOVE;
			break;
		}
//		state = State.INITIAL_PLACEMENT;
		Continent[] continents = game.getContinents();
		for(Continent continent: continents){
			Vector<Country> nations = continent.getTerritoriesContained();
			for(Country nation: nations){
				countries.put(nation.getColor(), nation.getArmies());
				if(nation.getOwner()==game.getCurrentPlayer())
					possessions.add(nation.getColor());
			}
		}
		extraArmies = game.getCurrentPlayer().getExtraArmies();
		enemyExtraArmies = extraArmies;
	}


	public GameScenario(HashMap<Integer, Integer> countries,
			HashSet<Integer> possessions, int extraArmies,
			State state, int attackerId, int defenderId, boolean enemyTurn, int enemyExtraArmies) {
		this.countries = countries;
		this.possessions = possessions;
		this.extraArmies = extraArmies;
		this.state = state;
		this.attackerId = attackerId;
		this.defenderId = defenderId;
		this.enemyTurn = enemyTurn;
		this.enemyExtraArmies = enemyExtraArmies;
	}

	public float getUtility() {
		if(utilityCache.containsKey(this))
			return utilityCache.get(this);
		float utility;
		switch(state) {
		case END:
			utility = possessions.size();
			utilityCache.put(this, utility);
			return utility;
		case ROLL:
			List<GameMutation> ms =  this.getMutations();
			utility = (ms.get(0).getUtility() + ms.get(1).getUtility())/2;
		default:
			List<GameMutation> mutations =  this.getMutations();
			utility = Collections.max(mutations).getUtility();
		}
		utilityCache.put(this, utility);
		return utility;
	}
	
	
	
//private static HashMap<GameScenario, Boolean> visiting = new HashMap<GameScenario, Boolean>();
//	
//	public float getUtility() {
//		if(visiting.containsKey(this)  && visiting.get(this)) {
//			return 0;
//		}
//		
//		visiting.put(this, true);
//		
//		float utility = 0.0f;
//		
//		switch(state) {
//		case END:
//			utility = SearchUtility.getNextTurnArmies(this, false);
//			break;
//		case ROLL:
//			List<GameMutation> ms =  this.getMutations();
//			utility = (ms.get(0).getUtility() + ms.get(1).getUtility())/2;
//			break;
//		default: 
//			List<GameMutation> mutations =  this.getMutations();
//			utility = Collections.min(mutations).getUtility();
//			break;
//		}
//		
//		visiting.put(this, false);
//		return utility;
//	}
	
	
	
	

	public List<GameMutation> getMutations() {
		
		if (mutationCache.get(this)!=null)
			return mutationCache.get(this);
		
		List<GameMutation> mutations = new LinkedList<GameMutation>();
//		System.out.println(state);
		switch(state) {
		
		case INITIAL_PLACEMENT: 
			boolean added = false;
			for(Integer key: countries.keySet()){
				if(countries.get(key) == 0){
					mutations.add(new GameMutation(this, String.format("placearmies %d 1", key )));
					added = true;
				}
			}
			if(added)
				break;
		case INITIAL_FORTIFY:	//È brutto da vedere
		case FORTIFY:			// ma funziona
			for(Integer key: countries.keySet()){
				if(possessions.contains(key) != enemyTurn){
					mutations.add(new GameMutation(this, String.format("placearmies %d 1", key )));
				}
			}
			break;

		case ATTACK: 
			for(Integer key: countries.keySet()){
				if(possessions.contains(key) && countries.get(key)>1){
					Country country = game.getCountryInt(key);
					Vector<Country> neighbours = country.getNeighbours();
					for(Country n: neighbours){
						if(!possessions.contains(n.getColor())){
							mutations.add(new GameMutation(this, String.format("attack %d %d", key, n.getColor())));
						}
					}
				}
			}
			mutations.add(new GameMutation(this, "endattack"));
			break;

		case ROLL:
			float victoryProbability = SearchUtility.victoryProbability(countries.get(attackerId), countries.get(defenderId));
			mutations.add(new GameMutation(this, "won").setLikelihood(victoryProbability));
			mutations.add(new GameMutation(this, "lost").setLikelihood(1 - victoryProbability));
			break;

		case BATTLEWON:
			mutations.add(new GameMutation(this, "move all"));
			break;

		case MOVE:
			for(Integer key: countries.keySet()){
				int armies = countries.get(key);
				if(possessions.contains(key) && armies > 1){
					Country country = game.getCountryInt(key);
					Vector<Country> neighbours = country.getNeighbours();
					for(Country n: neighbours){
						if(possessions.contains(n.getColor()))
							mutations.add(new GameMutation(this, String.format("movearmies %d %d %d", country.getColor(), n.getColor(), armies-1)));
					}
				}
			}
			mutations.add(new GameMutation(this, "nomove"));
			break;

		case DEFEND:
			for(Integer key: countries.keySet()){
				Country country = game.getCountryInt(key);
				if(possessions.contains(key)){
					Vector<Country> neighbours = country.getNeighbours();
					for(Country n: neighbours){
						if(!possessions.contains(n.getColor()) && countries.get(n.getColor())>1)
							mutations.add(new GameMutation(this, String.format("defendattack %d %d", n.getColor(), country.getColor())));
					}
				}
			}
			mutations.add(new GameMutation(this, "enddefend"));
			break;

		case DEFENDROLL:
			float dVictoryProbability = SearchUtility.victoryProbability(countries.get(attackerId), countries.get(defenderId));
			mutations.add(new GameMutation(this, "won").setLikelihood(dVictoryProbability));
			mutations.add(new GameMutation(this, "lost").setLikelihood(1 - dVictoryProbability));
			break;
			
		
		}

		mutationCache.put(this, mutations);
		return mutations;
	}

	@Override
	public int compareTo(GameScenario o) {
		return (int) (o.getUtility() - this.getUtility());
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new GameScenario((HashMap<Integer, Integer>) countries.clone(),(HashSet<Integer>) possessions.clone(), extraArmies,  state, attackerId, defenderId, enemyTurn, enemyExtraArmies); 
	}


	public void setAttackerDefender(int attackerId, int defenderId){
		this.attackerId = attackerId;
		this.defenderId = defenderId;
	}
	
	

	public RiskGame getGame(){
		return game;
	}


	public int getAttackerId() {
		return attackerId;
	}


	public int getDefenderId() {
		return defenderId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GameScenario)) return false;
		GameScenario other = (GameScenario) obj;
		return (countries.equals(other.countries) && possessions.equals(other.possessions)
				&& extraArmies == other.extraArmies && 
				state == other.getState() && attackerId == other.getAttackerId() &&
				defenderId == other.getDefenderId() && enemyTurn == other.enemyTurn && enemyExtraArmies == other.enemyExtraArmies);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[] {
			countries, possessions, extraArmies,
			state, attackerId, defenderId,
			enemyTurn, enemyExtraArmies
		});
	}

}
