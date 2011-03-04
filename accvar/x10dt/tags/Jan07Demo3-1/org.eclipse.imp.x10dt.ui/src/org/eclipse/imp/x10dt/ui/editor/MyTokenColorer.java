package x10.uide.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.uide.defaults.DefaultTokenColorer;
import org.eclipse.uide.editor.ITokenColorer;
import org.eclipse.uide.parser.IParseController;
 
import x10.parser.X10Parsersym;

import lpg.lpgjavaruntime.IToken;

public class MyTokenColorer extends DefaultTokenColorer implements X10Parsersym, ITokenColorer {

	TextAttribute commentAttribute, keywordAttribute, characterAttribute, numberAttribute, identifierAttribute;
	
	public TextAttribute getColoring(IParseController controller, IToken token) {
		switch (token.getKind())
		{
            case TK_DocComment: case TK_SlComment: case TK_MlComment:
                 return commentAttribute;
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
        	// RMF 10/26/2006 - Avoid AIOOB that happens if we pass error tokens to isKeyword()
                if (token.getKind() < TK_ERROR_TOKEN && controller.isKeyword(token.getKind()))
                     return keywordAttribute;
               else return null;
		}
	}

	public MyTokenColorer() {
		super();
        Display display = Display.getDefault();
		commentAttribute = new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_RED), null, SWT.ITALIC); 		
		characterAttribute = new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_BLUE), null, SWT.BOLD); 		
		identifierAttribute = new TextAttribute(display.getSystemColor(SWT.COLOR_BLACK), null, SWT.NORMAL); 		
		numberAttribute = new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_YELLOW), null, SWT.BOLD); 		
        keywordAttribute = new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_MAGENTA), null, SWT.BOLD); 		
	}

	public void setLanguage(String language) {
	}
}
