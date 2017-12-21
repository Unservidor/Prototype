package prototype;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 *
 * @author Pablo Alonso
 */
public class Listener implements Runnable{
    
    private MainAI mainAI;
    private ConfigurationManager cm;
    private Recognizer recognizer;
    private Microphone microphone;
    private boolean listening;
    
    public Listener(MainAI mainAI){
        
        this.mainAI = mainAI;
        try {
            URL url = new File("inicio.config.xml").toURI().toURL();
            cm = new ConfigurationManager(url);
            recognizer = (Recognizer) cm.lookup("recognizer");
            microphone = (Microphone) cm.lookup("microphone");
        }catch (PropertyException | IOException | InstantiationException ex) {}
        
        listening = true;
    }
    
    @Override
    public void run() {
        try {
            recognizer.allocate();
        } catch (IllegalStateException | IOException ex) {}
        
        if (microphone.startRecording()) {
            while (listening) {
                Result result = recognizer.recognize();
                if (result != null) {
                    String resultText = result.getBestFinalResultNoFiller();
                    if(!resultText.isEmpty()){
                        mainAI.listened(resultText);
                    }else{
                        mainAI.talkPlease();
                    }
                }
            }
        } else {
            System.out.println("Cannot start microphone.");
            recognizer.deallocate();
        }
    }
    
    public void close(){
        listening = false;
    }
}
