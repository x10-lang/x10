package org.x10.ui;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.uide.defaults.DefaultTokenColorer;
import org.eclipse.uide.editor.ITokenColorer;
import org.eclipse.uide.parser.IParseController;
 
import x10.parser.X10Parsersym;

import com.ibm.lpg.IToken;

public class MyTokenColorer extends DefaultTokenColorer implements X10Parsersym, ITokenColorer {

	TextAttribute characterAttribute, numberAttribute, identifierAttribute;
	
	public TextAttribute getColoring(IParseController controller, IToken token) {
		switch (token.getKind()) {
		case TK_IDENTIFIER:
			return identifierAttribute;
		case TK_DoubleLiteral:
		case TK_FloatingPointLiteral:
		case TK_IntegerLiteral:
		case TK_LongLiteral:
			return numberAttribute;
		case TK_CharacterLiteral:
		case TK_StringLiteral:
			return characterAttribute;
		default:
			return null;
		}
	}

	public MyTokenColorer() {
		super();
        Display display = Display.getDefault();
		characterAttribute = new TextAttribute(display.getSystemColor(SWT.COLOR_RED), null, SWT.BOLD); 		
		identifierAttribute = new TextAttribute(display.getSystemColor(SWT.COLOR_BLUE), null, SWT.BOLD); 		
		numberAttribute = new TextAttribute(display.getSystemColor(SWT.COLOR_YELLOW), null, SWT.BOLD); 		
	}

	public void setLanguage(String language) {
	}
}
