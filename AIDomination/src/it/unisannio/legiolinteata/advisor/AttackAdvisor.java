package it.unisannio.legiolinteata.advisor;

import java.util.*;

import net.sourceforge.jFuzzyLogic.FIS;
import net.yura.domination.engine.ai.commands.Attack;
import net.yura.domination.engine.core.AbstractCountry;
import net.yura.domination.engine.core.AbstractPlayer;
import net.yura.domination.engine.core.AbstractRiskGame;

public class AttackAdvisor implements Advisor<Attack> {
	private static Comparator<Attack> comparator = null;
	
	private Comparator<Attack> getComparator() {
		if(comparator == null) {
			comparator = new Comparator<Attack>() {
				{
					if(fis == null) {
						fis = FIS.load("fcl/attack.fcl");
						if(fis == null) {
							throw new RuntimeException("Cannot load FCL logic");
						}
					}
				}
				
				@Override
				public int compare(Attack arg0, Attack arg1) {
					return (int) Math.round(evaluate(arg1) - evaluate(arg0));
				}
				
			};
		}
		
		return comparator;
	}
	
	private static FIS fis;
	
	private double evaluate(Attack a) {
		fis.setVariable("enemy", (double) 10.0 * a.getDestination().getOwner().getTerritoriesOwnedSize()/game.getNoCountries());
		fis.setVariable("victory", getVictoryProbability(a.getOrigin().getArmies(), a.getDestination().getArmies()) * 100.0);
		
		fis.evaluate();
		return fis.getVariable("attack").getDefuzzifier().defuzzify();
	}
	
	private final AbstractRiskGame<?, ?, ?> game;
	private final AbstractPlayer<?> player;
	private final double cutoff;
	
	public AttackAdvisor(AbstractRiskGame<?, ?, ?> game, AbstractPlayer<?> player, double cutoff) {
		this.game = game;
		this.player = player;
		this.cutoff = cutoff;
	}
	
	public AttackAdvisor(AbstractRiskGame<?, ?, ?> game, AbstractPlayer<?> player) {
		this(game, player, 5.0);
	}
	
	@Override
	public List<Attack> getAdvices() {
		List<Attack> candidates = new ArrayList<Attack>();
		for(AbstractCountry<?, ?, ?> country : player.getTerritoriesOwned()) {
			@SuppressWarnings("unchecked")
			Vector<AbstractCountry<?, ?, ?>> neighbours = (Vector<AbstractCountry<?, ?, ?>>) country.getNeighbours();
			for(AbstractCountry<?, ?, ?> neighbour : neighbours) {
				if(neighbour.getOwner() != player) {
					candidates.add(new Attack(country, neighbour));
				}
			}
		}
		
		Collections.sort(candidates, getComparator());
		
		return candidates;
	}

	@Override
	public Attack getBestAdvice() {
		Attack attack = getAdvices().get(0);
		return (evaluate(attack) > cutoff) ? attack : null;
	}

	@Override
	public List<Attack> getAdvices(int limit) {
		return getAdvices().subList(0, limit);
	}

	/**
	 * Restituisce il numero di armate che saranno disponibili nel turno successivo
	 * @param scenario
	 * @param enemy vero se vogliamo calcolare le armate del nemico
	 * @return
	 */
	/*public static int getNextTurnArmies(AbstractRiskGame scenario, boolean enemy){
		int countries = (enemy)?	scenario.countries.size() - scenario.possessions.size()	:scenario.possessions.size();
		int armies = 0;
		if(countries < 9)
			armies = 3;
		else
			armies = countries/3;
		
		
		HashSet<Integer> possessions = new HashSet<Integer>();
		if(enemy){
			for(Integer c: scenario.countries.keySet())
				if(!scenario.possessions.contains(c))
					possessions.add(c);
		}
		else
		 	possessions =  scenario.possessions;
		Continent[] continents = scenario.getGame().getContinents();
		for(Continent continent: continents){
			if(possessions.containsAll(continent.getTerritoriesContained()))
				armies += continent.getArmyValue();
		}
		return armies;
	}	*/
	
	/**
	 * Restituisce il numero di armate che perse mediamente dall'attaccante per una data situazione iniziale 
	 * di armate 
	 * @param attackerArmies
	 * @param defenderArmies
	 * @return
	 */
	@SuppressWarnings("unused")
	private int getAttackerLostArmies(int attackerArmies, int defenderArmies){
		double probab  = 0, temp = 0;	
		double lostArmies = 0;
		for(int i = 1; i<=attackerArmies; i++){
			temp = computeFreeSpaceProb(attackerArmies, defenderArmies, i, 0);
			probab += temp;
			lostArmies += (attackerArmies - i) * temp;
		}
		lostArmies += (1 - probab) * attackerArmies; 
		return (int) Math.round(lostArmies);
	}
	
	/**
	 * Restituisce il numero di armate che perse mediamente dal difensore per una data situazione iniziale 
	 * di armate
	 * @param attackerArmies
	 * @param defenderArmies
	 * @return
	 */
	@SuppressWarnings("unused")
	private int getDefenderLostArmies(int attackerArmies, int defenderArmies){
		double probab  = 0, temp = 0;	
		double lostArmies = 0;
		for(int i = 1; i<=defenderArmies; i++){
			temp = computeFreeSpaceProb(attackerArmies, defenderArmies, 0, i);
			probab += temp;
			lostArmies += (defenderArmies - i) * temp;
		}
		lostArmies += (1 - probab) * defenderArmies; 
		return (int) Math.round(lostArmies);
	}
	
	
	/**
	 * Restituisce la percentuale di vittorie dell'attaccante basata sul numero di armate
	 * 
	 * @param attackerArmies
	 * @param defenderArmies
	 * @return
	 */
	private float getVictoryProbability(int attackerArmies, int defenderArmies){
		if ((attackerArmies <= 5) || (defenderArmies <= 3)) return (float) computeBoundaryProb(attackerArmies, defenderArmies);
		
		// calculate probability:
		double f0 = 2890.0/7776;
		double f1 = 2611.0/7776;
		double f2 = 2275.0/7776;
		float pOut = 0;
		for (int i = attackerArmies; i > 4; i--)
		{
			pOut += computeFreeSpaceProb(attackerArmies, defenderArmies, i, 3) * f0 * computeBoundaryProb(i, 1);
			pOut += (computeFreeSpaceProb(attackerArmies, defenderArmies, i + 1, 3) * f1 + computeFreeSpaceProb(attackerArmies, defenderArmies, i, 4) * f0) * computeBoundaryProb(i, 2);
		}
		pOut += computeFreeSpaceProb(attackerArmies, defenderArmies, 5, 3) * f1 * computeBoundaryProb(4, 2);
		for (int j = defenderArmies; j > 2; j--)
		{
			pOut += computeFreeSpaceProb(attackerArmies, defenderArmies, 5, j) * f2 * computeBoundaryProb(3, j);
			pOut += (computeFreeSpaceProb(attackerArmies, defenderArmies, 6, j) * f2 + computeFreeSpaceProb(attackerArmies, defenderArmies, 5, j + 1) * f1) * computeBoundaryProb(4, j);
		}
		
		return pOut;
	} 
	
	private float computeFreeSpaceProb(int attackerArmies, int defenderArmies, int m, int n) {
		//errors:
//		if ((attackerArmies < 4) || (m < 2) || (defenderArmies < 2) || (n < 0)) return -1;
//		if ((Math.floor(attackerArmies) != attackerArmies) || (Math.floor(defenderArmies) != defenderArmies) || (Math.floor(m) != m) || (Math.floor(n) != n)) return -1;
		double s = Math.floor((attackerArmies + defenderArmies - m - n)/2.0);
//		if (s < 0) return -1;

		//zero probability:
		if ((attackerArmies + defenderArmies - m - n) != 2 * s) return 0;	// Can't be reached by shaking 3 vs. 2 dice exclusively.
		if (m + n < 4) return 0;				// Can't be reached by shaking 3 vs. 2 dice exclusively.
		
		double f0 = 2890.0/7776;
		double f1 = 2611.0/7776;
		double f2 = 2275.0/7776;
		double L = ((m - n) - (attackerArmies - defenderArmies))/2.0;
		float pOut = 0;
		
		for (int smallL = 0; smallL <= (s - Math.abs(L))/2.0; smallL++)
		{
			double coeff = numComb(s, Math.abs(L) + smallL, smallL);
			if (L >= 0)
				pOut += coeff * Math.pow(f0, (L + smallL)) * Math.pow(f1, s - L - 2*smallL) * Math.pow(f2, smallL);
			else
				pOut += coeff * Math.pow(f0, smallL) * Math.pow(f1, s + L - 2*smallL) * Math.pow(f2, smallL - L);
		}
		
		return pOut;
	}

	private static double numComb(double s, double B, int L) {
		// numComb = (s)! / (B)!(L)!(s-B-L)!
		double C = Math.floor(B);
		double D = Math.floor(L);
		double E = Math.floor(s - C - D);
		if ((C < 0) || (C > s)) return 0;
		if ((D < 0) || (D > s)) return 0;
		if ((E < 0) || (E > s)) return 0;
		
		// shuffle so that C is the largest of the three:
		if (D > C)
		{
			double temp = C;
			C = D;
			D = temp;
		}
		if (E > C)
		{
			double temp = C;
			C = E;
			E = temp;
		}
		double nOut = 1;
		
		// start with (s!)/(C!):
		for (int i = 0; i < (s - C); i++)
			nOut *= (s - i);
		
		// divide by (D!) and (E!):
		nOut /= (float)(factorial((int) D) * factorial((int) E));
		
		return nOut;
	}
	
	private static int factorial(int n)
	{
		if (n < 0) return 0;
		if (Math.floor(n) != n) return 0;
		if (n < 2) return 1;
		int cumProduct = 1;
		for (int i = 2; i <= n; i++)
			cumProduct *= i;
		return cumProduct;
	}

	
	private static HashMap<Integer, HashMap<Integer,Double>> cache;
	
	private static double computeBoundaryProb(int m, int n) {
		// Look for an array element that stores the solution for this particular (m, n):
		if ( cache == null)
			cache = new  HashMap<Integer, HashMap<Integer,Double>>();
		else
			if (cache.get(m) != null && cache.get(m).get(n) != null)
				return cache.get(m).get(n);
		
		if ((Math.floor(m) != m) || (Math.floor(n) != n)) return -1;
		if ((m < 1) || (n < 0)) return -1;

		// zero or one probability:
		if (m == 1) return 0;	// "A"
		if (n == 0) return 1;	// "B"
		
		// pij[k] = prob. of attacker losing k armies with i dice vs. defender's j dice:
		double[] p11 = {15.0/36, 21.0/36};
		double[] p12 = {55.0/216, 161.0/216};
		double[] p21 = {125.0/216, 91.0/216};
		double[] p22 = {295.0/1296, 420.0/1296, 581.0/1296};
		double[] p31 = {855.0/1296, 441.0/1296};
		double[] p32 = {2890.0/7776, 2611.0/7776, 2275.0/7776};
		double pOut = 0;

		// recursively call this function if m > 3 and n > 2:
		if ((m > 3) && (n > 2))
		{
			pOut = p32[0] * computeBoundaryProb(m, n-2);
			pOut += p32[1] * computeBoundaryProb(m-1, n-1);
			pOut += p32[2] * computeBoundaryProb(m-2, n);
		}

		// otherwise use exact solutions:
		else if (m == 2)
		{	// "C"
			pOut = Math.pow(p12[0], (n-1)) * p11[0];
		}
		else if (n == 1)
		{	// "D"
			pOut = 1 - Math.pow(p31[1], (m-3)) * p21[1] * p11[1];
		}
		else if (m == 3)
		{	// "E"
			if ((n % 2) == 0)
			{	// n even:
				pOut = (Math.pow(p22[0], (n/2)) - Math.pow(p12[0], n));
				pOut /= (p22[0] - p12[0]*p12[0]);
				pOut *= p22[1] * p11[0];
				pOut += Math.pow(p22[0], (n/2));
			}
			else
			{	// n odd:
				pOut = (Math.pow(p22[0], (n-1)/2) - Math.pow(p12[0], n-1));
				pOut /= (p22[0] - p12[0]*p12[0]);
				pOut *= p22[1] * p12[0] * p11[0];
				pOut += Math.pow(p22[0], (n-1)/2) * (p21[0] + p21[1] * p11[0]);
			}
		}
		else if (n == 2)
		{	// "F"
			if ((m % 2) == 0)
			{	// m even:
				pOut = -(Math.pow(p32[2], m/2-2) - Math.pow(p31[1], m-4)) / (p32[2] - p31[1]*p31[1]);
				pOut *= p32[1] * p31[1] * p31[1] * p21[1] * p11[1];
				pOut += (p32[0] + p32[1]) * (1 - Math.pow(p32[2], m/2-1)) / (1 - p32[2]);
				pOut -= Math.pow(p32[2], m/2-2) * p32[1] * p21[1] * p11[1];
				pOut += Math.pow(p32[2], m/2-1) * p12[0] * p11[0];
			}
			else
			{	// m odd:
				pOut = -(Math.pow(p32[2], (m-3)/2) - Math.pow(p31[1], m-3)) / (p32[2] - p31[1]*p31[1]);
				pOut *= p32[1] *p31[1] * p21[1] * p11[1];
				pOut += (p32[0] + p32[1]) * (1 - Math.pow(p32[2], (m-3)/2)) / (1 - p32[2]);
				pOut += Math.pow(p32[2], (m-3)/2) * (p22[0] + p22[1] * p11[0]);
			}
		}
		
		if(cache.get(m) == null)
			cache.put(m, new HashMap<Integer, Double>());
		cache.get(m).put(n, pOut);
		return pOut;
	}
	
}
