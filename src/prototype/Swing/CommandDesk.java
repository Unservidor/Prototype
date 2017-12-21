package prototype.Swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import prototype.Command;
import prototype.DataBaseInterface;

/**
 *
 * @author Pablo Alonso
 */
public class CommandDesk extends JDialog{
    
    private final DataBaseInterface dataBase;
    private final DefaultListModel<String> commandTypesModel;
    private final DefaultListModel<String> commandOrdersModel;
    private final DefaultListModel<String> filePathsModel;
    private final DefaultListModel<String> programPathsModel;
    private final JList<String> commandTypes;
    private final JList<String> commandOrders ;
    private final JList<String> filePaths;
    private final JList<String> programPaths;
    
    public CommandDesk(DataBaseInterface dataBase){
        this.dataBase = dataBase;
        commandTypesModel =  new DefaultListModel<>();
        commandOrdersModel = new DefaultListModel<>();
        filePathsModel = new DefaultListModel<>();
        programPathsModel = new DefaultListModel<>();
        commandOrders = new JList<>(commandOrdersModel);
        commandTypes = new JList<>(commandTypesModel);
        filePaths = new JList<>(filePathsModel);
        programPaths = new JList<>(programPathsModel);
        
        createUI();
        setTitle("Those are my commands, so far");
        setIconImage((new ImageIcon("Decepticon Violet Logo.jpg")).getImage());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void createUI() {
        JPanel commandTypePanel = new JPanel();
        commandTypePanel.setBorder(BorderFactory.createTitledBorder("Command Type"));
        commandTypes.addMouseListener(listMouseListener());
        JScrollPane scrollTypes = new JScrollPane(commandTypes);
        scrollTypes.setPreferredSize(new Dimension(200,800));
        commandTypePanel.add(scrollTypes);
        
        JPanel commandOrderPanel = new JPanel();
        commandOrderPanel.setBorder(BorderFactory.createTitledBorder("Command Order"));
        commandOrders.addMouseListener(listMouseListener());
        JScrollPane scrollOrders = new JScrollPane(commandOrders);
        scrollOrders.setPreferredSize(new Dimension(200,800));
        commandOrderPanel.add(scrollOrders);
        
        JPanel commandFilePathPanel = new JPanel();
        commandFilePathPanel.setBorder(BorderFactory.createTitledBorder("File Path"));
        filePaths.addMouseListener(listMouseListener());
        JScrollPane scrollFilePaths = new JScrollPane(filePaths);
        scrollFilePaths.setPreferredSize(new Dimension(350,800));
        commandFilePathPanel.add(scrollFilePaths);
        
        JPanel commandProgramPathPanel = new JPanel();
        commandProgramPathPanel.setBorder(BorderFactory.createTitledBorder("Program Path"));
        programPaths.addMouseListener(listMouseListener());
        JScrollPane scrollProgramPaths = new JScrollPane(programPaths);
        scrollProgramPaths.setPreferredSize(new Dimension(350,800));
        commandProgramPathPanel.add(scrollProgramPaths);
        
        Map <String, String> quickCommands = dataBase.getURLs();
        String programUsed = dataBase.getBrowserPath();
        for (String key : quickCommands.keySet()) {
            commandTypesModel.addElement("URL");
            commandOrdersModel.addElement("Open " + key);
            filePathsModel.addElement(quickCommands.get(key));
            programPathsModel.addElement(programUsed);
            commandTypesModel.addElement("URL");
            commandOrdersModel.addElement("Close " + key);
            filePathsModel.addElement(quickCommands.get(key));
            programPathsModel.addElement(programUsed);
        }
        
        quickCommands = dataBase.getPrograms();
        for (String key : quickCommands.keySet()) {
            commandTypesModel.addElement("Program");
            commandOrdersModel.addElement("Open " + key);
            filePathsModel.addElement("NONE");
            programPathsModel.addElement(quickCommands.get(key));
            commandTypesModel.addElement("Program");
            commandOrdersModel.addElement("Close " + key);
            filePathsModel.addElement("NONE");
            programPathsModel.addElement(quickCommands.get(key));
        }
        
        quickCommands = dataBase.getFolders();
        for (String key : quickCommands.keySet()) {
            commandTypesModel.addElement("Folder");
            commandOrdersModel.addElement("Open " + key);
            filePathsModel.addElement(quickCommands.get(key));
            programPathsModel.addElement("NONE");
        }
        
        quickCommands = dataBase.getSongs();
        programUsed = dataBase.getPathOf("Music Player");
        for (String key : quickCommands.keySet()) {
            commandTypesModel.addElement("Song");
            commandOrdersModel.addElement("Play " + key);
            filePathsModel.addElement(quickCommands.get(key));
            programPathsModel.addElement(programUsed);
        }
        
        quickCommands = dataBase.getFiles();
        for (String key : quickCommands.keySet()) {
            commandTypesModel.addElement("File");
            commandOrdersModel.addElement("Open " + key);
            String file = quickCommands.get(key).split("::")[0].trim();
            filePathsModel.addElement(file);
            programUsed = quickCommands.get(key).split("::")[1].trim();
            programPathsModel.addElement(programUsed);
            commandTypesModel.addElement("File");
            commandOrdersModel.addElement("Close " + key);
            filePathsModel.addElement(file);
            programPathsModel.addElement(programUsed);
        }
        
        LinkedList<Command> allOtherCommands = dataBase.getAllOtherCommands();
        for (Command command : allOtherCommands) {
            commandTypesModel.addElement(command.getAction());
            commandOrdersModel.addElement(command.getCommandOrder());
            filePathsModel.addElement(command.getFilePath());
            programPathsModel.addElement(command.getProgramPath());
        }
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(closeActionListener());
        
        JPanel mainPanel = new JPanel();
        mainPanel.add(commandTypePanel);
        mainPanel.add(commandOrderPanel);
        mainPanel.add(commandFilePathPanel);
        mainPanel.add(commandProgramPathPanel);
        
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        southPanel.add(closeButton);
        
        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);
    }

    private MouseListener listMouseListener() {
        return new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                    int index = ((JList)evt.getSource()).getSelectedIndex();
                    commandTypes.setSelectedIndex(index);
                    commandOrders.setSelectedIndex(index);
                    filePaths.setSelectedIndex(index);
                    programPaths.setSelectedIndex(index);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int index = ((JList)e.getSource()).getSelectedIndex();
                commandTypes.setSelectedIndex(index);
                commandOrders.setSelectedIndex(index);
                filePaths.setSelectedIndex(index);
                programPaths.setSelectedIndex(index);
            }
        };
    }

    private ActionListener closeActionListener() {
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                CommandDesk.this.dispose();
            }
        };
    }
}
