package prototype.Swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
public class DefaultPathChanger extends JDialog{
    
    private final DataBaseInterface dataBase;
    private final String programToChange;
    private final JTextField newPath;
    private final JLabel warningLabel;
    
    public DefaultPathChanger(DataBaseInterface dataBase, String programToChange){
        this.dataBase = dataBase;
        this.programToChange = programToChange;
        newPath = new JTextField();
        warningLabel = new JLabel(" ");
        createUI();
        
        setTitle("Change the " + this.programToChange + " Path");
        setIconImage((new ImageIcon("Decepticon Violet Logo.jpg")).getImage());
        pack();
        setLocationRelativeTo(null);
        setModal(true);
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void createUI() {
        JLabel oldPathLabel = new JLabel("Old Path: ");
        String oldAbsolutePath = dataBase.getPathOf(programToChange);
        if(oldAbsolutePath == null) oldAbsolutePath = "Not yet Specified";
        JLabel oldPath = new JLabel(oldAbsolutePath);
        newPath.setColumns(40);
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(browseButtonActionListener());
        JLabel newPathLabel = new JLabel("New Path: ");
        JButton acceptButton = new JButton("Accept");
        acceptButton.addActionListener(acceptButtonActionListener());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(cancelButtonActionListener());
        warningLabel.setForeground(Color.red);
        JPanel oldPathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        oldPathPanel.add(oldPathLabel);
        oldPathPanel.add(oldPath);
        
        JPanel newPathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        newPathPanel.add(newPathLabel);
        newPathPanel.add(newPath);
        newPathPanel.add(browseButton);
        
        JPanel warningLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        warningLabelPanel.add(warningLabel);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(oldPathPanel);
        centerPanel.add(newPathPanel);
        centerPanel.add(warningLabelPanel);
        
        JPanel southPanel = new JPanel();
        southPanel.add(acceptButton);
        southPanel.add(cancelButton);
        
        this.setLayout(new BorderLayout());
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);
    }

    private ActionListener browseButtonActionListener() {
        return new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setDialogTitle("Select The New Program");
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
                if(!path.isEmpty()){
                    newPath.setText(path);
                }
            }
        };
    }

    private ActionListener acceptButtonActionListener() {
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if(newPath.getText().trim().isEmpty()){
                    warningLabel.setText("New Path Field Is Empty");
                    return;
                }
                if(!checkIfFileExist(newPath.getText())) return;
                if(!checkIfFileisFile(newPath.getText())) return;
                dataBase.updateProgramPath(programToChange, newPath.getText().trim());
                DefaultPathChanger.this.dispose();
            }
            
            private boolean checkIfFileExist(String path) {
                File file = new File(path);
                if (!file.exists()) {
                    warningLabel.setText("The Path Is Invalid");
                    return false;
                }
                return true;
            }

            private boolean checkIfFileisFile(String path) {
                File file = new File(path);
                if (!file.isFile()) {
                    warningLabel.setText("The Path Is Not The Path Of A Valid File");
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
                DefaultPathChanger.this.dispose();
            }
        };
    }
}
