/**
 * 
 */
package proj.mod;

import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import proj.enums.Confiramtion;
import proj.enums.MessageType;
import proj.mod.backup.Copier;
import proj.mod.backup.FileCopyNIO;
import proj.mod.logger.ILogger;
import proj.mod.logger.TextStyle;
import proj.util.IMessages;

/**
 * FileModifier is a class that provide methods for tracing the directory where
 * the class is placed and to search for files with required extension. If such
 * kind of file or files are found then they are parsed and all found parts like
 * in those files are removed and files are saved back.
 * 
 * @author Svilen Velikov
 * 
 * 20.01.2009
 */
public class FileModifier extends JFrame implements IMessages {

    private static final long serialVersionUID = -9056510793502618924L;

    /**
     * Extension type for the files that are being modified. This is taken from
     * configuration file.
     */
    private String FILE_EXT_TYPE = "";

    /**
     * Path list.
     */
    private List<String> pathList = null;

    /**
     * Array containing the strings to be removed from the file.
     */
    private String[][] strings = null;

    /**
     * Reference to logger.
     */
    private ILogger log;

    /**
     * Initializing constructor.
     * 
     * @param fileExt
     *                the extension file type that this application will search
     *                for
     * @param strings
     *                the matrix with source and replacement strings
     * @param log
     *                the logger
     * @throws IllegalArgumentException
     *                 if any of the arguments is missing or irrelevant
     */
    public FileModifier(String fileExt, String[][] strings, ILogger log)
	    throws IllegalArgumentException {
	if (fileExt.isEmpty() || strings == null || strings.length == 0
		|| log == null) {
	    displayMessage(INIT_PARAM_ERROR, TITLE_ERROR, MessageType.ERROR
		    .getValue());
	    throw new IllegalArgumentException(INIT_PARAM_ERROR);
	}
	this.FILE_EXT_TYPE = fileExt;
	this.strings = strings;
	this.log = log;
    }

    /**
     * Main entry method that dispatches the work about modifying the files.
     * 
     * @param strings
     *                configurations read from property file
     */
    public void modify() {
	String curDirectory = "";
	try {
	    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

	    pathList = new ArrayList<String>();
	    curDirectory = findCurrentDirectory();
	    log.appendLine("Current directory:", TextStyle.BOLD.style());
	    log.appendLine(curDirectory, TextStyle.PLAIN.style());

	    File current = new File(curDirectory);
	    log.appendLine(SEPARATOR + "Found files:", TextStyle.BOLD.style());
	    findFiles(current);

	    if (pathList.size() == 0) {
		displayMessage(NO_FILES_FOUND_INFO, TITLE_RESULT,
			MessageType.INFO.getValue());
		return;
	    }

	    makeBackup(pathList, curDirectory);

	    log.appendLine(SEPARATOR + "Start parsing files:", TextStyle.BOLD
		    .style());

	    parseFiles(pathList);

	    log.appendLine(SEPARATOR + "Job done!", TextStyle.BOLD.style());
	} catch (IOException e) {
	    log.appendLine(e.getMessage(), TextStyle.RED.style());
	} catch (Exception e) {
	    log.appendLine(e.getMessage(), TextStyle.RED.style());
	} finally {
	    this.setCursor(Cursor.getDefaultCursor());
	}
    }

    /**
     * Organizes a dialog with the user if a backup to be done and invokes a
     * method that saves a copies the files that are marked for modification.
     * 
     * @param pathList
     *                the files that are to be saved
     * @param curDirectory
     *                the current directory
     * @throws Exception
     */
    private void makeBackup(final List<String> pathList,
	    final String curDirectory) throws Exception {
	if (curDirectory == null || curDirectory.isEmpty()) {
	    throw new IOException(MISSING_OR_EMPTY_PATH);
	}

	int choise = JOptionPane.showConfirmDialog(null, "Make backup?",
		"Backup", JOptionPane.YES_NO_OPTION);
	try {
	    if (choise == Confiramtion.YES.getValue()) {
		writeBackupFiles(curDirectory, pathList);
	    }
	} catch (IOException e) {
	    log.appendLine(e.getMessage(), TextStyle.RED.style());
	    choise = JOptionPane.showConfirmDialog(null, e.getMessage()
		    + SEPARATOR + " Proceed without backup?", TITLE_ERROR,
		    JOptionPane.YES_NO_OPTION);
	    if (choise == Confiramtion.NO.getValue()) {
		throw new Exception(BACKUP_ABORTED);
	    } else {
		log.appendLine(SEPARATOR + "Backup was skipped!", TextStyle.RED
			.style());
	    }
	}
    }

    /**
     * Makes a backup of the found files. On every execution of the application
     * if backup is applied a new directory with unique name will be created and
     * the files will be copied in there.
     * 
     * @param path
     *                the path where to store the backup
     * @param pathList
     *                a list containing the found files that are gone be
     *                modified
     * @throws IOException
     *                 if provided path is null or empty
     * 
     */
    private void writeBackupFiles(final String path, final List<String> pathList)
	    throws IOException {
	if (new File(path).canWrite()) {
	    String backupDirPath = "backup_" + (System.currentTimeMillis());

	    if (new File(backupDirPath).mkdir()) {
		log.appendLine(SEPARATOR + "Start backup operation:",
			TextStyle.BOLD.style());

		Copier copier = new FileCopyNIO();
		StringBuilder srcFileNewName = new StringBuilder();
		for (String pathToSrcFile : pathList) {

		    srcFileNewName.append(backupDirPath);
		    srcFileNewName.append(pathToSrcFile.substring(pathToSrcFile
			    .lastIndexOf(PATH_SEPARATOR)));

		    copier.copy(new File(pathToSrcFile), new File(
			    srcFileNewName.toString()));
		    srcFileNewName.setLength(0);

		    log.appendLine("Backup of :" + pathToSrcFile,
			    TextStyle.GREEN.style());
		}
		log.appendLine("Backup finished!", TextStyle.BOLD.style());
	    }
	} else {
	    throw new IOException(CANT_WRITE_ERROR);
	}
    }

    /**
     * Searches recursively the directory where this class is situated to find
     * any subtitle files. In case that any files are found they are stored in a
     * list.
     * 
     * @param current
     *                the directory where to search for subtitle files
     * @throws IOException
     */
    private void findFiles(File current) throws IOException {
	try {
	    if (current.isFile()) {
		if (current.getName().endsWith(FILE_EXT_TYPE)) {
		    pathList.add(current.getCanonicalPath());
		    log.appendLine(current.getCanonicalPath(), 3);
		}
	    } else {
		File[] siblings = current.listFiles();
		for (int i = 0; i < siblings.length; i++) {
		    findFiles(siblings[i]);
		}
	    }
	} catch (Exception e) {
	    throw new IOException(CANT_SEARCH_DIR_ERROR);
	}
    }

    /**
     * Reads the content of the file denoted by the path parameter to an buffer.
     * 
     * @param path
     *                the source file
     * @return the buffer where the content of the file is stored after reading
     * @throws IOException
     */
    private StringBuilder readFile(final String path) throws IOException {
	BufferedReader br = null;
	try {
	    br = new BufferedReader(new FileReader(path));
	} catch (FileNotFoundException e1) {
	    // TODO handle this exception
	    e1.printStackTrace();
	}

	StringBuilder bufer = new StringBuilder();
	String line = "";
	try {
	    while ((line = br.readLine()) != null) {
		bufer.append(line);
		bufer.append(SEPARATOR);
	    }
	    return bufer;
	} catch (IOException e) {
	    throw new IOException(IO_ERROR);
	} finally {
	    try {
		br.close();
	    } catch (IOException e) {
		// TODO handle this exception
		e.printStackTrace();
	    }
	}
    }

    /**
     * Parses the files stored in the pathList and if any of the provided
     * strings are found then they are removed.
     * 
     * @throws IOException
     */
    private void parseFiles(final List<String> pathList) throws IOException {
	try {
	    for (String path : pathList) {

		log.appendLine("Scanning file: " + path, TextStyle.PLAIN
			.style());

		StringBuilder bufer = readFile(path);
		String buferAsString = "";
		if (bufer.length() > 0) {
		    buferAsString = bufer.toString();
		    for (int i = 0; i < strings.length; i++) {
			buferAsString = buferAsString.replaceAll(strings[i][0],
				strings[i][1]);
		    }
		}
		writeFile(buferAsString, path);

		log.appendLine("Done", TextStyle.PLAIN.style());
	    }
	} catch (Exception e) {
	    throw new IOException(IO_ERROR);
	}
    }

    /**
     * Writes the content of the string buffer back to the file.
     * 
     * @param buferAsString
     *                the modified content of the subtitle file that is to be
     *                saved back in file
     * @param path
     *                the path to the destination file
     * @throws IOException
     */
    private void writeFile(final String buferAsString, final String path)
	    throws IOException {
	BufferedWriter bw = new BufferedWriter(new FileWriter(path));

	String[] arrBuf = buferAsString.split(SEPARATOR);
	int arrLen = arrBuf.length;
	try {
	    for (int i = 0; i < arrLen; i++) {
		bw.write(arrBuf[i]);
		bw.newLine();
	    }
	} finally {
	    bw.close();
	}
    }

    /**
     * Finds the directory where this class is situated in.
     * 
     * @return the path that denotes the current directory for this class
     * @throws IOException
     */
    private String findCurrentDirectory() throws IOException {
	File file = new File(".");
	try {
	    String currentDirectory = file.getCanonicalPath();
	    return currentDirectory;
	} catch (Exception e) {
	    throw new IOException(CANT_SEARCH_DIR_ERROR);
	}
    }

    /**
     * Displays a pop up message that tells the result, status or error.
     * 
     * @param msg
     *                the message that should be displayed.
     * @param title
     *                the title for this pop up window
     * @param type
     *                the type of the option pane (error=0, warning=1)
     */
    public void displayMessage(String msg, String title, int type) {
	JOptionPane.showMessageDialog(null, msg, title, type);
    }

    /**
     * Getter for FILE_EXT_TYPE.
     * 
     * @return the FILE_EXT_TYPE
     */
    public String getFILE_EXT_TYPE() {
	return FILE_EXT_TYPE;
    }

    /**
     * Setter for FILE_EXT_TYPE.
     * 
     * @param file_ext_type
     *                the fILE_EXT_TYPE to set
     * @throws IllegalArgumentException
     */
    public void setFILE_EXT_TYPE(final String file_ext_type)
	    throws IllegalArgumentException {
	if (file_ext_type == null || file_ext_type.isEmpty()) {
	    throw new IllegalArgumentException(ILLEGAL_STRING_ARG_ERROR);
	}
	FILE_EXT_TYPE = file_ext_type;
    }

    /**
     * Setter for strings array.
     * 
     * @param strings
     *                the strings to set
     * @throws IllegalArgumentException
     */
    public void setStrings(final String[][] strings)
	    throws IllegalArgumentException {
	if (strings == null || strings.length == 0) {
	    throw new IllegalArgumentException(ILLEGAL_ARRAY_ARG_ERROR);
	}
	this.strings = strings;
    }
}
