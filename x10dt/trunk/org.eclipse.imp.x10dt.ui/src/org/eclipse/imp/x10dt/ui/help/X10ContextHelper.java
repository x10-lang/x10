package org.eclipse.imp.x10dt.ui.help;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import lpg.runtime.IToken;

import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.services.IHelpService;
import org.eclipse.imp.x10dt.ui.parser.ParseController;
import org.eclipse.jface.text.IRegion;

import polyglot.ast.Id;
import polyglot.ast.Node;

public class X10ContextHelper implements IHelpService {
    private final static Map<String,String> sKeywordHelp= new HashMap<String, String>();

    {
        sKeywordHelp.put("static", "static: Identifies the given member as a class member (cf. instance member).");
        sKeywordHelp.put("private", "private: Specifies that the given member is only visible to members of the same class.");
        sKeywordHelp.put("async", "async: runs the child statement block in parallel with the statement(s) following the async.");
        sKeywordHelp.put("future", "future: arranges for the child expression to be lazily evaluated; see 'force'.");
    }

    public String getHelp(Object target, IParseController parseController) {
        Node root= (Node) parseController.getCurrentAst();
        if (target instanceof Id) {
            return ((Id) target).id();
        }
        return "This is a " + target.getClass().getCanonicalName();
    }

    public String getHelp(IRegion target, IParseController parseController) {
        ParseController pc= (ParseController) parseController;
        IToken token= pc.getLexer().getLexStream().getPrsStream().getTokenAtCharacter(target.getOffset());

        if (token != null) {
            String tokenStr= token.toString();

            if (sKeywordHelp.containsKey(tokenStr)) {
                return sKeywordHelp.get(tokenStr);
            }
        }
        ISourcePositionLocator nodeLocator= parseController.getNodeLocator();
        Object node= nodeLocator.findNode(parseController.getCurrentAst(), target.getOffset());
        if (node != null) {
            return getHelp(node, parseController);
        }
        return null;
    }
}
