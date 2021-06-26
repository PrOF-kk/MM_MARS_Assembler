package mars.venus;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileSystemView;

/**
 * Factory to create instances of JFileChooser using the system look and feel
 * 
 * @author Valerio Colella
 * @version June 2021
 */
public class SystemJFileChooser {

	private static LookAndFeel laf = UIManager.getLookAndFeel();
	
	/**
	 * @link {@link JFileChooser#JFileChooser()}
	 */
	public static JFileChooser create() {
		setSystemLaf();
		JFileChooser fileChooser = new JFileChooser();
		setDefinedLaf();
		return fileChooser;
	}

	/**
	 * @link {@link JFileChooser#JFileChooser(String)}
	 */
	public static JFileChooser create(String currentDirectoryPath) {
		setSystemLaf();
		JFileChooser fileChooser = new JFileChooser(currentDirectoryPath);
		setDefinedLaf();
		return fileChooser;
	}

	/**
	 * @link {@link JFileChooser#JFileChooser(File)}
	 */
	public static JFileChooser create(File currentDirectory) {
		setSystemLaf();
		JFileChooser fileChooser = new JFileChooser(currentDirectory);
		setDefinedLaf();
		return fileChooser;
	}

	/**
	 * @link {@link JFileChooser#JFileChooser(FileSystemView)}
	 */
	public static JFileChooser create(FileSystemView fsv) {
		setSystemLaf();
		JFileChooser fileChooser = new JFileChooser(fsv);
		setDefinedLaf();
		return fileChooser;
	}

	/**
	 * @link {@link JFileChooser#JFileChooser(File, FileSystemView)}
	 */
	public static JFileChooser create(File currentDirectory, FileSystemView fsv) {
		setSystemLaf();
		JFileChooser fileChooser = new JFileChooser(currentDirectory, fsv);
		setDefinedLaf();
		return fileChooser;
	}

	/**
	 * @link {@link JFileChooser#JFileChooser(String, FileSystemView)}
	 */
	public static JFileChooser create(String currentDirectoryPath, FileSystemView fsv) {
		setSystemLaf();
		JFileChooser fileChooser = new JFileChooser(currentDirectoryPath, fsv);
		setDefinedLaf();
		return fileChooser;
	}
	
	private static void setSystemLaf() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// Will not happen
		}
	}
	
	private static void setDefinedLaf() {
		try {
			UIManager.setLookAndFeel(laf);
		}
		catch (UnsupportedLookAndFeelException e) {
			// Would have been caught on VenusUI init
		}
	}
	
	private SystemJFileChooser() {
		// No public constructor
	}

}
