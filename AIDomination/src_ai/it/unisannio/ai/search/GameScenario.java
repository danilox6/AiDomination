package it.unisannio.ai.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import net.yura.domination.engine.core.Continent;
import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;

public class GameScenario implements Comparable<GameScenario>, Cloneable {
	public static enum State {INITIAL_PLACEMENT, INITIAL_FORTIFY, FORTIFY, ATTACK, ROLL, BATTLEWON, MOVE, DEFEND, DEFENDROLL, END; } //FIXME Serve uno stato END?

	private static RiskGame game;
	protected HashMap<Integer, Integer> countries = new HashMap<Integer, Integer>();
	protected HashSet<Integer> possessions = new HashSet<Integer>();
	protected int extraArmies = 0;
	private float likelihood;
	private State state;

	private int attackerId, defenderId;


	public void setup(RiskGame game, Player player){
		GameScenario.game = game;
		//		int gamestate = game.getState();
		//		switch (gamestate) {
		//		case RiskGame.STATE_PLACE_ARMIES:
		//			if(!game.NoEmptyCountries())
		//				state = State.INITIAL_PLACEMENT;
		//			else if(!game.getSetup())
		//				state = State.INITIAL_FORTIFY;
		//			else state = State.FORTIFY;
		//			break;
		//		case RiskGame.STATE_TRADE_CARDS:
		//			break;
		//		case RiskGame.STATE_ATTACKING:
		//			state = State.ATTACK;
		//			break;
		//		case RiskGame.STATE_ROLLING:
		//			state = State.ROLL;
		//			break;
		//		case RiskGame.STATE_FORTIFYING:
		//			state = State.BATTLEWON;
		//			break;
		//		}
		state = State.INITIAL_PLACEMENT;
		Continent[] continents = game.getContinents();
		for(Continent continent: continents){
			Vector<Country> nations = continent.getTerritoriesContained();
			for(Country nation: nations){
				countries.put(nation.getColor(), nation.getArmies());
				if(nation.getOwner()==player)
					possessions.add(nation.getColor());
			}
		}
	}


	public GameScenario(HashMap<Integer, Integer> countries,
			HashSet<Integer> possessions, int extraArmies, float likelihood,
			State state, int attackerId, int defenderId) {
		this.countries = countries;
		this.possessions = possessions;
		this.extraArmies = extraArmies;
		this.likelihood = likelihood;
		this.state = state;
		this.attackerId = attackerId;
		this.defenderId = defenderId;
	}

	//TODO Definire utilità
	public float getUtility() {
		switch(state) {
		case INITIAL_PLACEMENT:
			//Posti migliori per percentuale di occupazione di un contintente
			//e vicinanza tra nazioni
			return 0;
		case INITIAL_FORTIFY:
			return 0;
		default:
			return SearchUtility.getNextTurnArmies(this);
		}
	}

	public List<GameMutation> getMutations() {
		List<GameMutation> mutations = new LinkedList<GameMutation>();
		switch(state) {
		
		case INITIAL_PLACEMENT: //È brutto da vedere
		case INITIAL_FORTIFY:	// ma funziona
		case FORTIFY:
			for(Integer key: countries.keySet()){
				if(possessions.contains(key)){
					mutations.add(new GameMutation(this, String.format("placearmies %d 1", key )));
				}
			}
			break;

		case ATTACK: 
			for(Integer key: countries.keySet()){
				if(possessions.contains(key)){
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
				Country country = game.getCountryInt(key);
				if(possessions.contains(key)){
					Vector<Country> neighbours = country.getNeighbours();
					for(Country n: neighbours){
						if(possessions.contains(n.getColor()))
							mutations.add(new GameMutation(this, String.format("movearmies %d %d %d", country.getColor(), n.getColor(), countries.get(country.getColor())-1)));
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
						if(possessions.contains(n.getColor()))
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
		return new GameScenario((HashMap<Integer, Integer>) countries.clone(),(HashSet<Integer>) possessions.clone(), extraArmies, likelihood, state, attackerId, defenderId); 
	}

	public float getLikelihood() {
		return likelihood;
	}

	public void setLikelihood(float likelihood) {
		this.likelihood = likelihood;
	}

	public void setAttackerDefender(int attackerId, int defenderId){
		this.attackerId = attackerId;
		this.defenderId = defenderId;
	}

	public RiskGame getGame(){
		return game;
	}


}