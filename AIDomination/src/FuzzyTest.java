import net.sourceforge.jFuzzyLogic.FIS;


public class FuzzyTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long beforeLoad = System.nanoTime();
		FIS fis = FIS.load("fcl/fortification.fcl", true);
        
        if(fis == null) 
            throw new RuntimeException("Cannot load FCL file");


        long beforeEvaluation = System.nanoTime();
        System.out.println("Parsing: " + (beforeEvaluation - beforeLoad));
        for(int i = 0; i < 100; ++i) {
        	long start = System.nanoTime();
        	// Set inputs
            fis.setVariable("defeat", Math.random() * 100);
            fis.setVariable("player", Math.random() * 10);
            fis.setVariable("enemy", Math.random() * 10);
            fis.setVariable("this_continent_ownership", Math.random() * 10);
            fis.setVariable("that_continent_ownership", Math.random() * 10);
            fis.setVariable("this_continent_enemy_ownership", Math.random() * 10);
            fis.setVariable("that_continent_enemy_ownership", Math.random() * 10);
            fis.setVariable("that_continent", Math.random() * 10);
            
            // Evaluate
            fis.evaluate();
            fis.getVariable("fortification").getDefuzzifier().defuzzify();
            long end = System.nanoTime();
        	
            System.out.println("Evaluation: " + (end - start) + " ns (" + ((end - start) / 1000000.0) + " ms)");
        }
        
        // Show output variable's chart 
        fis.getVariable("defeat").chart(true);
        fis.getVariable("player").chart(true);
        fis.getVariable("enemy").chart(true);
        fis.getVariable("this_continent_ownership").chart(true);
        fis.getVariable("that_continent_ownership").chart(true);
        fis.getVariable("this_continent_enemy_ownership").chart(true);
        fis.getVariable("that_continent_enemy_ownership").chart(true);
        fis.getVariable("that_continent").chart(true);
        fis.getVariable("fortification").chart(true);
        
        
	}

}
