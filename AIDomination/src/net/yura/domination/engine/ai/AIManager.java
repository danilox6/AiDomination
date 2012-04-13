package net.yura.domination.engine.ai;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Utilizzato per integrare facilmente nuove AI nel gioco
 * 
 * @author Danilo Iannelli <daniloiannelli6@gmail.com>
 *
 */
public class AIManager {
	private static HashMap<String, AI> AIs = new HashMap<String, AI>();
	
	/**
	 * Utilizzato per integrare le Ai nel gioco
	 * Per far ci&ograve, instanziare una AI ed aggiungerla alla mappa.
	 * &Egrave; preferibile usare i metodi addAI o addAIs dato che
	 * fanno qualche controllo sulla validità dell'AI 
	 * 
	 */
	public static void setup(){
		autodiscovery();
	}
	
	/**
	 * Esamina l'intero classpath alla ricerca di classi che implementano un'AI e le carica.
	 * 
	 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
	 */
	// TODO Implementa ricerca anche all'interno dei JAR
	private static void autodiscovery() {
		String classpath = System.getProperty("java.class.path");
		String[] paths = classpath.split(File.pathSeparator);
		for(String path : paths) {
			File dir = new File(path);
			if(dir.isDirectory()) {
				List<String> classes = autodiscoveryClasses(dir);
				String absDir = dir.getAbsolutePath();
				for(String classFile : classes) {
					String className = classFile.substring(absDir.length() + 1, classFile.lastIndexOf('.')).replace(File.separatorChar, '.');
					try {
						Class<?> cl = Class.forName(className);
						
						if(AI.class.isAssignableFrom(cl) && cl.isAnnotationPresent(Discoverable.class)) {
							System.out.println("Discovered ai " + cl);
							AI ai = (AI) cl.newInstance();
							addAI(ai);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private static List<String> autodiscoveryClasses(File path) {
		List<String> classes = new LinkedList<String>();
		FileFilter classFilter = new FileFilter() {
			
			@Override
			public boolean accept(File arg0) {
				return arg0.isDirectory() || (arg0.getName().toLowerCase().endsWith(".class") && arg0.getName().startsWith("AI"));
			}
		};
		
		if(!path.isDirectory())
			return Collections.<String>emptyList();
		
		for(File file : path.listFiles(classFilter)) {
			if(file.isDirectory())
				classes.addAll(autodiscoveryClasses(file));
			else
				classes.add(file.getAbsolutePath());
		}
		
		return classes;
	}
	
	/**
	 * Viene utilizzato quando l'engine risolve l'AI a partire dall'id
	 * @param id
	 * @return
	 */
	public static AI getAI(String id){
		return AIs.get(id);
	}
	
	/**
	 * Aggiunge una AI all'AIManager controllando che l'AI sia valida
	 * 
	 * @param ai
	 */
	public static void addAI(AI ai){
		try{
			ai.getName().toString(); //Serve solo per far scaturire un eventuale NullPointerExeption
			String id = ai.getId();
			if (AIs.containsKey(id))
				throw new IllegalArgumentException("Esiste già una AI con id: "+id);
			AIs.put(id, ai);
			ai.onInit();
		}catch (NullPointerException e) {
			throw new IllegalArgumentException("L'AI deve avere nome e id non null");
		}
	}
	
	/**
	 * Aggiunge più AI
	 * 
	 * @param ais
	 */
	public static void addAIs(AI... ais){
		for (AI ai: ais)
			addAI(ai);
	}
	
	/**
	 * Viene utilizzato dall'interfaccia grafica per visualizzare le AI disponibili
	 * @return
	 */
	public static Collection<AI> getAIs(){
		return AIs.values();
	}
	
}
