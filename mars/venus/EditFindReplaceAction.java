package mars.venus;

import mars.*;
import mars.venus.editors.MARSTextEditingArea;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*
Copyright (c) 2003-2009,  Pete Sanderson and Kenneth Vollmar

Developed by Pete Sanderson (psanderson@otterbein.edu)
and Kenneth Vollmar (kenvollmar@missouristate.edu)

Permission is hereby granted, free of charge, to any person obtaining 
a copy of this software and associated documentation files (the 
"Software"), to deal in the Software without restriction, including 
without limitation the rights to use, copy, modify, merge, publish, 
distribute, sublicense, and/or sell copies of the Software, and to 
permit persons to whom the Software is furnished to do so, subject 
to the following conditions:

The above copyright notice and this permission notice shall be 
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR 
ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

(MIT license, http://www.opensource.org/licenses/mit-license.html)
*/

/**
 * Action for the Edit -> Find/Replace menu item
 */
public class EditFindReplaceAction extends GuiAction {
	FindReplaceDialog findReplaceDialog = null;

	public EditFindReplaceAction(String name, Icon icon, String descrip,
			Integer mnemonic, KeyStroke accel, VenusUI gui) {
		super(name, icon, descrip, mnemonic, accel, gui);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (findReplaceDialog == null) {
			// Lazy instantiation, pseudo-singleton
			findReplaceDialog = new FindReplaceDialog();
		}
		findReplaceDialog.updateInputText();
		findReplaceDialog.setVisible(true);
	}

	//////////////////////////////////////////////////////////////////////////////
	//
	// Private class to do all the work!
	//
	private class FindReplaceDialog extends JDialog {
		JButton findButton, replaceButton, replaceAllButton, closeButton;
		JTextField findInputField, replaceInputField;
		JCheckBox caseSensitiveCheckBox;
		JRadioButton linearFromStart, circularFromCursor;
		private JLabel resultsLabel;
		
		private String searchString = "";
		private boolean caseSensitivity = false;
		
		private final Color darkRed = Color.RED.darker();
		private final Color darkGreen = Color.GREEN.darker();

		private static final String DIALOG_TITLE = "Find and Replace";
		
		public static final String FIND_TOOL_TIP_TEXT = "Find next occurrence of given text; wraps around at end";
		public static final String REPLACE_TOOL_TIP_TEXT = "Replace current occurrence of text then find next";
		public static final String REPLACE_ALL_TOOL_TIP_TEXT = "Replace all occurrences of text";
		public static final String CLOSE_TOOL_TIP_TEXT = "Close the dialog";
		public static final String RESULTS_TOOL_TIP_TEXT = "Outcome of latest operation (button click)";

		public static final String RESULTS_TEXT_FOUND = "Text found";
		public static final String RESULTS_TEXT_NOT_FOUND = "Text not found";
		public static final String RESULTS_TEXT_REPLACED = "Text replaced and found next";
		public static final String RESULTS_TEXT_REPLACED_LAST = "Text replaced; last occurrence";
		public static final String RESULTS_TEXT_REPLACED_ALL = "Replaced";
		public static final String RESULTS_NO_TEXT_TO_FIND = "No text to find";

		private FindReplaceDialog(Frame owner, String title, boolean modality) {
			super(owner, title, modality);
			this.setContentPane(buildDialogPanel());
			this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			this.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent we) {
					performClose();
				}
			});
			this.pack();
			this.setLocationRelativeTo(owner);
		}
		
		public FindReplaceDialog() {
			this(Globals.getGui(), DIALOG_TITLE, false);
		}

		/**
		 * Constructs the dialog's main panel.
		 */
		private JPanel buildDialogPanel() {
			JPanel dialogPanel = new JPanel(new BorderLayout());
			dialogPanel.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));
			dialogPanel.add(buildInputPanel(), BorderLayout.NORTH);
			dialogPanel.add(buildOptionsPanel());
			dialogPanel.add(buildControlPanel(), BorderLayout.SOUTH);
			return dialogPanel;
		}

		/**
		 * Constructs the top part of the dialog, containing the two input text fields.
		 */
		private Component buildInputPanel() {
			JPanel inputPanel = new JPanel();
			Box columns = Box.createHorizontalBox();
			
			JPanel labelsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
			labelsPanel.add(new JLabel("Find what:"));
			labelsPanel.add(new JLabel("Replace with:"));
			
			JPanel fieldsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
			
			findInputField = new JTextField(30);
			// Perform find on Enter
			findInputField.addActionListener(e -> performFind());
			
			replaceInputField = new JTextField(30);
			
			fieldsPanel.add(findInputField);
			fieldsPanel.add(replaceInputField);

			columns.add(labelsPanel);
			columns.add(Box.createHorizontalStrut(6));
			columns.add(fieldsPanel);
			inputPanel.add(columns);
			return inputPanel;
		}

		/**
		 * Constructs the center part of the dialog, which contains the
		 * check box for case sensitivity along with a label to display
		 * the outcome of each operation.
		 */	
		private Component buildOptionsPanel() {
			Box optionsPanel = Box.createHorizontalBox();
			
			JPanel casePanel = new JPanel(new GridLayout(2, 1));
			caseSensitiveCheckBox = new JCheckBox("Case Sensitive", caseSensitivity);
			casePanel.add(caseSensitiveCheckBox);
			casePanel.setMaximumSize(casePanel.getPreferredSize());
			
			JPanel resultsPanel = new JPanel(new GridLayout(1, 1));
			resultsPanel.setBorder(BorderFactory.createTitledBorder("Outcome"));
			resultsLabel = new JLabel("");
			resultsLabel.setForeground(darkRed);
			resultsLabel.setToolTipText(RESULTS_TOOL_TIP_TEXT);
			resultsPanel.add(resultsLabel);
			
			optionsPanel.add(casePanel);
			optionsPanel.add(Box.createHorizontalStrut(5));
			optionsPanel.add(resultsPanel);
			return optionsPanel;
		}

		/**
		 * Constructs the row of control buttons to be placed along the bottom
		 * of the dialog
		 */
		private Component buildControlPanel() {
			Box controlPanel = Box.createHorizontalBox();
			controlPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
			
			findButton = new JButton("Find");
			findButton.setToolTipText(FIND_TOOL_TIP_TEXT);
			findButton.addActionListener(e -> performFind());
			
			replaceButton = new JButton("Replace then Find");
			replaceButton.setToolTipText(REPLACE_TOOL_TIP_TEXT);
			replaceButton.addActionListener(e -> performReplace());
			
			replaceAllButton = new JButton("Replace all");
			replaceAllButton.setToolTipText(REPLACE_ALL_TOOL_TIP_TEXT);
			replaceAllButton.addActionListener(e -> performReplaceAll());
			
			closeButton = new JButton("Close");
			closeButton.setToolTipText(CLOSE_TOOL_TIP_TEXT);
			closeButton.addActionListener(e -> performClose());
			
			controlPanel.add(Box.createHorizontalGlue());
			controlPanel.add(findButton);
			controlPanel.add(Box.createHorizontalGlue());
			controlPanel.add(replaceButton);
			controlPanel.add(Box.createHorizontalGlue());
			controlPanel.add(replaceAllButton);
			controlPanel.add(Box.createHorizontalGlue());
			controlPanel.add(closeButton);
			controlPanel.add(Box.createHorizontalGlue());
			return controlPanel;
		}
		
		/**
		 * Sets input field to selected text, if something is selected.
		 * Else sets it to last searched string
		 */
		public void updateInputText() {
			String selection = mainUI.getMainPane().getEditPane().getTextArea().getSelectedText(); 
			if (selection != null) {
				findInputField.setText(selection);
			}
			else if (!searchString.isEmpty()) {
				findInputField.setText(searchString);
			}
			findInputField.selectAll();
		}

		////////////////////////////////////////////////////////////////////////
		//
		// Private methods to carry out the button actions

		// Performs a find. The operation starts at the current cursor position
		// which is not known to this object but is maintained by the EditPane
		// object. The operation will wrap around when it reaches the end of the
		// document. If found, the matching text is selected.
		private void performFind() {
			resultsLabel.setText("");
			if (findInputField.getText().length() > 0) {
				EditPane editPane = mainUI.getMainPane().getEditPane();
				// Being cautious.
				// Should not be null because find/replace tool button disabled if no file open
				if (editPane != null) {
					searchString = findInputField.getText();
					int posn = editPane.doFindText(searchString, caseSensitiveCheckBox.isSelected());
					if (posn == MARSTextEditingArea.TEXT_NOT_FOUND) {
						resultsLabel.setForeground(darkRed);
						resultsLabel.setText(findButton.getText() + ": " + RESULTS_TEXT_NOT_FOUND);
					}
					else {
						resultsLabel.setForeground(darkGreen);
						resultsLabel.setText(findButton.getText() + ": " + RESULTS_TEXT_FOUND);
					}
				}
			}
			else {
				resultsLabel.setForeground(darkRed);
				resultsLabel.setText(findButton.getText() + ": " + RESULTS_NO_TEXT_TO_FIND);
			}
		}

		// Performs a replace-and-find. If the matched text is current selected with
		// cursor at its end, the replace happens immediately followed by a find
		// for the next occurrence. Otherwise, it performs a find.
		// This will select the matching text so the next
		// press of Replace will do the replace. This is apparently common behavior for
		// replace buttons of different apps I've checked.
		private void performReplace() {
			resultsLabel.setText("");
			if (findInputField.getText().length() > 0) {
				EditPane editPane = mainUI.getMainPane().getEditPane();
				// Being cautious.
				// Should not be null b/c find/replace tool button disabled if no file open
				if (editPane != null) {
					searchString = findInputField.getText();
					int posn = editPane.doReplace(searchString, replaceInputField.getText(),
							caseSensitiveCheckBox.isSelected());
					String result = replaceButton.getText() + ": ";
					switch (posn) {

						case MARSTextEditingArea.TEXT_NOT_FOUND:
							resultsLabel.setForeground(darkRed);
							result += RESULTS_TEXT_NOT_FOUND;
							break;
						case MARSTextEditingArea.TEXT_FOUND:
							resultsLabel.setForeground(darkGreen);
							result += RESULTS_TEXT_FOUND;
							break;
						case MARSTextEditingArea.TEXT_REPLACED_NOT_FOUND_NEXT:
							resultsLabel.setForeground(darkGreen);
							result += RESULTS_TEXT_REPLACED_LAST;
							break;
						case MARSTextEditingArea.TEXT_REPLACED_FOUND_NEXT:
							resultsLabel.setForeground(darkGreen);
							result += RESULTS_TEXT_REPLACED;
							break;
					}
					resultsLabel.setText(result);
				}
			}
			else {
				resultsLabel.setForeground(darkRed);
				resultsLabel.setText(replaceButton.getText() + ": " + RESULTS_NO_TEXT_TO_FIND);
			}

		}

		// Performs a replace-all. Makes one pass through the document starting at
		// position 0.
		private void performReplaceAll() {
			resultsLabel.setText("");
			if (findInputField.getText().length() > 0) {
				EditPane editPane = mainUI.getMainPane().getEditPane();
				// Being cautious.
				// Should not be null b/c find/replace tool button disabled if no file open
				if (editPane != null) {
					searchString = findInputField.getText();
					int replaceCount = editPane.doReplaceAll(searchString, replaceInputField.getText(),
							caseSensitiveCheckBox.isSelected());
					if (replaceCount == 0) {
						resultsLabel.setForeground(darkRed);
						resultsLabel.setText(replaceAllButton.getText() + ": " + RESULTS_TEXT_NOT_FOUND);
					}
					else {
						resultsLabel.setForeground(darkGreen);
						resultsLabel.setText(replaceAllButton.getText() + ": " + RESULTS_TEXT_REPLACED_ALL + " "
								+ replaceCount + " occurrence" + (replaceCount == 1 ? "" : "s"));
					}
				}
			}
			else {
				resultsLabel.setForeground(darkRed);
				resultsLabel.setText(replaceAllButton.getText() + ": " + RESULTS_NO_TEXT_TO_FIND);
			}
		}

		// Performs the close operation. Records the current state of the
		// case-sensitivity checkbox into a static variable so it will be
		// remembered across invocations within the session.
		// This also happens with the contents of the "find" text field.
		private void performClose() {
			caseSensitivity = caseSensitiveCheckBox.isSelected();
			this.setVisible(false);
			this.dispose();
		}
		//
		////////////////////////////////////////////////////////////////////////////////
	}

}
