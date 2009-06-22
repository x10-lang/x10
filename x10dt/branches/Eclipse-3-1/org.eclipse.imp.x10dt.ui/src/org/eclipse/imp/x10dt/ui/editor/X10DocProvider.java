/*
 * Created on Mar 8, 2007
 */
package org.eclipse.imp.x10dt.ui.editor;

import lpg.runtime.IToken;

import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IDocumentationProvider;
import org.eclipse.imp.utils.HTMLPrinter;

import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.types.Declaration;
import polyglot.util.Position;
import x10.parser.X10Parser.JPGPosition;

public class X10DocProvider implements IDocumentationProvider, ILanguageService {

    public String getDocumentation(Object target, IParseController parseController) {
	StringBuffer buff= new StringBuffer();
	Position pos= null;

	HTMLPrinter.addSmallHeader(buff, target.toString());

	if (target instanceof Node) {
       	    Node targetNode= (Node) target;
       	    pos= targetNode.position();
       	} else if (target instanceof Declaration) {
       	    Declaration decl= (Declaration) target;
       	    pos= decl.position();
       	} else if (target instanceof Id) {
       	    Id id= (Id) target;
       	    pos= id.position();
       	}

       	if (pos instanceof JPGPosition) {
       	    IToken leftToken= ((JPGPosition) pos).getLeftIToken();
       	    IToken[] adjuncts= leftToken.getPrecedingAdjuncts();

       	    for(int i= 0; i < adjuncts.length; i++) {
       		String s= adjuncts[i].toString();
       		if (s.startsWith("/*")) {
       		    int openingFenceEnd= s.startsWith("/**") ? 3 : 2;
       		    int endingFenceStart= s.length() - (s.startsWith("//") ? 0 : (s.endsWith("**/") ? 3 : 2));

       		    HTMLPrinter.addParagraph(buff, s.substring(openingFenceEnd, endingFenceStart));
       		}
       	    }
       	}
       	// How to get JavaDoc corresponding to classes residing in jar files?
	return buff.toString();
    }
}
