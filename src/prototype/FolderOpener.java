package prototype;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Pablo Alonso
 */
public class FolderOpener {

    /**
     * Open the folder with the specific path
     *
     * @param folderPath The absolut path of the folder
     * @return True if folder open correctly, False if IOException
     */
    public static boolean openFolder(String folderPath) {
        try {
            if(Desktop.isDesktopSupported()){
                Desktop.getDesktop().open(new File(folderPath));
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
