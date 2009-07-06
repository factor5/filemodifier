/**
 * 
 */
package proj.mod;

import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Svilen Velikov
 * 
 * 19.04.2009
 */
public class Util {

    /**
     * Single instance of this utility class.
     */
    private static Util utilInstance = null;

    /**
     * ResourceBundle instance.
     */
    private ResourceBundle bundle = null;

    /**
     * File extension type.
     */
    private String fileExtensionType = null;

    /**
     * Private constructor.
     */
    private Util() {
	bundle = getBundle();
	fileExtensionType = bundle.getString("fileExt");
    }

    /**
     * Static factory method to get an instance of this utility class.
     * 
     * @return
     */
    public static Util getInstance() {
	if (utilInstance == null) {
	    utilInstance = new Util();
	}
	return utilInstance;
    }

    /**
     * Loads configuration based in properties file. It sets the file type
     * extension taken from configuration file and loads all strings that should
     * be replaced. Those strings are found by its prefix also taken from
     * configuration.
     * 
     * @return an array of strings
     * @throws MissingResourceException
     */
    public String[][] loadProperties() throws MissingResourceException {
	final ResourceBundle bundle = getBundle();
	final String PREFIX = bundle.getString("prefix");

	List<String> strings = new LinkedList<String>();
	Set<String> keys = bundle.keySet();
	for (String key : keys) {
	    if (key.startsWith(PREFIX)) {
		strings.add(bundle.getString(key));
	    }
	}
	if (strings.size() != 0) {
	    return separate(strings.toArray(new String[strings.size()]));
	}
	return null;
    }

    /**
     * 
     * @return
     */
    private ResourceBundle getBundle() throws MissingResourceException {
	return ResourceBundle.getBundle("config");
    }

    /**
     * 
     * 
     * @param strings
     * @return
     */
    private String[][] separate(final String[] strings) {
	final String[][] separatedStrings = new String[strings.length][strings.length];
	final String EMPTY_STRING = "";
	for (int i = 0; i < strings.length; i++) {
	    String[] temp = strings[i].split("\\|");
	    separatedStrings[i][0] = temp[0];
	    if (temp.length == 1) {
		separatedStrings[i][1] = EMPTY_STRING;
	    } else {
		separatedStrings[i][1] = temp[1];
	    }
	}

	return separatedStrings;
    }

    /**
     * 
     * 
     * @return the fileExtensionType
     */
    public String getFileExtensionType() {
	return fileExtensionType;
    }
}
