/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

package org.eclipse.imp.x10dt.ui.parser;

import org.eclipse.imp.services.ILanguageSyntaxProperties;

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
        return new String[][] { { "{", "}" }, { "(", ")"}, { "[", "]"}, {"<", ">"} };
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
