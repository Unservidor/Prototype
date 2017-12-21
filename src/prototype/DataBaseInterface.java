/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototype;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Pablo Alonso
 */
public interface DataBaseInterface {
    
    /**
     *
     * @param key the name of the web we want the url, or
     * the name of program or folder we want the absolute path
     * @return an url or a path, depending on the matches of the key
     */
    public String getPathOf(String key);
    public String getSongPath(String songName);
    public String getBrowserPath();
    public Set<String> getProgramsNames();
    public Command getCommand(String commandOrder);
    public LinkedList<Command> getAllOtherCommands();
    public Map<String, String> getURLs();
    public Map<String, String> getPrograms();
    public Map<String, String> getFolders();
    public Map<String, String> getSongs();
    public Map<String, String> getFiles();
    public boolean createNewQuickCommand(String commandType, String commandOrder, String filePath, String programPath);
    public boolean createNewCommand(String commandType, String commandOrder, String filePath, String programPath);
    public void saveAllNewCommands();
    public void updateProgramPath(String programName, String newProgramPath);
    public String[] getAllRecognizedNames();
    public double[][] getTrainigData();
}
