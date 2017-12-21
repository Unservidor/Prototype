package prototype;

import java.io.FileNotFoundException;
import prototype.Swing.CommandEditor;
import prototype.Swing.CommandDesk;
import prototype.Swing.DefaultPathChanger;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import prototype.Swing.NameSelector;

/**
 *
 * @author Pablo Alonso
 */
public class Reader {
    private final MainAI listener;
    private final Vision vision;
    private final Voice voice;
    private boolean listening;
    private boolean scanning;
    private CommandDesk commandDesk;
    private final Map <String, Integer> processesPid;
    private final DataBaseInterface dataBase;
    
    public Reader(MainAI listener) {
        this.listener = listener;
        setVision();
        vision = new Vision();
        voice = new Voice(true);
        listening = true;
        scanning = false;
        commandDesk = null;
        processesPid = new HashMap();
        dataBase = new SQLDataBase();
    }
    
    public void scanFinished(){
        voice.speak("scan finished");
        NameSelector nameSelector = new NameSelector();
        scanning = false;
        PerceptronMulticapa perceptron = new PerceptronMulticapa(22500, 1);
        Action.trainNet(perceptron, dataBase.getTrainigData(), 10);
        try {
            NeuralNet.saveWeights(perceptron, nameSelector.getNewName(), 100);
        } catch (FileNotFoundException ex) {}
        voice.speak("Neural net Saved");
    }
    
    public void readCommand(String command){
        String[] words = command.split(" ");
        
        int index = 0;
        if(words[index].equals("prototype")){
            command = command.substring(10);
            index++;
        }
        
        if(!listening && !command.equalsIgnoreCase("listen to me")) return;
        
        switch(words[index].toLowerCase()){
            case "open":
                if(words[words.length-1].equalsIgnoreCase("Folder")){
                    openCommands(command.substring(5, command.length()-7));
                }else{
                    openCommands(command.substring(5));
                }
                break;
            case "close":
                closeCommands(command.substring(6));
                break;
            case "play":
                if(! command.substring(4).trim().isEmpty()) playCommands(command.substring(5));
                break;
            default:
                otherCommands(command);
                break;
        }
    }
    
    public void saveNewCommnads(){
        dataBase.saveAllNewCommands();
    }

    private void openCommands(String command) {
        String path = dataBase.getPathOf(command);
        if(path == null) return;
        if(path.substring(0, 4).equals("http")){
            int pid = Action.openAFile(voice, dataBase.getBrowserPath(), path);
            processesPid.put(command, pid);
        }else if(!path.contains("::") && (path.substring(path.length()-3).equals("exe")) || command.equalsIgnoreCase("notepad")){
            int pid = Action.openAProgram(path);
            processesPid.put(command, pid);
        }else if(path.contains("::")){
            String[] fileAndProgramPath = path.split("::");
            int pid = Action.openAFile(voice, fileAndProgramPath[1].trim(), fileAndProgramPath[0].trim());
            processesPid.put(command, pid);
        }else{
            Action.openFolder(voice, path);
        }
    }

    private void closeCommands(String command) {
        if(command.equalsIgnoreCase("commands")){
            otherCommands("close commands");
            return;
        }
        
        Integer pid = processesPid.get(command);
        if(pid != null){
            processesPid.remove(command);
            Action.closeAProgram(pid);
        }
    }

    private void playCommands(String command) {
        String playerPath = dataBase.getPathOf("Music Player");
        String songPath = dataBase.getSongPath(command);
        int pid = Action.openAFile(voice, playerPath, songPath);
        if(!processesPid.containsKey("music player")){
            processesPid.put("music player", pid);
        }
    }

    private void otherCommands(String commandOrder) {
        Command command = dataBase.getCommand(commandOrder);
        if(command == null) return;
        
        Integer pid;
        switch (command.getAction().toLowerCase()) {
            case "take a picture":
                if(!scanning){
                    Action.takeAPicture(voice, vision);
                }else{
                    voice.speak("Alredy using the camera");
                }
                break;
            case "scan face":
                if(!scanning){
                    scanning = true;
                    Action.scan(voice, vision, this);
                }else{
                    voice.speak("Alredy scanning");
                }
                break;
            case "add a command":
                new CommandEditor(dataBase);
                voice.speak("Remember, the new commands will be added when this program is restarted");
                break;
            case "show commands":
                commandDesk = new CommandDesk(dataBase);
                break;
            case "close commands":
                if(commandDesk != null){
                    commandDesk.dispose();
                    commandDesk = null;
                }
                break;
            case "change default music player":
                new DefaultPathChanger(dataBase, "Music Player");
                voice.speak("Remember, the new commands will be added when this program is restarted");
                break;
            case "change default browser":
                new DefaultPathChanger(dataBase, "Default Browser");
                voice.speak("Remember, the new commands will be added when this program is restarted");
                break;
            case "play song":
                pid = Action.openAFile(voice, command.getProgramPath().trim(), command.getFilePath().trim());
                if (!processesPid.containsKey("music player")) {
                    processesPid.put("music player", pid);
                }
                break;
            case "open web":
                pid = Action.openAFile(voice, dataBase.getBrowserPath(), command.getFilePath());
                processesPid.put(command.getFilePath(), pid);
                break;
            case "close web":
                pid = processesPid.get(command.getFilePath());
                if (pid != null) {
                    processesPid.remove(command.getFilePath());
                    Action.closeAProgram(pid);
                }
                break;
            case "open program":
                pid = Action.openAProgram(command.getProgramPath());
                processesPid.put(command.getProgramPath(), pid);
                break;
            case "close program":
                pid = processesPid.get(command.getProgramPath());
                if (pid != null) {
                    processesPid.remove(command.getProgramPath());
                    Action.closeAProgram(pid);
                }
                break;
            case "open file":
                System.out.println("File Path: " + command.getFilePath());
                System.out.println("Program Path: " + command.getProgramPath());
                pid = Action.openAFile(voice, command.getProgramPath(), command.getFilePath());
                processesPid.put(command.getFilePath(), pid);
                break;
            case "close file":
                pid = processesPid.get(command.getFilePath());
                if (pid != null) {
                    processesPid.remove(command.getFilePath());
                    Action.closeAProgram(pid);
                }
                break;
            case "open folder":
                Action.openFolder(voice, command.getFilePath());
                break;
            case "stop listening":
                voice.speak("I'm out for a while");
                listening = false;
                break;
            case "start listening":
                voice.speak("I'm listening");
                listening = true;
                break;
            case "stop talking":
                voice.speak("i'm quiet");
                voice.mute();
                break;
            case "start talking":
                voice.unmute();
                voice.speak("Hello Again");
                break;
            case "exit program":
                voice.speak("Good bye");
                voice.deallocateVoice();
                listener.close();
                break;
            case "who am i":
                if(!scanning){
                    Action.recognize(voice, vision, dataBase.getAllRecognizedNames());
                }else{
                    voice.speak("Alredy using the camera");
                }
                break;
            default:
                break;
        }
    }

    private void setVision() {
        System.setProperty("java.library.path", "Open CV x64");
        try {
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {}
    }
    
}
