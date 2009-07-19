/**
 * 
 */
package proj.mod;

import java.util.MissingResourceException;

import javax.swing.JOptionPane;

import proj.mod.logger.WindowLogger;
import proj.util.Util;

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
	    // there is no properties read from the configuration file
	    if (strings.length == 0) {
		throw new Exception();
	    }

	    fmod = new FileModifier(util.getFileExtensionType(), strings,
		    new WindowLogger());
	    fmod.modify();
	} catch (MissingResourceException e) {
	    JOptionPane
		    .showMessageDialog(
			    null,
			    "Application can't be started because of missing config.properties file or wrong format!",
			    "Error", 0);

	    e.printStackTrace();
	} catch (Exception e) {
	    JOptionPane
		    .showMessageDialog(
			    null,
			    "There are no strings found for replacement in config file!",
			    "Error", 0);
	    e.printStackTrace();
	}
    }
}
