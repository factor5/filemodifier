/**
 * 
 */
package proj.mod.logger;

/**
 * @author Svilen Velikov
 * 
 * 19.04.2009
 */
public enum TextStyle {
    
    BOLD (1),
    RED  (2),
    PLAIN (3);
    
    private TextStyle(int type) {
	this.type = type;
    }
    
    private int type;
    
    public int style() {
	return type;
    }
}
