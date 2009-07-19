/**
 * 
 */
package proj.util;

/**
 * @author Svilen Velikov
 * 
 * 19.07.2009
 */
public interface IMessages {
    
    /**
     * Empty string.
     */
    public static final String EMPTY_STRING = "";
    
    /**
     * Path separator.
     */
    public static final String PATH_SEPARATOR = System
	    .getProperty("file.separator");
    
    /**
     * Line separator.
     */
    public static final String SEPARATOR = System
	    .getProperty("line.separator");
    
    /**
     * Message and error string constants
     */
    public static final String CANT_SEARCH_DIR_ERROR = "Current directory cannot be traced!";
    public static final String NO_FILES_FOUND_INFO = "There aren't files with provided extesion!";
    public static final String IO_ERROR = "IO error!";
    public static final String INIT_PARAM_ERROR = "Wrong type or missing parameter error!";
    public static final String READY_MESSAGE = "End of scanning!";
    public static final String TITLE_ERROR = "Error";
    public static final String TITLE_RESULT = "Result";
    

}
