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
	private static HashMap<String, AIClass> AIclasses = new HashMap<String, AIClass>();
	
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
	@SuppressWarnings("unchecked")
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
							AIClass clazz = new AIClass((Class<? extends AI>) cl); 
							AIclasses.put(clazz.getId(), clazz);

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
	public static Class<? extends AI> getAIClass(String id){
		return AIclasses.get(id).getAIclass();
	}
	
	
	/**
	 * Viene utilizzato dall'interfaccia grafica per visualizzare le AI disponibili
	 * @return
	 */
	public static Collection<AIClass> getAIs(){
		return AIclasses.values();
	}
	
	
	public static String getAIClassId(Class<? extends AI> clazz){
		if(clazz.getSimpleName().equals("AIHuman"))
			return "human";
		return "ai "+clazz.getSimpleName();
	}
	
	
	public static class AIClass{
		
		private String name, id;
		private Class<? extends AI> AIclass;
		
		public AIClass(Class<? extends AI> clazz) {
			this.AIclass = clazz;
			if(clazz.getSimpleName().equals("AIHuman")){
				id = "human";
				name = "Umano";
			}else{
				id = "ai "+clazz.getSimpleName().toLowerCase().substring(2);
				name = clazz.getSimpleName().substring(2);
			}
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id;
		}

		public Class<? extends AI> getAIclass() {
			return AIclass;
		}
	
		
		@Override
		public String toString() {
			return name;
		}
		
		
	}
}
