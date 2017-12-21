package prototype.Swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import prototype.DataBaseInterface;

/**
 *
 * @author Pablo Alonso
 */
public class CommandEditor extends JDialog {
    
    private final DataBaseInterface dataBase;
    private final JComboBox commandType;
    private final JComboBox programSelector;
    private final JCheckBox justOpenOption;
    private final JCheckBox justCloseOption;
    private final JCheckBox prefixOption;
    private final JTextField newCommand;
    private final JTextField filePath;
    private final JTextField programPath;
    private final JPanel centerFilePanel;
    private final JPanel centerProgramPanel;
    private final JLabel warning;
    private boolean isJustOpenOption, useOpenClosePrefix;
    
    public CommandEditor(DataBaseInterface dataBase){
        this.dataBase = dataBase;
        commandType = new JComboBox();
        programSelector = new JComboBox();
        justOpenOption = new JCheckBox("Just Open");
        justCloseOption = new JCheckBox("Just Close");
        prefixOption = new JCheckBox();
        newCommand = new JTextField();
        filePath = new JTextField();
        programPath = new JTextField();
        centerFilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        centerProgramPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        warning = new JLabel(" ");
        isJustOpenOption = true;
        useOpenClosePrefix = false;
        
        createUI();
        setTitle("Give me my commands");
        setIconImage((new ImageIcon("Decepticon Violet Logo.jpg")).getImage());
        pack();
        setLocationRelativeTo(null);
        setModal(true);
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void createUI() {
        JLabel commandTypeLabel = new JLabel("Command Type");
        JLabel programSelectorLabel = new JLabel("Use Program");
        commandType.addItem("NONE");
        commandType.addItem("Play Song");
        commandType.addItem("Open/Close Web");
        commandType.addItem("Open/Close Program");
        commandType.addItem("Open/Close file");
        commandType.addItem("Open Folder");
        commandType.setSelectedIndex(0);
        commandType.addActionListener(commandTypeActionListener());
        addProgramListToSelectProgramComboBox();
        
        JLabel newCommandLabel = new JLabel("The New Command: ");
        newCommand.setColumns(40);
        justOpenOption.setSelected(true);
        justOpenOption.setVisible(false);
        justOpenOption.addActionListener(openCloseOptionsActionListener());
        justCloseOption.setVisible(false);
        justCloseOption.addActionListener(openCloseOptionsActionListener());
        prefixOption.setVisible(false);
        prefixOption.addActionListener(prefixOptionActionListener());
        
        filePath.setColumns(40);
        JLabel fileLabel = new JLabel("File Path");
        JButton fileBrowse = new JButton("Browse");
        fileBrowse.addActionListener(BrowseActionListener(true));
        
        programPath.setColumns(40);
        JLabel programLabel = new JLabel("Program Path");
        JButton programBrowse = new JButton("Browse");
        programBrowse.addActionListener(BrowseActionListener(false));
        
        JButton accept = new JButton("Accept");
        accept.addActionListener(acceptButtonActionListener());
        
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(cancelButtonActionListener());
        
        //Layout
        JPanel northPanel = new JPanel();
        northPanel.add(commandTypeLabel);
        northPanel.add(commandType);
        northPanel.add(programSelectorLabel);
        northPanel.add(programSelector);
        
        JPanel openCloseOptions = new JPanel (new FlowLayout(FlowLayout.LEFT, 20, 10));
        openCloseOptions.add(newCommandLabel);
        openCloseOptions.add(justOpenOption);
        openCloseOptions.add(justCloseOption);
        JPanel centerCommandTextFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        centerCommandTextFieldPanel.add(newCommand);
        centerCommandTextFieldPanel.add(prefixOption);
        centerCommandTextFieldPanel.setPreferredSize(new Dimension(800,46));
        
        centerFilePanel.add(filePath);
        centerFilePanel.add(fileLabel);
        centerFilePanel.add(fileBrowse);
        
        centerProgramPanel.add(programPath);
        centerProgramPanel.add(programLabel);
        centerProgramPanel.add(programBrowse);
        
        warning.setForeground(Color.red);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(openCloseOptions);
        centerPanel.add(centerCommandTextFieldPanel);
        centerPanel.add(centerFilePanel);
        centerPanel.add(centerProgramPanel);
        centerPanel.add(warning);
        
        JPanel southPanel = new JPanel();
        southPanel.add(accept);
        southPanel.add(cancel);
        
        this.setLayout(new BorderLayout());
        this.add(northPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);
    }

    private ActionListener commandTypeActionListener() {
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                String item = (String)(commandType.getSelectedItem());
                prefixOption.setVisible(true);
                justOpenOption.setVisible(true);
                justCloseOption.setVisible(true);
                switch(item){
                    case"NONE":
                        centerFilePanel.setVisible(true);
                        centerProgramPanel.setVisible(true);
                        prefixOption.setVisible(false);
                        justOpenOption.setVisible(false);
                        justCloseOption.setVisible(false);
                        break;
                    case"Play Song":
                        justOpenOption.setVisible(false);
                        justCloseOption.setVisible(false);
                        centerFilePanel.setVisible(true);
                        centerProgramPanel.setVisible(true);
                        boolean exist = false;
                        for (int i = 0; i < programSelector.getItemCount(); i++) {
                            if(((String)(programSelector.getItemAt(i))).equalsIgnoreCase("Music Player")){
                                exist = true;
                                break;
                            }
                        }
                        if(exist){
                            programSelector.setSelectedItem("Music Player");
                            programPath.setText(dataBase.getPathOf("Music Player"));
                        }
                        prefixOption.setText("Use the \"Play\" prefix for the command");
                        break;
                    case"Open/Close Web":
                        centerFilePanel.setVisible(true);
                        centerProgramPanel.setVisible(true);
                        programSelector.setSelectedItem("Default Browser");
                        programPath.setText(dataBase.getBrowserPath());
                        prefixOption.setText("Use the \"Open Close\" prefixes for the command");
                        break;
                    case"Open/Close Program":
                        centerFilePanel.setVisible(false);
                        centerProgramPanel.setVisible(true);
                        programSelector.setSelectedItem("NONE");
                        prefixOption.setText("Use the \"Open Close\" prefixes for the command");
                        break;
                    case"Open/Close file":
                        centerFilePanel.setVisible(true);
                        centerProgramPanel.setVisible(true);
                        prefixOption.setText("Use the \"Open Close\" prefixes for the command");
                        break;
                    case"Open Folder":
                        justOpenOption.setVisible(false);
                        justCloseOption.setVisible(false);
                        centerFilePanel.setVisible(true);
                        centerProgramPanel.setVisible(false);
                        programSelector.setSelectedItem("NONE");
                        prefixOption.setText("Use the \"Open ... Folder\" prefix for the command");
                        break;
                    default:
                        break;
                }
           }
        };
    }

    private void addProgramListToSelectProgramComboBox() {
        programSelector.addItem("NONE");
        for (String programName : dataBase.getProgramsNames()) {
            programSelector.addItem(programName);
        }
        programSelector.addActionListener(programSelectorActionListener());
    }

    private ActionListener programSelectorActionListener() {
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                String item = (String)(programSelector.getSelectedItem());
                if(item.equals("Default Browser")){
                    programPath.setText(dataBase.getBrowserPath());
                }else if(!item.equals("NONE")){
                    programPath.setText(dataBase.getPathOf(item));
                }
           }
        };
    }

    private ActionListener BrowseActionListener(final boolean isAFileChooser) {
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                if(isAFileChooser){
                    fileChooser.setDialogTitle("Select The File Or Folder");
                }else{
                    fileChooser.setDialogTitle("Select The Program");
                }
                String path = "";
                JDialog dialog = new JDialog();
                dialog.setModal(true);
                int returnVal = fileChooser.showOpenDialog(dialog);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    path = fileChooser.getSelectedFile().getAbsolutePath();
                    dialog.dispose();
                }
                if (returnVal == JFileChooser.CANCEL_OPTION) {
                    dialog.dispose();
                }
                if(!path.equals("")){
                    if(isAFileChooser){
                        filePath.setText(path);
                    }else{
                        programPath.setText(path);
                    }
                }
            }
        };
    }
    
    private ActionListener prefixOptionActionListener() {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                useOpenClosePrefix = !useOpenClosePrefix;
                if(! prefixOption.getText().contains("Open Close"))return;
                if(useOpenClosePrefix){
                    justOpenOption.setVisible(false);
                    justCloseOption.setVisible(false);
                }else{
                    justOpenOption.setVisible(true);
                    justCloseOption.setVisible(true);
                }
            }
        };
    }
    
    private ActionListener openCloseOptionsActionListener() {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(isJustOpenOption){
                    justOpenOption.setSelected(false);
                    justCloseOption.setSelected(true);
                }else{
                    justCloseOption.setSelected(false);
                    justOpenOption.setSelected(true);
                }
                isJustOpenOption = !isJustOpenOption;
            }
        };
    }

    private ActionListener acceptButtonActionListener() {
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if(newCommand.getText().trim().equals("") || 
                        (filePath.isShowing() && filePath.getText().trim().equals("")) || 
                        (programPath.isShowing() && programPath.getText().trim().equals(""))){
                    
                    warning.setText("Please Fill All The Blanks");
                    return;
                }
                
                File file = new File(filePath.getText());
                boolean created;
                switch((String)(commandType.getSelectedItem())){
                    case"NONE":
                        if(!checkIfFileExist(file, "File"))return;
                        if(!checkIfFileisFile(file, "File"))return;
                        break;
                    case"Play Song":
                        if(!checkIfFileExist(file, "File"))return;
                        if(!checkIfFileisFile(file, "File"))return;
                        if(useOpenClosePrefix){
                            created = createNewCommand("Play Song", newCommand.getText().trim(), filePath.getText().trim(), "NONE", true);
                        }else{
                            created = createNewCommand("play song", newCommand.getText().trim(), filePath.getText().trim(), programPath.getText().trim(), false);
                        }
                        if(!created) return;
                        break;
                    case"Open/Close file":
                        if(!checkIfFileExist(file, "File"))return;
                        if(!checkIfFileisFile(file, "File"))return;
                        if(useOpenClosePrefix){
                            created = createNewCommand("Open/Close file", newCommand.getText().trim(), filePath.getText().trim(), programPath.getText().trim(), true);
                        }else if(isJustOpenOption){
                            created = createNewCommand("open file", newCommand.getText().trim(), filePath.getText().trim(), programPath.getText().trim(), false);
                        }else{
                            created = createNewCommand("close file", newCommand.getText().trim(), filePath.getText().trim(), programPath.getText().trim(), false);
                        }
                        if(!created) return;
                        break;
                    case"Open/Close Web":
                        if(filePath.getText().length() < 4 || !filePath.getText().substring(0, 4).equalsIgnoreCase("http")){
                            warning.setText("The File Path is not an URL");
                            return;
                        }
                        if(useOpenClosePrefix){
                            created = createNewCommand("Open/Close Web", newCommand.getText().trim(), filePath.getText().trim(), "NONE", true);
                        }else if(isJustOpenOption){
                            created = createNewCommand("open web", newCommand.getText().trim(), filePath.getText().trim(), programPath.getText().trim(), false);
                        }else{
                            created = createNewCommand("close web", newCommand.getText().trim(), filePath.getText().trim(), programPath.getText().trim(), false);
                        }
                        if(!created) return;
                        break;
                    case"Open Folder":
                        if(!checkIfFileExist(file, "File"))return;
                        if(!checkIfFileisDirectory(file))return;
                        if(useOpenClosePrefix){
                            created = createNewCommand("Open Folder", newCommand.getText().trim(), filePath.getText().trim(), "NONE", true);
                        }else{
                            created = createNewCommand("Open Folder", newCommand.getText().trim(), filePath.getText().trim(), "NONE", false);
                        }
                        if(!created) return;
                        break;
                    case"Open/Close Program":
                        file = new File(programPath.getText());
                        if(!checkIfFileExist(file, "Program"))return;
                        if(!checkIfFileisFile(file, "Program"))return;
                        if(!programPath.getText().substring(programPath.getText().length()-3).equalsIgnoreCase("exe")){
                            warning.setText("The Program Path Is Not The Path Of A Program");
                            return;
                        }
                        if(useOpenClosePrefix){
                            created = createNewCommand("Open/Close Program", newCommand.getText().trim(), "NONE", programPath.getText().trim(), true);
                        }else if(isJustOpenOption){
                            created = createNewCommand("open program", newCommand.getText().trim(), "NONE", programPath.getText().trim(), false);
                        }else{
                            created = createNewCommand("close program", newCommand.getText().trim(), "NONE", programPath.getText().trim(), false);
                        }
                        if(!created) return;
                        break;
                    default:
                        break;
                }
                
                CommandEditor.this.dispose();
            }

            private boolean checkIfFileExist(File file, String type) {
                if (!file.exists()) {
                    warning.setText("The "+ type + " Path Is Invalid");
                    return false;
                }
                return true;
            }

            private boolean checkIfFileisFile(File file, String type) {
                if (!file.isFile()) {
                    warning.setText("The " + type + " Path Is Not The Path Of A " + type);
                    return false;
                }
                return true;
            }
            
            private boolean checkIfFileisDirectory(File file) {
                if (!file.isDirectory()) {
                    warning.setText("The File Path Is Not The Path Of A Directory");
                    return false;
                }
                return true;
            }

            private boolean createNewCommand(String newCommandType, String commandOrder, String commandFilePath, String commandProgramPath, boolean isQuickCommand) {
                boolean isANewCommand;
                if(isQuickCommand){
                    isANewCommand = dataBase.createNewQuickCommand(newCommandType, commandOrder, commandFilePath, commandProgramPath);
                }else{
                    isANewCommand = dataBase.createNewCommand(newCommandType, commandOrder, commandFilePath, commandProgramPath);
                }
                if (!isANewCommand) {
                    warning.setText("you are trying to add an already existing command");
                    return false;
                }
                return true;
            }
        };
    }

    private ActionListener cancelButtonActionListener() {
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                CommandEditor.this.dispose();
            }
        };
    }
    
}
