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

/**
 * FileModifier is a class that provide methods for tracing the directory where
 * the class is placed and to search for files with required extension. If such
 * kind of file or files are found then they are parsed and all found parts like
 * in those files are removed and files are saved back.
 * 
 * @author Svilen Velikov
 * 
 *         20.01.2009
 */
public class FileModifier extends JFrame {

    private static final long serialVersionUID = -9056510793502618924L;

    /**
     * Message constants
     */
    public static String FILE_EXT_TYPE;
    private static final String CANT_SEARCH_DIR_ERROR = "Current directory cannot be traced!";
    private static final String NO_FILES_FOUND_INFO = "There aren't files with provided extesion!";
    private static final String IO_ERROR = "IO error!";
    private static final String INIT_PARAM_ERROR = "Wrong type or missing parameter error!";
    private static final String READY_MESSAGE = "End of scanning!";
    private static final String TITLE_ERROR = "Error";
    private static final String TITLE_RESULT = "Result";
    private static final String EMPTY_STRING = "";

    /**
     * Line separator.
     */
    private static final String SEPARATOR = System
            .getProperty("line.separator");

    /**
     * Path separator.
     */
    private static final String PATH_SEPARATOR = System
            .getProperty("file.separator");

    /**
     * Path list.
     */
    List<String> pathList = null;

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
     *            the extension file type that this application will search for
     * @param strings
     *            the matrix with source and replacement strings
     * @param log
     *            the logger
     * @throws IllegalArgumentException
     *             if any of the arguments is missing or irrelevant
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
     *            configurations read from property file
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

            int choise = JOptionPane.showConfirmDialog(null, "Make backup?",
                    "Backup", JOptionPane.YES_NO_OPTION);
            try {
                if (choise == Confiramtion.YES.getValue()) {
                    backupFiles(curDirectory);
                }
            } catch (IOException e) {
                log.appendLine(e.getMessage(), TextStyle.RED.style());
                choise = JOptionPane.showConfirmDialog(null, e.getMessage()
                        + SEPARATOR + " Proceed without backup?", TITLE_ERROR,
                        JOptionPane.YES_NO_OPTION);
                if (choise == Confiramtion.NO.getValue()) {
                    throw new Exception(
                            "Modification aborted due to unsuccesfull backup!");
                } else {
                    log.appendLine(SEPARATOR + "Backup was skipped!",
                            TextStyle.RED.style());
                }
            }

            log.appendLine(SEPARATOR + "Start parsing files:", TextStyle.BOLD
                    .style());

            parseFiles();

            log.appendLine(SEPARATOR + "Job done!", TextStyle.BOLD.style());
            // displayMessage(READY_MESSAGE, TITLE_RESULT, MESS_TYPE_INFO);
        } catch (IOException e) {
            log.appendLine(e.getMessage(), TextStyle.RED.style());
            displayMessage(e.getMessage(), TITLE_ERROR, MessageType.ERROR
                    .getValue());
        } catch (Exception e) {
            log.appendLine(e.getMessage(), TextStyle.RED.style());
            displayMessage(e.getMessage(), TITLE_ERROR, MessageType.ERROR
                    .getValue());
        } finally {
            this.setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Makes a backup of the found files. On every execution of the application
     * if backup is applied a new directory with unique name will be created and
     * the files will be copied in there.
     * 
     * @param path
     *            the path where to store the backup
     * @throws IOException
     *             if provided path is null or empty
     */
    private void backupFiles(final String path) throws IOException {
        if (path == null || path.isEmpty()) {
            throw new IOException(
                    "Backup failure because of missing or empty path!");
        }

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
        }
    }

    /**
     * Searches recursively the directory where this class is situated to find
     * any subtitle files. In case that any files are found they are stored in a
     * list.
     * 
     * @param current
     *            the directory where to search for subtitle files
     * @throws IOException
     */
    void findFiles(File current) throws IOException {
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
     *            the source file
     * @return the buffer where the content of the file is stored after reading
     * @throws IOException
     */
    private StringBuilder readFile(String path) throws IOException {
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
    private void parseFiles() throws IOException {
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
     *            the modified content of the subtitle file that is to be saved
     *            back in file
     * @param path
     *            the path to the destination file
     * @throws IOException
     */
    private void writeFile(String buferAsString, String path)
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
    public String findCurrentDirectory() throws IOException {
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
     *            the message that should be displayed.
     * @param title
     *            the title for this pop up window
     * @param type
     *            the type of the option pane (error=0, warning=1)
     */
    public void displayMessage(String msg, String title, int type) {
        JOptionPane.showMessageDialog(null, msg, title, type);
    }
}
