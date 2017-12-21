package prototype;

import prototype.Swing.MainFrame;

/**
 *
 * @author Pablo Alonso
 */
public class MainAI{
    
    private final Reader reader;
    private final Listener listener;
    private final MainFrame face;
    private final Thread listenerThread;

    public MainAI() {
        reader = new Reader(this);
        listener = new Listener(this);
        face = new MainFrame(this);
        listenerThread = new Thread(listener);
    }
    
    public void startListening(){
        listenerThread.start();
    }
    
    public void listened(String listened){
        face.setRecognizedText(listened);
        reader.readCommand(listened);
    }
    
    public void talkPlease(){
        face.setRecognizedText("Talk Please");
    }
    
    public void close(){
        face.dispose();
        listener.close();
        reader.saveNewCommnads();
        System.exit(0);
    }
}
