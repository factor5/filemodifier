/**
 * 
 */
package proj.mod.backup;

import java.io.File;
import java.io.IOException;

/**
 * Interface that defines functionality that copier classes must have.
 * 
 * @author Svilen Velikov
 * 
 * 08.07.2009
 */
public interface Copier {

    /**
     * Copy source file to the destination place.
     * 
     * @param source
     *                the source file handler
     * @param dest
     *                the destination
     * @throws IOException
     */
    public void copy(final File source, final File dest) throws IOException;

}
