package it.unisannio.ai.search;

import net.yura.domination.engine.core.Continent;

public class SearchUtility {
	
	/**
	 * Restituisce il numero di armate che saranno disponibili nel turno successivo
	 * @param scenario
	 * @return
	 */
	public static int getNextTurnArmies(GameScenario scenario){
		int armies = 0;
		armies += scenario.possessions.size()/3;
		Continent[] continents = scenario.getGame().getContinents();
		for(Continent continent: continents){
			if(scenario.possessions.containsAll(continent.getTerritoriesContained()))
				armies += continent.getArmyValue();
		}
		return armies;
	}	
	
	/**
	 * Restituisce il numero di armate che perse dall'attaccante
	 * @param attackerArmies
	 * @param defenderArmies
	 * @return
	 */
	public static int getAttackerLostArmies(int attackerArmies, int defenderArmies){
		//TODO
		return 0;
	}
	
	/**
	 * Restituisce il numero di armate che perse dal difensore
	 * @param attackerArmies
	 * @param defenderArmies
	 * @return
	 */
	public static int getDefenderLostArmies(int attackerArmies, int defenderArmies){
		//TODO
		return 0;
	}
	
	
	/**
	 * Restituisce la percentuale di vittorie dell'attaccante basata sul numero di armate
	 * 
	 * @param attackerArmies
	 * @param defenderArmies
	 * @return
	 */
	public static float victoryProbability(int attackerArmies, int defenderArmies){
		if ((attackerArmies <= 5) || (defenderArmies <= 3)) return (float) boundaryProb(attackerArmies, defenderArmies);
		
		// calculate probability:
		float f0 = 2890/7776;
		float f1 = 2611/7776;
		float f2 = 2275/7776;
		float pOut = 0;
		for (int i = attackerArmies; i > 4; i--)
		{
			pOut += freeSpaceProb(attackerArmies, defenderArmies, i, 3) * f0 * boundaryProb(i, 1);
			pOut += (freeSpaceProb(attackerArmies, defenderArmies, i + 1, 3) * f1 + freeSpaceProb(attackerArmies, defenderArmies, i, 4) * f0) * boundaryProb(i, 2);
		}
		pOut += freeSpaceProb(attackerArmies, defenderArmies, 5, 3) * f1 * boundaryProb(4, 2);
		for (int j = defenderArmies; j > 2; j--)
		{
			pOut += freeSpaceProb(attackerArmies, defenderArmies, 5, j) * f2 * boundaryProb(3, j);
			pOut += (freeSpaceProb(attackerArmies, defenderArmies, 6, j) * f2 + freeSpaceProb(attackerArmies, defenderArmies, 5, j + 1) * f1) * boundaryProb(4, j);
		}
		
		return pOut;
	} 
	
	private static float freeSpaceProb(int attackerArmies, int defenderArmies, int m, int n) {
		//errors:
		if ((attackerArmies < 4) || (m < 2) || (defenderArmies < 2) || (n < 0)) return -1;
		if ((Math.floor(attackerArmies) != attackerArmies) || (Math.floor(defenderArmies) != defenderArmies) || (Math.floor(m) != m) || (Math.floor(n) != n)) return -1;
		double s = Math.floor((attackerArmies + defenderArmies - m - n)/2);
		if (s < 0) return -1;

		//zero probability:
		if ((attackerArmies + defenderArmies - m - n) != 2 * s) return 0;	// Can't be reached by shaking 3 vs. 2 dice exclusively.
		if (m + n < 4) return 0;				// Can't be reached by shaking 3 vs. 2 dice exclusively.
		
		float f0 = 2890/7776;
		float f1 = 2611/7776;
		float f2 = 2275/7776;
		float L = ((m - n) - (attackerArmies - defenderArmies))/2;
		float pOut = 0;
		
		for (int smallL = 0; smallL <= (s - Math.abs(L))/2; smallL++)
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
		nOut /= (factorial((int) D) * factorial((int) E));
		
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

	//TODO Cache
//	private static HashMap<Integer, Integer> boundaryProb = new HashMap<Integer, Integer>();
	
	private static double boundaryProb(int m, int n) {
//		// Look for an array element that stores the solution for this particular (m, n):
//		if ( boundaryProb == null)
//			boundaryProb = new HashMap<Integer, Integer>();
//		else
//			if (boundaryProb.get(m) != null)
//				return g_arrBoundaryProb[m][n];
		
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
			pOut = p32[0] * boundaryProb(m, n-2);
			pOut += p32[1] * boundaryProb(m-1, n-1);
			pOut += p32[2] * boundaryProb(m-2, n);
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

//		g_arrBoundaryProb[m][n] = pOut;
		return pOut;
	}
	
	
	public static void main(String[] args) {
		System.out.println(victoryProbability(5, 2)); 
	}
}
