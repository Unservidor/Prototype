package prototype;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Pablo Alonso
 */
public class Action {
    
    private final static Map <Integer, Process> openProcesses = new HashMap();
    private static int pid = 0;
    
    public static void openFolder(Voice voice, String path){
        voice.speak("on it");
        FolderOpener.openFolder(path);
    }

    public static int openAProgram(String programPath){
        Runtime runTime = Runtime.getRuntime();
        Process process;
        try {
            process = runTime.exec(programPath);
            openProcesses.put(pid, process);
            pid++;
        } catch (IOException ex) {}
        return (pid-1);
    }
    
    public static int openAFile(Voice voice, String programPath, String filePath) {
        try {
            Runtime runTime = Runtime.getRuntime();
            String[] programAndFile = new String[2];
            programAndFile[0] = programPath;
            programAndFile[1] = filePath;
            Process process = runTime.exec(programAndFile);
            openProcesses.put(pid, process);
            pid++;
        } catch (IOException ex) {
            voice.speak("This file no longer exist, or the program path is wrong");
        }
        return (pid-1);
    }
    
    public static void closeAProgram(int programPid){
        openProcesses.get(programPid).destroy();
        openProcesses.remove(programPid);
    }
    
    public static void takeAPicture(Voice voice, Vision vision) {
        voice.speak("Say cheese");
        vision.takeAPicture(false);
    }
    
    public static void scan(Voice voice, Vision vision, Reader reader){
        voice.speak("scanning...");
        vision.scan(200);
        Thread scanThread = new Thread(vision);
        scanThread.start();
        threadListener(scanThread, reader);
    }
    
    public static void recognize(Voice voice, Vision vision, String[] allNames){
        String result = vision.recognize(allNames);
        if(result.isEmpty()){
            voice.speak("Impossible to recognize someone");
        }else{
            voice.speak("Hello " + result);
        }
    }
    
    public static void trainNet(PerceptronMulticapa perceptron, double[][] trainSet, int numberOfCycles){
        double[] labels = new double[22500];
        for (int i = 0; i < labels.length; i++) {
            if(i%2 == 0){
                labels[i] = 1;
            }else{
                labels[i] = 0;
            }
        }
        NeuralNet.trainNetwork(perceptron, numberOfCycles, trainSet, labels);
    }
    
    private static void threadListener(final Thread thread, final Reader reader){
        new Thread(){
            @Override
            public void run(){
                try {
                    thread.join();
                } catch (InterruptedException ex) {}
                reader.scanFinished();
            }
        }.start();        
    }
}
