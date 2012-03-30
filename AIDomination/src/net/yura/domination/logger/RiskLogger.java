package net.yura.domination.logger;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import net.yura.domination.engine.Risk;

public class RiskLogger {
	public static String LOGGER = "RiskLogger"; 

	public static void setup()  {
		try{
			Logger logger = Logger.getLogger(LOGGER);
			FileHandler fh = new FileHandler("RiskLog.txt");
			fh.setFormatter(new LoggerFormatter());
			logger.addHandler(fh);
			logger.addHandler(new SysOutConsoleHandler()); //Commentare questa linea se non si vuole scrivere su System.out
			
			Risk.setLogOwnedCountries(true);
			Risk.setLogOwnedCards(true);
			Risk.setLogTradeCards(true);
			Risk.setLogPlaceArmies(true);
			Risk.setLogAttacks(true);
			Risk.setLogReceivedAttacks(true);
			Risk.setLogBattleDetails(true);
			Risk.setLogBattleWon(true);
			Risk.setLogTacMove(true);
			
			
			
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private static class SysOutConsoleHandler extends ConsoleHandler{
		public SysOutConsoleHandler() {
			setOutputStream(System.out);
			setFormatter(new LoggerFormatter());
		}
	}
	
	private static class LoggerFormatter extends Formatter{

		@Override
		public String format(LogRecord record) {
			if(record.getLevel() == Level.INFO){
				return record.getMessage();
			}
			return record.toString();
		}
		
	}
}
