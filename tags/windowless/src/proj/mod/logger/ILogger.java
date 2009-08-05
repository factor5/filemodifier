/**
 * 
 */
package proj.mod.logger;

/**
 * @author Svilen Velikov
 * 
 * 18.04.2009
 */
public interface ILogger {

    void appendText(String text, int styleType);

    void appendLine(String text, int styleType);

}
