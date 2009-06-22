package org.eclipse.imp.x10dt.ui.parser;

import org.eclipse.imp.language.ILanguageSyntaxProperties;

public class X10SyntaxProperties implements ILanguageSyntaxProperties {

    public String getBlockCommentEnd() {
        return "*/";
    }

    public String getBlockCommentStart() {
        return "/*";
    }

    public String getSingleLineCommentPrefix() {
        return "//";
    }

    public String getBlockCommentContinuation() {
        // TODO Auto-generated method stub
        return null;
    }

    public String[][] getFences() {
        // TODO Auto-generated method stub
        return null;
    }

    public int[] getIdentifierComponents(String ident) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getIdentifierConstituentChars() {
        // TODO Auto-generated method stub
        return null;
    }
}
