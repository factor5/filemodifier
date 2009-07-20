/**
 * 
 */
package proj.mod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import proj.enums.Confiramtion;
import proj.enums.MessageType;
import proj.mod.backup.Copier;
import proj.mod.backup.FileCopy;
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
 *         20.01.2009
 */
public class FileModifier implements IMessages {

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
     * Counter for the found files.
     */
    private int countFoundFiles = 1;

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
            pathList = new ArrayList<String>();
            curDirectory = findCurrentDirectory();
            log.appendLine("Current directory:", TextStyle.BOLD.style());
            log.appendLine(curDirectory, TextStyle.PLAIN.style());

            File current = new File(curDirectory);
            log.appendLine(SEPARATOR + "Found files:", TextStyle.BOLD.style());
            searchFiles(current);

            if (pathList.size() == 0) {
                displayMessage(NO_FILES_FOUND_INFO, TITLE_RESULT,
                        MessageType.INFO.getValue());
                return;
                // System.exit(EXIT_ON_CLOSE);
            }

            makeBackup(pathList, curDirectory);

            log.appendLine(SEPARATOR + "Start parsing files:", TextStyle.BOLD
                    .style());

            parseFiles(pathList);

            log.appendLine(SEPARATOR + "Job done!", TextStyle.BOLD.style());
        } catch (IOException e) {
            e.printStackTrace();
            log.appendLine(e.getMessage(), TextStyle.RED.style());
        } catch (Exception e) {
            e.printStackTrace();
            log.appendLine(e.getMessage(), TextStyle.RED.style());
        }
    }

    /**
     * Organizes a dialog with the user if a backup to be done and invokes a
     * method that saves a copies the files that are marked for modification.
     * 
     * @param pathList
     *            the files that are to be saved
     * @param curDirectory
     *            the current directory
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
            e.printStackTrace();
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
     *            the path where to store the backup
     * @param pathList
     *            a list containing the found files that are gone be modified
     * @throws IOException
     *             if provided path is null or empty
     * 
     */
    private void writeBackupFiles(final String path, final List<String> pathList)
            throws IOException {
        String rootPath = System.getProperty("user.home") + "\\";
        if (new File(rootPath).canWrite()) {
            String backupDirPath = rootPath + "FMod_backup_"
                    + (System.currentTimeMillis());

            if (new File(backupDirPath).mkdir()) {
                log.appendLine(
                        SEPARATOR + "Root directory for the backup is: ",
                        TextStyle.BOLD.style());
                log.appendLine(rootPath, TextStyle.RED.style());
                log.appendLine("Start backup operation:", TextStyle.BOLD
                        .style());

                Copier copier = new FileCopy();
                StringBuilder srcFileNewName = new StringBuilder();
                int counter = 1;
                for (String pathToSrcFile : pathList) {

                    srcFileNewName.append(backupDirPath);
                    srcFileNewName.append(pathToSrcFile.substring(pathToSrcFile
                            .lastIndexOf(PATH_SEPARATOR)));

                    copier.copy(new File(pathToSrcFile), new File(
                            srcFileNewName.toString()));
                    srcFileNewName.setLength(0);

                    log.appendLine(counter + ". " + pathToSrcFile,
                            TextStyle.GREEN.style());
                    counter++;
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
     *            the directory where to search for subtitle files
     * @throws IOException
     */
    private void searchFiles(File current) throws IOException {
        try {
            if (current.isFile()) {
                if (current.getName().endsWith(FILE_EXT_TYPE)) {
                    pathList.add(current.getCanonicalPath());
                    log.appendLine(countFoundFiles + ". "
                            + current.getCanonicalPath(), 3);
                    countFoundFiles++;
                }
            } else {
                File[] siblings = current.listFiles();
                for (int i = 0; i < siblings.length; i++) {
                    searchFiles(siblings[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            int counter = 1;
            StringBuilder bufer = null;
            for (String path : pathList) {

                log.appendLine("Scanning file: " + counter + ". " + path,
                        TextStyle.PLAIN.style());
                counter++;

                bufer = readFile(path);
                String[] buffAsArray = bufer.toString().split(SEPARATOR);
                Integer arrLen = buffAsArray.length;
                if (arrLen > 0) {
                    for (int i = 0; i < arrLen; i++) {
                        for (int j = 0; j < strings.length; j++) {
                            buffAsArray[i] = buffAsArray[i].replace(
                                    strings[j][0], strings[j][1]);
                        }
                    }
                }

                //                
                // String buferAsString = "";
                // if (bufer.length() > 0) {
                // buferAsString = bufer.toString();
                // for (int i = 0; i < strings.length; i++) {
                // buferAsString = buferAsString.replaceAll(strings[i][0],
                // strings[i][1]);
                // }
                // }
                // writeFile(buferAsString, path);
                writeFile(buffAsArray, path);

                log.appendLine("Done", TextStyle.PLAIN.style());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    private void writeFile(final String[] buferAsArray, final String path)
            throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));

        // String[] arrBuf = buferAsString.split(SEPARATOR);
        int arrLen = buferAsArray.length;
        try {
            for (int i = 0; i < arrLen; i++) {
                bw.write(buferAsArray[i]);
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
            e.printStackTrace();
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
     *            the fILE_EXT_TYPE to set
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
     *            the strings to set
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
