/**
 * 
 */
package proj.mod.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class that implements a copy operation using old IO streams.
 * 
 * @author Svilen Velikov
 * 
 * 08.07.2009
 */
public class FileCopy implements Copier {

    /**
     * The buffer's size.
     */
    private static final int BUFF_SIZE = 5 * 1024 * 1024;

    /**
     * The buffer.
     */
    private static final byte[] buffer = new byte[BUFF_SIZE];

    /**
     * Makes a copy of the provided file to the needed destination place.
     * 
     * @param source
     *                the source file handler
     * @param dest
     *                the destination place
     * @throws IOException
     *                 if an error occurs during copy operation
     */
    @Override
    public void copy(final File source, final File dest) throws IOException {
	InputStream in = null;
	FileOutputStream out = null;
	try {
	    in = new FileInputStream(source);
	    out = new FileOutputStream(dest);

	    while (true) {
		int count = in.read(buffer);
		if (count == -1) {
		    break;
		}
		out.write(buffer, 0, count);
	    }
	} catch (FileNotFoundException e) {
	    throw new IOException(
		    "Wrong source or destination path for backup operation!");
	} finally {
	    if (out != null) {
		out.close();
	    }
	    if (in != null) {
		in.close();
	    }
	}
    }
}
