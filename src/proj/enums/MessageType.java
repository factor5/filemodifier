/**
 * 
 */
package proj.enums;

/**
 * @author Svilen Velikov
 * 
 * 07.07.2009
 */
public enum MessageType {
    ERROR(0), INFO(1);

    int type;

    private MessageType(int type) {
	this.type = type;
    }

    public int getValue() {
	return type;
    }
}
