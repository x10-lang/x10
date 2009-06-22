package org.eclipse.imp.x10dt.ui.help;
import java.util.Random;

import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IHelpService;

import polyglot.ast.Id;
import polyglot.ast.Node;

public class X10ContextHelper implements IHelpService {

    public String getHelp(Object target, IParseController parseController) {
        Node root= (Node) parseController.getCurrentAst();
        if (target instanceof Id) {
            return ((Id) target).id();
        }
        return "This is a " + target.getClass().getCanonicalName();
    }

}
