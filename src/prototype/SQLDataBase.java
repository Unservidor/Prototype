package prototype;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import javax.imageio.ImageIO;

/**
 *
 * @author Pablo Alonso
 */
public class SQLDataBase implements DataBaseInterface{
    
    private final Map <String, String> URLs;
    private final Map <String, String> programs;
    private final Map <String, String> folders;
    private final Map <String, String> songs;
    private final Map <String, String> files;
    private final Map <String, String> settings;
    private final LinkedList <Command> otherCommands;
    private final LinkedList <Command> quickCommandsToBeAdded;
    private final LinkedList <Command> otherCommandsToBeAdded;
    
    public SQLDataBase(){
        URLs = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        programs = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        folders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        songs = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        files = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        settings = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        otherCommands = new LinkedList<>();
        quickCommandsToBeAdded = new LinkedList<>();
        otherCommandsToBeAdded =  new LinkedList<>();
        
        loadSettings();
        
        try {
            loadCommands();
        } catch (ClassNotFoundException | SQLException ex){}
    }

    @Override
    public String getPathOf(String key) {
        String path;
        if (URLs.containsKey(key)) {
            path = URLs.get(key);
        } else if (programs.containsKey(key)) {
            path = programs.get(key);
        } else if (files.containsKey(key)) {
            path = files.get(key);
        } else {
            path = folders.get(key);
        }
        return path;
    }

    @Override
    public String getSongPath(String songName) {
        String path = songs.get(songName);
        return path;
    }

    @Override
    public String getBrowserPath() {
        return programs.get("Default Browser");
    }

    @Override
    public Set<String> getProgramsNames(){
        return programs.keySet();
    }
    
    @Override
    public Command getCommand(String commandOrder){
        for (Command otherCommand : otherCommands) {
            if(otherCommand.getCommandOrder().equalsIgnoreCase(commandOrder)) return otherCommand;
        }
        return null;
    }

    @Override
    public LinkedList<Command> getAllOtherCommands() {
        return otherCommands;
    }

    @Override
    public Map<String, String> getURLs() {
        return URLs;
    }

    @Override
    public Map<String, String> getPrograms() {
        return programs;
    }

    @Override
    public Map<String, String> getFolders() {
        return folders;
    }

    @Override
    public Map<String, String> getSongs() {
        return songs;
    }

    @Override
    public Map<String, String> getFiles() {
        return files;
    }

    @Override
    public boolean createNewQuickCommand(String commandType, String commandOrder, String filePath, String programPath) {
        boolean exist = checkIfCommandExistAlredy(commandOrder.trim());
        if (exist) return false;
        switch(commandType){
            case "Play Song":
                addWordsToGrammarFile("PlaySongs", commandOrder.trim());
                quickCommandsToBeAdded.add(new Command(commandOrder.trim(), "Songs", programPath.trim(), filePath.trim()));
                songs.put(commandOrder, filePath);
                break;
            case "Open/Close file":
                addWordsToGrammarFile("OpenOrder", commandOrder.trim());
                addWordsToGrammarFile("CloseOrder", commandOrder.trim());
                quickCommandsToBeAdded.add(new Command(commandOrder.trim(), "Files", programPath.trim(), filePath.trim()));
                files.put(commandOrder, filePath + " :: " + programPath);
                break;
            case "Open/Close Web":
                addWordsToGrammarFile("OpenOrder", commandOrder.trim());
                addWordsToGrammarFile("CloseOrder", commandOrder.trim());
                quickCommandsToBeAdded.add(new Command(commandOrder.trim(), "WebURL", programPath.trim(), filePath.trim()));
                URLs.put(commandOrder, filePath);
                break;
            case "Open Folder":
                addWordsToGrammarFile("OpenOrder", commandOrder.trim() + " folder");
                quickCommandsToBeAdded.add(new Command(commandOrder.trim(), "Folders", programPath.trim(), filePath.trim()));
                folders.put(commandOrder.trim(), filePath);
                break;
            case "Open/Close Program":
                addWordsToGrammarFile("OpenOrder", commandOrder.trim());
                addWordsToGrammarFile("CloseOrder", commandOrder.trim());
                quickCommandsToBeAdded.add(new Command(commandOrder.trim(), "Programs", programPath.trim(), filePath.trim()));
                programs.put(commandOrder, programPath);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean createNewCommand(String commandType, String commandOrder, String filePath, String programPath) {
        boolean exist = checkIfCommandExistAlredy(commandOrder.trim());
        if (exist) return false;
        addWordsToGrammarFile("OtherCommands", commandOrder.trim());
        otherCommandsToBeAdded.add(new Command (commandOrder, commandType, programPath, filePath));
        return true;
    }
    
    @Override
    public void saveAllNewCommands(){
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conect = DriverManager.getConnection("jdbc:sqlite:CommandDB.db");
            Statement state = conect.createStatement();
            for (Command quickCommand : quickCommandsToBeAdded) {
                String importantPath;
                if(quickCommand.getAction().equalsIgnoreCase("Programs")){
                    importantPath = quickCommand.getProgramPath();
                }else{
                    importantPath = quickCommand.getFilePath();
                }
                
                String values;
                if(quickCommand.getAction().equalsIgnoreCase("Files")){
                    values = " VALUES ('"+ quickCommand.getCommandOrder() + "', '" + quickCommand.getFilePath() + "', '" + quickCommand.getProgramPath() + "')";
                }else{
                    values = " VALUES ('"+ quickCommand.getCommandOrder() + "', '" + importantPath + "')";
                }
                state.executeUpdate("INSERT INTO " + quickCommand.getAction() + values);
            }
            
            for (Command otherCommand : otherCommandsToBeAdded) {
                //Esto se puede mejorar aqui estoy guardando, posiblemente, muchos "NONE" en la base de datos
                String values = "VALUES ('"+ otherCommand.getCommandOrder() + "', '" + otherCommand.getAction() + "', '" + otherCommand.getProgramPath() + "', '" + otherCommand.getFilePath() + "')";
                state.executeUpdate("INSERT INTO OtherCommands " + values);
            }
            
        } catch (ClassNotFoundException | SQLException ex) {}
    }
    
    @Override
    public void updateProgramPath(String programName, String newProgramPath){
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conect = DriverManager.getConnection("jdbc:sqlite:CommandDB.db");
            Statement state = conect.createStatement();
            String qwery = "REPLACE INTO Programs values('" + programName + "', '" + newProgramPath + "')";
            state.executeUpdate(qwery);
        } catch (ClassNotFoundException | SQLException ex){}
    }
    
    @Override
    public String[] getAllRecognizedNames(){
        LinkedList <String> nameList = new LinkedList();
        
        try (Scanner input = new Scanner(new BufferedReader(new FileReader("config.txt")))) {
            while (input.hasNextLine()) {
                String line = input.nextLine();
                if(line.contains("Person:")){
                    nameList.add(line.substring(8).trim());
                }
            }
        } catch (FileNotFoundException ex) {}
        
        String[] allNames = new String[nameList.size()];
        for (int i = 0; i < nameList.size(); i++) {
            allNames[i] = nameList.get(i);
        }
        
        return allNames;
    }
    
    @Override
    public double[][] getTrainigData(){
        File randomFacePicturesFolder = new File("Face Pictures Folder\\Face0");
        File picturesFolder = new File("Face Pictures Folder");
        File newRecognizedPicturesFolder = new File("Face Pictures Folder\\Face"+(picturesFolder.list().length-1));
        
        File[] randomFacePictures = randomFacePicturesFolder.listFiles();
        File[] newRecognizedPictures = newRecognizedPicturesFolder.listFiles();
        
         try {
            BufferedImage sizeAux = ImageIO.read(newRecognizedPictures[0]);
            double[][] pixelMatrix = new double [randomFacePictures.length * 2] [sizeAux.getHeight() * sizeAux.getWidth()];
            
            double[][] randomFacePixelMatrix = new double [randomFacePictures.length] [sizeAux.getHeight() * sizeAux.getWidth()];
            double[][] newReognizedFacePixelMatrix = new double [randomFacePictures.length] [sizeAux.getHeight() * sizeAux.getWidth()];
            
            for (int i = 0; i < newRecognizedPictures.length ; i++) {

                File newRecognizedPicture = newRecognizedPictures[i];
                BufferedImage in = ImageIO.read(newRecognizedPicture);
                BufferedImage newImage = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g1 = newImage.createGraphics();
                g1.drawImage(in, 0, 0, null);
                g1.dispose();

                File randomFacePicture = randomFacePictures[i];
                BufferedImage in2 = ImageIO.read(randomFacePicture);
                BufferedImage newImage2 = new BufferedImage(in2.getWidth(), in2.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = newImage2.createGraphics();
                g2.drawImage(in2, 0, 0, null);
                g2.dispose();
                
                //They are all the same size
                for (int j = 0; j < newImage.getHeight(); j++) {
                    for (int k = 0; k < newImage.getWidth(); k++) {
                        randomFacePixelMatrix[i][j*newImage.getHeight()+k] = newImage.getRGB(j, k);
                        newReognizedFacePixelMatrix[i][j*newImage2.getHeight()+k] = newImage2.getRGB(j, k);
                    }
                }
            }
            
            System.arraycopy(randomFacePixelMatrix, 0, pixelMatrix, 0, randomFacePixelMatrix.length);
            System.arraycopy(newReognizedFacePixelMatrix, 0, pixelMatrix, randomFacePixelMatrix.length, newReognizedFacePixelMatrix.length);
            
            return pixelMatrix;
        } catch (IOException ex) {}
        return null;
    }

    private void loadSettings() {
        File settingsFile = new File("Settings.data");
        if (!settingsFile.exists()){
            createSettings();
            firstTimeSave();
        }
        try (Scanner input = new Scanner(new BufferedReader(new FileReader(settingsFile)))) {
            while (input.hasNextLine()) {
                String line = input.nextLine();
                if (line.isEmpty()) {
                    continue;
                }
                String[] keyValue = line.split("::");
                keyValue[0] = keyValue[0].trim();
                keyValue[1] = keyValue[1].trim();
                settings.put(keyValue[0], keyValue[1]);
            }
        } catch (FileNotFoundException ex) {}//impossible
    }
    
    private void createSettings() {
        File settingsFile = new File("Settings.data");
        try {
            settingsFile.createNewFile();
            PrintStream line = new PrintStream (settingsFile);
            //WHAT TO PRINT HERE???? WHAT ABOUT THE SETTINGS???????????????????????????????????¿??????
            //line.println("Songs :: C:\\Users\\"+System.getProperty("user.name")+"\\Music\\");
            //line.println("Browser :: " + defaultBrowserGetter.getDefaultBrowserPath());
            line.close();
        } catch (IOException ex) {}
    }

    private void firstTimeSave() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conect = DriverManager.getConnection("jdbc:sqlite:CommandDB.db");
            Statement state = conect.createStatement();
            state.executeUpdate("INSERT INTO Programs " + "VALUES ('Default Browser', '" + defaultBrowserGetter.getDefaultBrowserPath() + "')");
        } catch (ClassNotFoundException | SQLException ex) {}
    }
    
    private void loadCommands() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        Connection conect = DriverManager.getConnection("jdbc:sqlite:CommandDB.db");
        Statement state = conect.createStatement();
        
        ResultSet rs = state.executeQuery("SELECT * FROM WebURL");
        while(rs.next()){
            URLs.put(rs.getString("Order"), rs.getString("URL"));
        }
        
        rs = state.executeQuery("SELECT * FROM Programs");
        while(rs.next()){
            programs.put(rs.getString("Order"), rs.getString("ProgramPath"));
        }
        
        rs = state.executeQuery("SELECT * FROM Folders");
        while(rs.next()){
            folders.put(rs.getString("Order"), rs.getString("FolderPath"));
        }
        
        rs = state.executeQuery("SELECT * FROM Songs");
        while(rs.next()){
            songs.put(rs.getString("Order"), rs.getString("SongPath"));
        }
        
        rs = state.executeQuery("SELECT * FROM Files");
        while(rs.next()){
            files.put(rs.getString("Order"), rs.getString("FilePath") + " :: " + rs.getString("ProgramPath"));
        }
        
        rs = state.executeQuery("SELECT * FROM OtherCommands");
        while(rs.next()){
            String programPath = (rs.getString("ProgramPath") == null) ? "NONE" : rs.getString("ProgramPath");
            String filePath = (rs.getString("FilePath") == null) ? "NONE" : rs.getString("FilePath");
            otherCommands.add(new Command(rs.getString("Order"), rs.getString("Type"), programPath, filePath));
        }
    }
    
    private boolean checkIfCommandExistAlredy(String newOrder) {
        for (Command otherCommand : otherCommands) {
            if(otherCommand.getCommandOrder().equalsIgnoreCase(newOrder)) return true;
        }
        return programs.containsKey(newOrder) || folders.containsKey(newOrder) || songs.containsKey(newOrder) || files.containsKey(newOrder);
    }
    
    private void addWordsToGrammarFile(String ruleName, String commandOrder) {
        LinkedList <String> lines = new LinkedList<>();
        try (Scanner input = new Scanner(new BufferedReader(new FileReader("inicio.gram")))) {
            while (input.hasNextLine()) {
                lines.add(input.nextLine());
            }
        } catch (FileNotFoundException ex) {}
        
        File gramFile = new File("inicio.gram");
        gramFile.delete();
        
        try {
            gramFile.createNewFile();
            PrintStream fileLine = new PrintStream (gramFile);
            for (String line : lines) {
                String fileRuleName = line;
                String[] lineWords = line.split("<|>");
                if(lineWords.length > 1) fileRuleName = lineWords[1];
                if(fileRuleName.equalsIgnoreCase(ruleName)){
                    line = line.substring(0, line.length()-2);
                    line += " | " + commandOrder + ");";
                }
                fileLine.println(line);
            }
            fileLine.close();
        } catch (IOException ex) {}
    }

}
