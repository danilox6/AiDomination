import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import org.jfree.chart.ChartPanel;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

/**
 * Piccola UI per il debug dei file FCL.
 * 
 * @author Michele Piccirillo
 */
public class FuzzyTester extends JFrame {
	private static final long serialVersionUID = 1L;
	
	FuzzyTester(String file, String functionBlock) {
		super("Fuzzy Tester - " + file + " [" + functionBlock + "]");
		
		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		add(splitPane);
		
		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.PAGE_AXIS));
		final JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.PAGE_AXIS));
		final JScrollPane rightScroll = new JScrollPane(right);
		
		splitPane.add(left);
		splitPane.add(rightScroll);
		
		final JPanel input = new JPanel();
		input.setLayout(new BoxLayout(input, BoxLayout.PAGE_AXIS));
		input.setBorder(BorderFactory.createTitledBorder("Input"));
		right.add(input, BorderLayout.NORTH);
		
		final JPanel output = new JPanel();
		output.setLayout(new BoxLayout(output, BoxLayout.PAGE_AXIS));
		output.setBorder(BorderFactory.createTitledBorder("Output"));
		right.add(output, BorderLayout.SOUTH);
		
		final FIS fis = FIS.load(file, true);

        if(fis == null) 
            throw new RuntimeException("Cannot load FCL file");
        
		final FunctionBlock block = fis.getFunctionBlock(functionBlock);
        
        
        HashMap<String, Variable> variables = block.getVariables();
        final Map<String, JTextField> fields = new HashMap<String, JTextField>();
        for(Map.Entry<String, Variable> var : variables.entrySet()) {
        	if(var.getValue().isOutputVarable())
        		continue;
        	
        	left.add(new JLabel(var.getKey()));
        	JTextField textField = new JTextField("0.0", 20);
        	textField.setMaximumSize(textField.getPreferredSize());
        	left.add(textField);
        	left.add(Box.createVerticalStrut(5));
        	fields.put(var.getKey(), textField);
        }
        
        left.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton calculate = new JButton("Calcola");
        left.add(calculate);
        calculate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for(Map.Entry<String, JTextField> entry : fields.entrySet()) {
					block.setVariable(entry.getKey(), Double.parseDouble(entry.getValue().getText()));
				}
				fis.evaluate();
				input.removeAll();
				output.removeAll();
				for(Variable var : block.getVariables().values()) {
					ChartPanel chart = new ChartPanel(var.chart(false));
					chart.setMaximumSize(new Dimension(500,200));
					(var.isOutputVarable() ? output : input).add(chart);
				}
				output.validate();
				input.validate();
				right.validate();
				rightScroll.validate();
			}
        	
        });
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new FuzzyTester("fcl/fortification.fcl", "country").setVisible(true);
	}

}
