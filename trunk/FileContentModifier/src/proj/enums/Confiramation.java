/**
 * 
 */
package proj.enums;

/**
 * @author Svilen Velikov
 * 
 * 07.07.2009
 */
public enum Confiramation {
    YES(0), NO(1);

    int type;

    private Confiramation(int type) {
	this.type = type;
    }

    public int getValue() {
	return type;
    }
}
