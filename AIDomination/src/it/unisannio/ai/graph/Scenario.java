package it.unisannio.ai.graph;

import it.unisannio.ai.graph.model.GraphTraversal;
import it.unisannio.ai.graph.model.Node;
import it.unisannio.ai.graph.model.UtilityCalculator;

import java.lang.ref.SoftReference;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;

public class Scenario implements Node<Transition>{


	public static class Builder {
		private final GraphSetup setup;

		private int[] countryArmies;
		private int[] ownedCountries;

		private Deque<Integer> players = new LinkedList<Integer>();

		public Builder(Scenario s) {
			setup = s.setup;
			countryArmies = s.countryArmies.clone();
			players = s.players;
			ownedCountries = s.ownedCountries;
		}

		public Builder(GraphSetup setup) {
			this.setup = setup;

			RiskGame game = setup.getGame();
			
			Country[] countries = game.getCountries();

			countryArmies = new int[countries.length];
			ownedCountries = new int[countries.length];

			Vector<Player> gPlayers = game.getPlayers();
			for(Player p: gPlayers)
				players.add(p.getColor());

			for(Country c : countries){ 
				countryArmies[c.getColor() - 1] = c.getArmies();
				ownedCountries[c.getColor()-1] = (c.getOwner()==null)? 0: c.getOwner().getColor();
			}
				
		}

		public Builder(RiskGame game, Player player) {
			this(new GraphSetup(game, player));
		}

		public int getArmies(int countryId) {
			return countryArmies[countryId - 1];
		}

		public Builder switchPlayer(){
			players.addLast(players.pop());
			return this;
		}
		
		public Builder setArmies(int countryId, int n) {
			ownedCountries[countryId -1] = players.getFirst(); 
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
			return "Builder: " + computeId(countryArmies, players, ownedCountries);
		}

		public Scenario create() {
			String id = computeId(countryArmies, players, ownedCountries);
			SoftReference<Scenario> cachedEntry = nodeCache.get(id);
			Scenario scenario = (cachedEntry == null) ? null : cachedEntry.get();

			if(scenario == null) {
				scenario = new Scenario(setup, countryArmies, players, ownedCountries);
				nodeCache.put(id, new SoftReference<Scenario>(scenario));
			}

			return scenario;
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
	private Deque<Integer> players = new LinkedList<Integer>();
	private int[] ownedCountries;

	private transient List<Transition> transitions;
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

	private static String computeId(int[] countryArmies, Deque<Integer> players, int[] ownedCountries) {
		StringBuilder buf = new StringBuilder();
		for(int i=0; i < countryArmies.length; ++i) {
			buf.append(' ').append(countryArmies[i]+ ":");
			buf.append(ownedCountries[i]);
		}
		buf.append(players.toString());

		return buf.toString();
	}


	private Scenario(GraphSetup setup, int[] countryArmies, Deque<Integer> players, int[] ownedCountries) {
		this.setup = setup;
		this.countryArmies = countryArmies;
		this.players = players;
		this.ownedCountries = ownedCountries;
	}


	public Deque<Integer> getPlayers() {
		return players;
	}

	public void setPlayers(Deque<Integer> players) {
		this.players = players;
	}

	public int[] getOwnedCountries() {
		return ownedCountries;
	}

	public void setOwnedCountries(int[] ownedCountries) {
		this.ownedCountries = ownedCountries;
	}

	public int[] getCountryArmies() {
		return countryArmies;
	}

	public List<Transition> getEdges() {
		if(transitions == null) 
			transitions = buildPlacementTransitions();
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
						return new Builder(Scenario.this)
						.setArmies(countryId, 1).switchPlayer()
						//FIXME Controllare fine stato e numero extraArmies
						//								.setState(state == State.SETUP_PLACE_SELF ? State.SETUP_PLACE_OTHER : State.SETUP_PLACE_SELF) 
						.create();
					}




				});
			}
		}

		return list;
	}

	
	public GraphSetup getGraphSetup() {
		return setup;
	}

	public float getUtility(final UtilityCalculator<Scenario> calculator, GraphTraversal<Scenario, Transition> traversal) {


		if(utility != Float.MIN_VALUE)
			return utility;

		// Let's avoid loops by setting a temporary value: 
		// if the node is visited again during the calculation, it will return immediately
		// with this null value 
		utility = 0.0f; 

		if(isFinal()) {
			return utility = calculator.evaluateUtility(this);
		}

		// select best move
		utility = traversal.traverse(this, calculator).getUtility(calculator, traversal);

		return utility;

	}

	public boolean isFinal() {
		for(int i: countryArmies)
			if(i==0)
				return false;
		return true;
	}

	@Override
	public String toString() {
		return "Scenario: " + computeId(countryArmies, players, ownedCountries)+ " Is Final?"+isFinal();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}
