Ho cercato di togliere la maggior parte delle cose inutili ai nostri fini.
Ho lasciato solo l'interfaccia "FlashGui".
Non ci sono più i problemi del linking delle risorse esterne.

Il tutto si avvia dalla classe net.yura.domination.ui.flashgui.MainMenu
presente nella source folder src_swing.

Ho cercato di sistemare alla meglio le API per creare una nuova AI.

Ho creato la classe astratta net.yura.domination.engine.ai.AI.
Ora è la superclasse di AICrap, AIEasy, AIHard ecc...
Ai metodi di AI.java ho aggiunto i commenti javadoc così da permettere di visualizzare
più facilmente quali sono le stringhe che i metodi dell'AI devono contenere.

Per permettere di integrare facilmente le AI, ho fatto un bel pò di modifiche
all'engine del gioco.
Prima ad ogni Player era assegnato un numero intero che indicava la sua AI (crap, easy, hard o human).
Di solito veniva usato uno swith o degli if annidati per determinare l'ia da utilizzare.
Inoltre per accedere al ResourceBundle per le traduzioni e per permettere al parser di capire l'azione
da compiere veniva usato una stringa univoca che veniva risolta in base all'intero dell'ai. 
  
Ora invece, ad ogni Player è associato un oggetto AI. Oltre agli attributi game e player, è dotato
di una stringa id e una stringa name:
La stringa id è una stringa univoca che serve per far identificare l'ai dal parser.
Per essere processata dal parser DEVE iniziare con "ai " e non deve contenere ulteriori spazi (es. "ai crap" è un id valido).
Viene attribito tramite il metodo setId(String id);

Il nome invece gli viene attribuito per fare in modo che non venga cercato nel ResourceBoundle.
Quando serve al gioco per essere mostrato a video, viene recuperato con un getName()...
Viene attribito tramite il metodo setName(String name);

Per supportare la possibilità di usare diverse Ai a seconda della modalità di gioco,
(come fa AIHard) ho aggiunto ad AI.java i metodi setCapitalAI(AI ai) e setMissionAI(AI ai)

i metodi setName(), setID(), setCapitalAI() e setMissionAI() restituiscono this per far utilizzare i metodi a cascata

Ho aggiunto la classe net.yura.domination.engine.ai.AIManager.
Questa serve per integrare le AI nel gioco.
nel metodo .setup() vanno instanziate le AI; queste vengono aggiunte ad una HashMap usando come chiave l'id
È preferibile aggiungere le AI tramite i metodi addAI(AI ai) o addAIs(AI... ais) dato che effettuano alcuni controlli
sulla validità delle AI.

il metodo getAI(String id) viene chiamato dal parser quanto ha bisogno di risolvere l'ai
il metodo gerAIs() viene utilizzato dall'interfaccia per ottenere la lista di tutte le AI al fine
di visualizzarle in un ComboBox (ho tolto i RadioButton).

Ad ogni modo ho spiegato tutto anche nel codice e faccio lanciare qualche eccezione nel caso di errori.

Mi sembra di aver detto tutto; scusate gli eventuali errori e la poca chiarezza: non mi va di rileggere il tutto...

Danilo


