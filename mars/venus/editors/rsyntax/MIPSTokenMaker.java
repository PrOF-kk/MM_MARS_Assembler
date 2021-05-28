package mars.venus.editors.rsyntax;

import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMaker;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import mars.assembler.Directives;
import mars.mips.instructions.InstructionSet;
import mars.venus.editors.jeditsyntax.tokenmarker.MIPSTokenMarker;

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
 * MIPS Token Maker, for use with RSyntaxTextArea<br>
 * Not to be confused with {@link MIPSTokenMarker}
 * 
 * @author Valerio Colella
 * @version May 2021
 */
public class MIPSTokenMaker extends AbstractTokenMaker implements TokenMaker {
	
	/**
	 * Returns the words to highlight for MIPS programs.
	 *
	 * @return A <code>TokenMap</code> containing the words to highlight for
	 *         MIPS programs.
	 * @see org.fife.ui.rsyntaxtextarea.AbstractTokenMaker#getWordsToHighlight
	 */
	@Override
	public TokenMap getWordsToHighlight() {
		
		TokenMap tokenMap = new TokenMap();
		
		InstructionSet iSet = new InstructionSet();
		iSet.populate();
		iSet.getInstructionList().forEach(instr -> tokenMap.put(instr.getName(), Token.RESERVED_WORD));
		
		Directives.getDirectiveList().forEach(dir -> tokenMap.put(dir.getName(), Token.COMMENT_DOCUMENTATION));
		
		return tokenMap;
	}

	@Override
	public void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {
		// This assumes all keywords, etc. were parsed as "identifiers."
		// Registers are highlighted on their own
		if (tokenType == Token.IDENTIFIER) {
			int value = wordsToHighlight.get(segment, start, end);
			if (value != -1) {
				tokenType = value;
			}
		}
		super.addToken(segment, start, end, tokenType, startOffset);
	}
	
	/**
	 * Returns a list of tokens representing the given text.
	 *
	 * @param text           The text to break into tokens.
	 * @param startTokenType The token with which to start tokenizing.
	 * @param startOffset    The offset at which the line of tokens begins.
	 * @return A linked list of tokens representing <code>text</code>.
	 */
	@Override
	public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
		resetTokenList();

		char[] array = text.array;
		int offset = text.offset;
		int count = text.count;
		int end = offset + count;

		// Token starting offsets are always of the form:
		// 'startOffset + (currentTokenStart-offset)', but since startOffset and
		// offset are constant, tokens' starting positions become:
		// 'newStartOffset+currentTokenStart'.
		int newStartOffset = startOffset - offset;

		int currentTokenStart = offset;
		int currentTokenType = initialTokenType;

		for (int i = offset; i < end; i++) {

			char c = array[i];

			switch (currentTokenType) {

				case Token.NULL:

					currentTokenStart = i; // Starting a new token here.

					switch (c) {

						case ' ':
						case '\t':
							currentTokenType = Token.WHITESPACE;
							break;

						case '"':
							currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
							break;

						case '#':
							currentTokenType = Token.COMMENT_EOL;
							break;
						
						case '$':
						case '%':
							currentTokenType = Token.VARIABLE;
							break;

						default:
							if (RSyntaxUtilities.isDigit(c)) {
								currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
								break;
							}
							else if (RSyntaxUtilities.isLetter(c) || c == '/' || c == '_') {
								currentTokenType = Token.IDENTIFIER;
								break;
							}

							// Anything not currently handled - mark as an identifier
							currentTokenType = Token.IDENTIFIER;
							break;

					} // End of switch (c).

					break;

				case Token.WHITESPACE:

					switch (c) {

						case ' ':
						case '\t':
							break; // Still whitespace.

						case '"':
							addToken(text, currentTokenStart, i - 1, Token.WHITESPACE,
									newStartOffset + currentTokenStart);
							currentTokenStart = i;
							currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
							break;

						case '#':
							addToken(text, currentTokenStart, i - 1, Token.WHITESPACE,
									newStartOffset + currentTokenStart);
							currentTokenStart = i;
							currentTokenType = Token.COMMENT_EOL;
							break;
						
						case '$':
						case '%':
							currentTokenType = Token.VARIABLE;
							break;

						default: // Add the whitespace token and start anew.

							addToken(text, currentTokenStart, i - 1, Token.WHITESPACE,
									newStartOffset + currentTokenStart);
							currentTokenStart = i;

							if (RSyntaxUtilities.isDigit(c)) {
								currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
								break;
							}
							else if (RSyntaxUtilities.isLetter(c) || c == '/' || c == '_') {
								currentTokenType = Token.IDENTIFIER;
								break;
							}

							// Anything not currently handled - mark as identifier
							currentTokenType = Token.IDENTIFIER;

					} // End of switch (c).

					break;

				default: // Should never happen
				case Token.IDENTIFIER:

					switch (c) {

						case ' ':
						case '\t':
							addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER,
									newStartOffset + currentTokenStart);
							currentTokenStart = i;
							currentTokenType = Token.WHITESPACE;
							break;

						case '"':
							addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER,
									newStartOffset + currentTokenStart);
							currentTokenStart = i;
							currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
							break;
							
						case '$':
						case '%':
							addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER,
									newStartOffset + currentTokenStart);
							currentTokenStart = i;
							currentTokenType = Token.VARIABLE;
							break;
						
						case ':':
							addToken(text, currentTokenStart, i, Token.FUNCTION, newStartOffset + currentTokenStart);
							currentTokenStart = i + 1;
							break;

						default:
							if (RSyntaxUtilities.isLetterOrDigit(c) || c == '/' || c == '_') {
								break; // Still an identifier of some type.
							}
							// Otherwise, we're still an identifier (?).

					} // End of switch (c).

					break;

				case Token.LITERAL_NUMBER_DECIMAL_INT:

					switch (c) {

						case ' ':
						case '\t':
							addToken(text, currentTokenStart, i - 1, Token.LITERAL_NUMBER_DECIMAL_INT,
									newStartOffset + currentTokenStart);
							currentTokenStart = i;
							currentTokenType = Token.WHITESPACE;
							break;

						case '"':
							addToken(text, currentTokenStart, i - 1, Token.LITERAL_NUMBER_DECIMAL_INT,
									newStartOffset + currentTokenStart);
							currentTokenStart = i;
							currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
							break;

						default:

							if (RSyntaxUtilities.isDigit(c)) {
								break; // Still a literal number.
							}

							// Otherwise, remember this was a number and start over.
							addToken(text, currentTokenStart, i - 1, Token.LITERAL_NUMBER_DECIMAL_INT,
									newStartOffset + currentTokenStart);
							i--;
							currentTokenType = Token.NULL;

					} // End of switch (c).

					break;

				case Token.COMMENT_EOL:
					i = end - 1;
					addToken(text, currentTokenStart, i, currentTokenType, newStartOffset + currentTokenStart);
					// We need to set token type to null so at the bottom we don't add one more
					// token.
					currentTokenType = Token.NULL;
					break;

				case Token.LITERAL_STRING_DOUBLE_QUOTE:
					if (c == '"') {
						addToken(text, currentTokenStart, i, Token.LITERAL_STRING_DOUBLE_QUOTE,
								newStartOffset + currentTokenStart);
						currentTokenType = Token.NULL;
					}
					break;
				
				case Token.VARIABLE:
					if (!RSyntaxUtilities.isLetterOrDigit(c)) {
						addToken(text, currentTokenStart, i-1, currentTokenType, newStartOffset + currentTokenStart);
						// Last char after the register might be a parenthesis or
						// a comma, reevaluate it
						i--;
						currentTokenType = Token.NULL;
					}
					

			} // End of switch (currentTokenType).

		} // End of for (int i=offset; i<end; i++).

		if (currentTokenType == Token.NULL) {
			// Do nothing if everything was okay.
			addNullToken();
		}
		else {
			// In MIPS no token types continue to the next line...
			addToken(text, currentTokenStart, end - 1, currentTokenType, newStartOffset + currentTokenStart);
			addNullToken();
		}

		// Return the first token in our linked list.
		return firstToken;
	}

}
