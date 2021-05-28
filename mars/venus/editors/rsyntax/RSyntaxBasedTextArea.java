package mars.venus.editors.rsyntax;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.ScrollPaneConstants;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

import mars.Globals;
import mars.venus.EditPane;
import mars.venus.editors.MARSTextEditingArea;

/*
Copyright (c) 2021, Valerio Colella (colella.1951557@studenti.uniroma1.it)

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
 * Adapter subclass for RSyntaxTextArea
 *
 * Provides those methods required by the MARSTextEditingArea interface that are
 * not defined by RSyntaxTextArea. This permits RSyntaxTextArea to be used within
 * MARS largely without modification.
 *
 * @author Valerio Colella
 * @version May 2021
 */
public class RSyntaxBasedTextArea extends RSyntaxTextArea implements MARSTextEditingArea{

	private EditPane editPane;
	private UndoManager undoManager;
	private UndoableEditListener undoableEditListener;
	private boolean isCompoundEdit = false;
	private CompoundEdit compoundEdit;
	private RTextScrollPane editAreaScrollPane;
	
	public RSyntaxBasedTextArea(EditPane editPain, boolean lineNumbers) {
		super();
		
		this.editPane = editPain;
		this.undoManager = new UndoManager();
		
		// Syntax highlighting
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("text/mips", "mars.venus.editors.rsyntax.MIPSTokenMaker");
		this.setSyntaxEditingStyle("text/mips");
		
		this.setFont(Globals.getSettings().getEditorFont());
		this.setTabSize(Globals.getSettings().getEditorTabSize());
		this.setMargin(new Insets(0, 3, 3, 3));
		this.setCaretBlinkRate(Globals.getSettings().getCaretBlinkRate());
		this.setCurrentLineHighlightColor(new Color(0xE8F2FE)); // Light blue, same as Eclipse
		
		editAreaScrollPane = new RTextScrollPane(this);
		//JScrollPane editAreaScrollPane = new JScrollPane(source);
		editAreaScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		editAreaScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		editAreaScrollPane.setLineNumbersEnabled(lineNumbers);
		
		this.getCaret().addChangeListener(e -> editPane.displayCaretPosition(getCaretPosition()));
		
		// Needed to support unlimited undo/redo capability
		undoableEditListener = e -> {
			// Remember the edit and update the menus.
			if (isCompoundEdit) {
				compoundEdit.addEdit(e.getEdit());
			}
			else {
				undoManager.addEdit(e.getEdit());
				editPane.updateUndoState();
				editPane.updateRedoState();
			}
		};
		this.getDocument().addUndoableEditListener(undoableEditListener);
	}

	/**
	 * For initializing the source code when opening an ASM file
	 * 
	 * @param code        String containing text
	 * @param editable true if code is editable else false
	 */
	@Override
	public void setSourceCode(String code, boolean editable) {
		this.setText(code);
		this.setEditable(editable);
		this.setEnabled(editable);
		this.setCaretPosition(0);
		if (editable)
			this.requestFocusInWindow();
	}
	
	/**
	 * Returns the undo manager for this editing area
	 * 
	 * @return the undo manager
	 */
	@Override
	public UndoManager getUndoManager() {
		return undoManager;
	}
	
	/**
	 * Undo previous edit
	 */
	@Override
	public void undo() {
		undoLastAction();
	}
	
	/**
	 * Redo previous edit
	 */
	@Override
	public void redo() {
		redoLastAction();
	}
	
	/**
	 * Get rid of any accumulated undoable edits. It is useful to call this method
	 * after opening a file into the text area. The act of setting its text content
	 * upon reading the file will generate an undoable edit. Normally you don't want
	 * a freshly-opened file to appear with its Undo action enabled. But it will
	 * unless you call this after setting the text.
	 */
	@Override
	public void discardAllUndoableEdits() {
		discardAllEdits();
	}
	
	/**
	 * Finds next occurrence of text in a forward search of a string. Search begins
	 * at the current cursor location, and wraps around when the end of the string
	 * is reached.
	 * 
	 * @param find          the text to locate in the string
	 * @param caseSensitive true if search is to be case-sensitive, false otherwise
	 * @return TEXT_FOUND or TEXT_NOT_FOUND, depending on the result.
	 */
	@Override
	public int doFindText(String find, boolean caseSensitive) {
		SearchContext context = new SearchContext(find, caseSensitive);
		context.setSearchWrap(true);
		return SearchEngine.find(this, context).wasFound() ? TEXT_FOUND : TEXT_NOT_FOUND ;
	}

	@Override
	public int doReplace(String find, String replace, boolean caseSensitive) {
		return 0;
	}

	/**
	 * Finds and replaces <B>ALL</B> occurrences of text in a string in a forward search.
	 * All replacements are bundled into one CompoundEdit, so one Undo operation
	 * will undo all of them.
	 * 
	 * @param find          the text to locate in the string
	 * @param replace       the text to replace the find text with - if the find
	 *                      text exists
	 * @param caseSensitive true for case sensitive. false to ignore case
	 * @return the number of occurrences that were matched and replaced.
	 */
	@Override
	public int doReplaceAll(String find, String replace, boolean caseSensitive) {
		SearchContext context = new SearchContext(find, caseSensitive);
		context.setReplaceWith(replace);
		return SearchEngine.replaceAll(this, context).getCount();
	}

	/**
	 * Control caret visibility
	 *
	 * @param vis true to display caret, false to hide it
	 */
	@Override
	public void setCaretVisible(boolean vis) {
		getCaret().setVisible(vis);
	}

	/**
	 * Control selection visibility
	 *
	 * @param vis true to display selection, false to hide it
	 */
	@Override
	public void setSelectionVisible(boolean vis) {
		getCaret().setSelectionVisible(vis);
	}

	/**
	 * Use for highlighting the line currently being edited.
	 * 
	 * @param highlight true to enable line highlighting, false to disable.
	 */
	@Override
	public void setLineHighlightEnabled(boolean highlight) {
		setHighlightCurrentLine(highlight);
	}

	/**
	 * Set the caret blinking rate in milliseconds. If rate is 0 it will not blink.
	 * If negative, do nothing.
	 * 
	 * @param rate blinking rate in milliseconds
	 */
	@Override
	public void setCaretBlinkRate(int rate) {
		if (rate > 0) {
			getCaret().setBlinkRate(rate);
		}
	}

	@Override
	public void updateSyntaxStyles() {
	}

	@Override
	public Component getOuterComponent() {
		return editAreaScrollPane;
	}

	@Override
	public void toggleComment() {
	}
}
