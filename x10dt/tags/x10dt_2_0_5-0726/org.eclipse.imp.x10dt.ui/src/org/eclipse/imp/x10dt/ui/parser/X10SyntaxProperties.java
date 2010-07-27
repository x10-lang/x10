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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        return "*";
    }

    public String[][] getFences() {
        return new String[][] { { "{", "}" }, { "(", ")"}, { "[", "]"}, {"<", ">"}, { "\"", "\"" } };
    }

    public int[] getIdentifierComponents(String ident) {
        List<Integer> wordStarts= new ArrayList<Integer>();

        wordStarts.add(new Integer(0));
        for(int i=1; i < ident.length(); i++) {
            if (Character.isLowerCase(ident.charAt(i-1)) && Character.isUpperCase(ident.charAt(i))) {
                wordStarts.add(new Integer(i));
            }
        }

        int[] result= new int[wordStarts.size()];
        int idx= 0;
        for(Iterator<Integer> iterator= wordStarts.iterator(); iterator.hasNext(); idx++) {
            result[idx]= iterator.next();
        }
        return result;
    }

    public String getIdentifierConstituentChars() {
        return "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_$";
    }
}
