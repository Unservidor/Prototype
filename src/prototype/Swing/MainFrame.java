package prototype.Swing;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import prototype.MainAI;

/**
 *
 * @author Pablo Alonso
 */
public class MainFrame extends JFrame{
    
    private final MainAI mainAI;
    private final JLabel recognizedText;
    
    public MainFrame(MainAI mainAI){
        this.mainAI = mainAI;
        recognizedText = new JLabel(" ");
        setGUI();
        setTitle("Prototype");
        setIconImage((new ImageIcon("Decepticon Violet Logo.jpg")).getImage());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
    
    public void setRecognizedText(String text){
        recognizedText.setText(text);
    }

    private void setGUI() {
        JLabel image = new JLabel();
        image.setIcon(new ImageIcon("Decepticon Logo.jpg"));
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(image);
        mainPanel.add(recognizedText, BorderLayout.SOUTH);
        
        this.addWindowListener(windowListener());
        this.add(mainPanel);
    }

    private WindowListener windowListener() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                mainAI.close();
            }
        };
    }
}
