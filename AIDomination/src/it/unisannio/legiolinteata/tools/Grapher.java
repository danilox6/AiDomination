package it.unisannio.legiolinteata.tools;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class Grapher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for(String file : args) {
			System.out.println("Loading " + file);
			FIS fis = FIS.load(file);
			for(FunctionBlock block : fis) {
				System.out.println("\tGenerating graphs for block " + block.getName());
				HashMap<String, Variable> vars = block.getVariables();
				for(Entry<String, Variable> entry : vars.entrySet()) {
					Variable var = entry.getValue();
					System.out.println("\t\tPlotting variable " + var.getName());
					JFreeChart chart = var.chart(false);
					
					try {
						FileOutputStream fos = new FileOutputStream(block.getName() + "-" + var.getName() + ".png");
						ChartUtilities.writeChartAsPNG(fos,chart,800,400);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

}
