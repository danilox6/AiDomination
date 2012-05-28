package it.unisannio.ai.graph;

import it.unisannio.ai.search.SearchUtility;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;

public class Scenario {
	

	public static class Builder {
		private final GraphSetup setup;
		
		private int[] countryArmies;
		private HashMap<Integer, TPlayer> players;
//		private boolean[] countryIsOwned;
		private State state;
//		private int extraArmies;
		private TPlayer currentPlayer;
		
		public Builder(Scenario s) {
			setup = s.setup;
			countryArmies = s.countryArmies.clone();
//			countryIsOwned = s.countryIsOwned.clone();
			players = s.players;
			currentPlayer = s.currentPlayer;
			state = s.state;
//			extraArmies = s.extraArmies;
		}
		
		public Builder(GraphSetup setup) {
			this.setup = setup;
			
			RiskGame game = setup.getGame();
//			Player player = setup.getPlayer();
			
			Country[] countries = game.getCountries();
			
			state = null; 
			int gamestate = game.getState();
			switch (gamestate) {
			case RiskGame.STATE_PLACE_ARMIES:
				if(!game.NoEmptyCountries())
					state = State.SETUP_PLACE; //_SELF;
				else if(!game.getSetup())
					state = State.SETUP_FORTIFY;
				else 
					state = State.FORTIFY;
				break;
			case RiskGame.STATE_ATTACKING:
				state = State.ATTACK;
				break;
			case RiskGame.STATE_ROLLING:
				state = State.ATTACK_ROLL;
				break;
			case RiskGame.STATE_FORTIFYING:
				state = State.MOVE;
				break;
			default:
				throw new IllegalStateException("Cannot create a Scenario from this state!");
			}
			 
//			extraArmies = player.getExtraArmies();
			countryArmies = new int[countries.length];
//			countryIsOwned = new boolean[countries.length];
			this.players = new HashMap<Integer, TPlayer>();
			
			Vector<Player> gPlayers = game.getPlayers();
			
			for(int i = 0; i< gPlayers.size(); i++){
				Player p = gPlayers.get(i);
				TPlayer tPlayer = new TPlayer(p.getColor(), i, p.getExtraArmies());
				this.players.put(p.getColor(), tPlayer);
				if (p == game.getCurrentPlayer())
					currentPlayer = tPlayer;
			}
			
			for(Country c : countries) {
				countryArmies[c.getColor() - 1] = c.getArmies();
				if(c.getOwner() != null)
					this.players.get(c.getOwner().getColor()).addCountry(c.getColor());
				
//				countryIsOwned[c.getColor() - 1] = c.getOwner() == player;
			}
		}
		
		public Builder(RiskGame game, Player player) {
			this(new GraphSetup(game, player));
		}
		
		public State getState() {
			return state;
		}
		
		public Builder setState(State s) {
			state = s;
			return this;
		}
		
		public int getArmies(int countryId) {
			return countryArmies[countryId - 1];
		}
		
		public Builder setArmies(int countryId, int n) {
			countryArmies[countryId - 1] = n;
			return this;
		}
		
		public Builder sumArmies(int countryId, int n) {
			countryArmies[countryId - 1] = countryArmies[countryId - 1] + n;
			return this;
		}
		
		public Builder moveArmies(int originId, int destinationId, int n) {
			sumArmies(originId, -n);
			sumArmies(destinationId, n);
			return this;
		}
		
//		public boolean ownsCountry(int countryId) {
//			return countryIsOwned[countryId];
//		}
		
//		public Builder setOwnsCountry(int countryId, boolean owns) {
//			countryIsOwned[countryId - 1] = owns;
//			return this;
//		}
//		
//		public Builder setExtraArmies(int extraArmies) {
//			this.extraArmies = extraArmies;
//			return this;
//		}
//		
//		public int getExtraArmies() {
//			return extraArmies;
//		}
//		
//		public Builder subExtraArmies(int n) {
//			this.extraArmies -= n;
//			return this;
//		}
		
		@Override
		public String toString() {
			return "Builder: " + computeId(state, countryArmies, currentPlayer);
		}
		
		public Scenario create() {
			String id = computeId(state, countryArmies, currentPlayer);
			SoftReference<Scenario> cachedEntry = nodeCache.get(id);
			Scenario scenario = (cachedEntry == null) ? null : cachedEntry.get();
			
			if(scenario == null) {
				scenario = new Scenario(setup, state, countryArmies, currentPlayer);
				nodeCache.put(id, new SoftReference<Scenario>(scenario));
			}
			
			return scenario;
		}
	}
	
	public static enum State {
		SETUP_PLACE(true),
//		SETUP_PLACE_OTHER(false),
//		SETUP_FORTIFY_SELF(true),
		SETUP_FORTIFY(true),
//		SETUP_FORTIFY_OTHER(false), 
		END_SETUP(false, true), // if we're evaluating the setup stop here
		
		FORTIFY(true), 
		ATTACK(true), 
		ATTACK_ROLL(false), 
		OCCUPY(true), 
		MOVE(true), 
		//FORTIFY_OTHER(true),
		//DEFEND(false), 
		//DEFEND_ROLL(false), 
		END_TURN(false, true); // stop after a turn
		
		private final boolean isFinal;
		private final boolean requiresMove;
		
		private State(boolean requiresMove) {
			this(requiresMove, false);
		}
		
		private State(boolean requiresMove, boolean isFinal) {
			this.requiresMove = requiresMove;
			this.isFinal = isFinal;
		}
		
		public boolean isFinal() {
			return isFinal;
		}
		
		public boolean requiresMove() {
			return requiresMove;
		}	
	}
	
	public static void wipe() {
		nodeCache = new HashMap<String, SoftReference<Scenario>>();
	}
	
	public static Scenario create(RiskGame game, Player player) {
		return new Builder(game, player).create();
	}
	
	private static Map<String, SoftReference<Scenario>> nodeCache = new HashMap<String, SoftReference<Scenario>>();
	
	private final GraphSetup setup;
	
	private final int[] countryArmies;
	private final HashMap<Integer, TPlayer> players = new HashMap<Integer, TPlayer>();
//	private final boolean[] countryIsOwned;
	private final State state;
	private final TPlayer currentPlayer;
//	private final int extraArmies;
	
	private transient Transition[] transitions;
	private transient float utility = Float.MIN_VALUE;
	
//	private static String computeId(State state, int[] countryArmies, boolean[] countryIsOwned, int extraArmies) {
//		StringBuilder buf = new StringBuilder();
//		buf.append(state);
//		for(int i=0; i < countryArmies.length; ++i) {
//			buf.append(' ').append(countryArmies[i]);
//			if(countryIsOwned[i])
//				buf.append('*');
//		}
//		buf.append(" (+").append(extraArmies).append(')');
//		
//		return buf.toString();
//	}
	
	private static String computeId(State state, int[] countryArmies, TPlayer tPlayer) {
		StringBuilder buf = new StringBuilder();
		buf.append(state);
		for(int i=0; i < countryArmies.length; ++i) {
			buf.append(' ').append(countryArmies[i]);
			if(tPlayer.ownsCountry(i))
				buf.append('*');
		}
		buf.append(" (+").append(tPlayer.getExtraArmies()).append(')');
		
		return buf.toString();
	}
	
	
	private Scenario(GraphSetup setup, State state, int[] countryArmies, TPlayer currentPlayer) {
		this.setup = setup;
		this.state = state;
		this.countryArmies = countryArmies;
		this.currentPlayer = currentPlayer;
	}

	
	public Transition[] getTransitions() {
		if(transitions == null) {
			List<Transition> list = null;

			switch(state) {
				case SETUP_PLACE: //_SELF: case SETUP_PLACE_OTHER:
					list = buildPlacementTransitions();
					break;countryColor
					
				case SETUP_FORTIFY: //_SELF: case SETUP_FORTIFY_OTHER:
					list = buildSetupFortificationTransitions();
					break;
					
				case FORTIFY: //_SELF:
					list = buildFortificationTransitions();
					break;
					
				case ATTACK:
					list = buildAttackTransitions();
					break;
					
				case ATTACK_ROLL: //case DEFEND_ROLL:
					list = buildRollTransitions();
					break;
					
				case OCCUPY:
					list = buildOccupationTransitions();
					break;
					
				case MOVE:
					list = buildMoveTransitions();
					break;
					
//				case DEFEND:
//					list = buildDefenseTransitions();
//					break;
//					
				case END_TURN:
					list = Collections.emptyList();
					break;
			}
			
			transitions = list.toArray(new Transition[0]);
		}
		
		return transitions;
	}
	
	private List<Transition> buildPlacementTransitions() {
		List<Transition> list = new LinkedList<Transition>();
		
		for(int i = 0; i < countryArmies.length; ++i) {
			final int countryId = i + 1;
			if(countryArmies[i] == 0) {
				list.add(new CommandTransition("placearmies %d 1", countryId) {

					@Override
					protected Scenario buildNext() {
						currentPlayer.addCountry(countryId);
						return new Builder(Scenario.this)
								.setArmies(countryId, 1)
								//FIXME Controllare fine stato e numero extraArmies
//								.setState(state == State.SETUP_PLACE_SELF ? State.SETUP_PLACE_OTHER : State.SETUP_PLACE_SELF) 
								.create();
					}
					
				});
			}
		}
		
		if(list.isEmpty())
			list.add(new StateTransition(this, state == State.SETUP_PLACE ? State.SETUP_FORTIFY : State.SETUP_FORTIFY));
		
		return list;
	}

//	private List<Transition> buildDefenseTransitions() {
//		return null;
//	}

	private List<Transition> buildMoveTransitions() {
		List<Transition> transitions = new LinkedList<Transition>();
		for(final Country country : setup.getGame().getCountries()) {
			if(!currentPlayer.ownsCountry(country.getColor()))
				continue;
			
			for(final Country neighbour : country.getNeighbours()) {
				if(!currentPlayer.ownsCountry(country.getColor()))
					continue;
				
				transitions.add(new CommandTransition("movearmies %d %d", country.getColor(), neighbour.getColor()) {

					@Override
					protected Scenario buildNext() {
						return new Builder(Scenario.this)
								.moveArmies(country.getColor(), neighbour.getColor(), countryArmies[country.getColor() - 1] - 1)
								//.setState(State.DEFEND)
								.setState(State.END_TURN)
								.create();
					}
				});
			}
		}
		
		transitions.add(new CommandTransition("nomove") {

			@Override
			protected Scenario buildNext() {
				return new Builder(Scenario.this)./*setState(State.DEFEND)*/setState(State.END_TURN).create();
			}
			
		});
	
		return transitions;
	}

	private List<Transition> buildOccupationTransitions() {
		// TODO Auto-generated method stub
		return null;
	}

	private List<Transition> buildRollTransitions() {
		// TODO Auto-generated method stub
		return null;
	}

	private List<Transition> buildAttackTransitions() {
		List<Transition> transitions = new LinkedList<Transition>();
		
		for(Country country : setup.getGame().getCountries()) {
			if(!currentPlayer.ownsCountry(country.getColor()))
				continue;
			
			int myArmies = countryArmies[country.getColor() - 1];
					
			for(Country neighbour : country.getNeighbours()) {
				if(currentPlayer.ownsCountry(country.getColor()))
					continue;
				
				int neighbourArmies = countryArmies[neighbour.getColor() - 1];
				float victoryLikelihood = SearchUtility.victoryProbability(myArmies, neighbourArmies);
				
				if(victoryLikelihood < setup.getAttackThreshold())
					continue;
				
				transitions.add(new CommandTransition("attack %d %d", country.getColor(), neighbour.getColor()) {

					@Override
					protected Scenario buildNext() {
						return new Builder(Scenario.this).setState(State.ATTACK_ROLL).create();
					}
				});
			}
		}
		
		transitions.add(new CommandTransition("endattack") {

			@Override
			protected Scenario buildNext() {
				return new Builder(Scenario.this).setState(State.MOVE).create();
			}
			
		});
	
		return transitions;
	}

	private List<Transition> buildFortificationTransitions() {
		// TODO Auto-generated method stub
		return null;
	}

	private List<Transition> buildSetupFortificationTransitions() {
		// TODO Auto-generated method stub
		return null;
	}

	protected int getCurrentUtility() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public float getUtility() {
		if(utility != Float.MIN_VALUE)
			return utility;
		
		// Let's avoid loops by setting a temporary value: 
		// if the node is visited again during the calculation, it will return immediately
		// with this null value 
		utility = 0.0f; 
		
		if(state.isFinal()) {
			return utility = getCurrentUtility();
		}
		
		Transition[] transitions = getTransitions();
		if(state.requiresMove()) { 	// select best move
			utility = Collections.max(Arrays.asList(transitions)).getUtility();
		} else { 					// stochastic transition: get weighted average
			for(Transition t : transitions)
				utility += t.getUtility();
		}
		
		return utility;
	}
	
	@Override
	public String toString() {
		return "Scenario: " + computeId(state, countryArmies, currentPlayer);
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}
