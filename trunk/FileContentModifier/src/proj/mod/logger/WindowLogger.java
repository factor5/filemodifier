/**
 * 
 */
package proj.mod.logger;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Window logger is a class that outputs the noticeable events in a window
 * panel.
 * 
 * @author Svilen Velikov
 * 
 * 18.04.2009
 */
public class WindowLogger extends JFrame implements ILogger {

    /**
     * Line separator according to system.
     */
    private static final String LINE_BREAK = System
	    .getProperty("line.separator");

    /**
     * Panel where this log outputs text.
     */
    private static JTextPane logPanel;

    /**
     * Comment for StyledDocument.
     */
    private StyledDocument doc;

    /**
     * Constructor.
     */
    public WindowLogger() {
	this.setName("Log Panel");
	this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	logPanel = new JTextPane();
	logPanel.setEditable(false);
	doc = logPanel.getStyledDocument();

	JScrollPane scroll = new JScrollPane(logPanel);
	scroll.setPreferredSize(new Dimension(600, 300));
	this.getContentPane().add(scroll);
	this.setLocation(300, 200);
	this.pack();
	this.setVisible(true);
    }

    /**
     * Appends some text to the log and applies bold style to it if the bold
     * parameter is set to true.
     * 
     * @param text
     *                the text to be appended to the log.
     * @param bold
     *                shows if the text should have bold style
     */
    @Override
    public void appendText(final String text, final int styleType) {
	printText(text, getStyle(styleType));
    }

    /**
     * Append the provided text to the log. If the text style is set to bold
     * then it is applied.
     * 
     * @param text
     *                the text to be appended to the log
     * @param bold
     *                shows if the provided text should be printed in bold
     */
    @Override
    public void appendLine(final String text, final int styleType) {
	printText(text + LINE_BREAK, getStyle(styleType));
    }

    /**
     * Outputs the text to the panel inserting a line break after each string
     * and applies a style that is provided.
     * 
     * @param text
     *                the text to be printed into the text panel
     * @param attr
     *                the style to be applied to the text
     */
    private void printText(final String text, final AttributeSet attr) {
	try {
	    doc.insertString(doc.getLength(), text, attr);
	    logPanel.scrollRectToVisible(new Rectangle(0,
		    logPanel.getHeight() + 2, 1, 1));
	} catch (BadLocationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * 
     * @param styleType
     * @return
     */
    private Style getStyle(final int styleType) {
	Style s = null;
	switch (styleType) {
	case 1:
	    s = doc.addStyle("bold", null);
	    StyleConstants.setBold(s, true);
	    break;
	case 2:
	    s = doc.addStyle("red", null);
	    StyleConstants.setForeground(s, Color.RED);
	    break;
	case 3:
	    s = doc.addStyle("plain", null);
	    StyleConstants.setBold(s, false);
	    break;
	default:
	    break;
	}
	return s;
    }

}
