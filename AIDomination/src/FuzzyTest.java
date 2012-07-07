import net.sourceforge.jFuzzyLogic.FIS;


public class FuzzyTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long beforeLoad = System.nanoTime();
		FIS fis = FIS.load("fcl/attack.fcl", true);
        
        if(fis == null) 
            throw new RuntimeException("Cannot load FCL file");


        long beforeEvaluation = System.nanoTime();
        System.out.println("Parsing: " + (beforeEvaluation - beforeLoad));
        for(int i = 0; i < 100; ++i) {
        	long start = System.nanoTime();
        	// Set inputs
            fis.setVariable("victory", Math.random() * 100);
            fis.setVariable("enemy", Math.random() * 10);
            
            // Evaluate
            fis.evaluate();
            fis.getVariable("attack").getDefuzzifier().defuzzify();
            long end = System.nanoTime();
        	
            System.out.println("Evaluation: " + (end - start) + " ns (" + ((end - start) / 1000000.0) + " ms)");
        }
        
        // Show output variable's chart 
        fis.getVariable("victory").chart(true);
        fis.getVariable("enemy").chart(true);
        fis.getVariable("attack").chart(true);
        
        
        
	}

}
