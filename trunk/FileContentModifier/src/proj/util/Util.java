/**
 * 
 */
package proj.util;

import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Svilen Velikov
 * 
 *         19.04.2009
 */
public class Util implements IMessages {

    private static final String CONFIG_FILE_NAME = "config";
    private static final String FILE_EXTESION_TYPE = "fileExt";
    private static final String PREFIX = "prefix";

    /**
     * Single instance of this utility class.
     */
    private static Util utilInstance = null;

    /**
     * File extension type.
     */
    private String fileExtensionType = null;

    /**
     * Private constructor.
     */
    private Util() {
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
     * @return an array of strings or empty array
     * @throws MissingResourceException
     */
    public String[][] loadProperties() throws MissingResourceException {
        final ResourceBundle bundle = getBundle(CONFIG_FILE_NAME);
        this.fileExtensionType = bundle.getString(FILE_EXTESION_TYPE);
        final String prefix = bundle.getString(Util.PREFIX);

        List<String> strings = new LinkedList<String>();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            if (key.startsWith(prefix)) {
                strings.add(bundle.getString(key));
            }
        }
        String[][] result = {};
        if (strings.size() != 0) {
            result = parsePropertyValues(strings.toArray(new String[strings
                    .size()]));
        }
        return result;
    }

    /**
     * Gets a resource bundle with provided name.
     * 
     * @param bundleName
     *            the name for the bundle to be loaded.
     * @return the resource bundle
     * @throws MissingResourceException
     *             if the bundle can't be found
     */
    private ResourceBundle getBundle(final String bundleName)
            throws MissingResourceException {
        return ResourceBundle.getBundle(bundleName);
    }

    /**
     * Iterates the values of the provided array and for every value finds out 2
     * parts - a string to be found and a replacement for that string (They
     * should be separated by '|' sign). These 2 parts are stored in a two
     * dimensional array.
     * 
     * @param strings
     *            an array containing properties values read from the properties
     *            file
     * @return an two dimensional array containing target and replacement
     *         strings
     */
    private String[][] parsePropertyValues(final String[] strings) {
        final String[][] separatedStrings = new String[strings.length][2];
        for (int i = 0; i < strings.length; i++) {
            String[] temp = strings[i].split(PROPERTY_SEPARATOR);
            // there is property set but no value for it
            // or improper value for property (more than two parts separated by
            // '|' sign)
            if (temp.length == 0 || temp.length > 2) {
                continue;
            }

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
