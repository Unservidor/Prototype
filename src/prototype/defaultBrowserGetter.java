package prototype;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Pablo Alonso
 * Adapted from: https://stackoverflow.com/questions/15852885/method-returning-default-browser-as-a-string
 */
public class defaultBrowserGetter {
    
    public static String getDefaultBrowserPath(){
        try {
            // Get registry where we find the default browser
            Process process = Runtime.getRuntime().exec("REG QUERY HKEY_CLASSES_ROOT\\http\\shell\\open\\command");
            Scanner scanner = new Scanner(process.getInputStream());
            
            while (scanner.hasNextLine()) {
                // Get output from the terminal, and replace all '\' with '/' (makes regex a bit more manageable)
                String registry = (scanner.nextLine()).replaceAll("\\\\", "/").trim();
                // Extract the default browser
                Matcher matcher = Pattern.compile("/(?=[^/]*$)(.+?)[.]").matcher(registry);
                if (matcher.find()) {
                    String path = registry.substring(registry.indexOf("\"")+1, registry.indexOf("\"", registry.indexOf("\"")+1));
                    scanner.close();
                    return path;
                }
            }
            // Match wasn't found, still need to close Scanner
            scanner.close();
        } catch (Exception e) {}
        return "Error: Unable to get default browser";
    }
}
