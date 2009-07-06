/**
 * 
 */
package proj.mod;

import java.util.MissingResourceException;

import proj.mod.logger.WindowLogger;

/**
 * @author Svilen Velikov
 * 
 * 20.01.2009
 */
public class Start {

    /**
     * @param args
     */
    public static void main(String args[]) {
	String[][] strings = null;
	FileModifier fmod = null;
	Util util = Util.getInstance();
	try {
	    strings = util.loadProperties();

	    if (strings == null) {
		throw new Exception();
	    }
	    fmod = new FileModifier(util.getFileExtensionType(), strings, new WindowLogger());
	    fmod.modify();
	} catch (MissingResourceException e) {
	    fmod
		    .displayMessage(
			    "Application can't be started because of missing config.properties file or wrong format!",
			    "Error", Byte.valueOf("1"));
	    e.printStackTrace();
	} catch (Exception e) {
	    fmod
		    .displayMessage(
			    "There are no strings found for replacement in config file!",
			    "Error", Byte.valueOf("1"));
	    e.printStackTrace();
	}
    }
}
