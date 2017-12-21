package prototype.Swing;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Pablo Alonso
 */
public class NameSelector extends JDialog{
    
    private final JTextField nameField;
    private final JLabel warningLabel;
    private String newName;

    public NameSelector(){
        nameField = new JTextField();
        warningLabel = new JLabel(" ");
        newName = "The one that can't be named";
        
        createUI();
        setTitle("Set your name");
        setIconImage((new ImageIcon("Decepticon Violet Logo.jpg")).getImage());
        pack();
        setLocationRelativeTo(null);
        setModal(true);
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public String getNewName(){
        return newName;
    }
    
    private void createUI() {
        JLabel label = new JLabel("Write your name:");
        JButton accept = new JButton("Accept");
        accept.addActionListener(acceptButtonActionListener());
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(cancelButtonActionListener());
        
        nameField.setColumns(40);
        
        //Layout
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        labelPanel.add(label);
        
        JPanel warningLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        warningLabelPanel.add(warningLabel);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        warningLabelPanel.add(accept);
        warningLabelPanel.add(cancel);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(labelPanel);
        centerPanel.add(nameField);
        centerPanel.add(warningLabelPanel);
        centerPanel.add(buttonsPanel);
        
        this.add(centerPanel);
    }

    private ActionListener acceptButtonActionListener() {
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!nameField.getText().isEmpty()){
                    newName = nameField.getText();
                }
                NameSelector.this.dispose();
            }
        };
    }

    private ActionListener cancelButtonActionListener() {
        return new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                NameSelector.this.dispose();
            }
        };
    }
    
}
