package x10dt.ui.editor;

import org.eclipse.imp.services.IEntityNameLocator;

import polyglot.ast.ClassDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.MethodDecl;

public class X10EntityNameLocator implements IEntityNameLocator {

    public Object getName(Object srcEntity) {
        if (srcEntity instanceof MethodDecl) {
            return ((MethodDecl) srcEntity).name();
        } else if (srcEntity instanceof FieldDecl) {
            return ((FieldDecl) srcEntity).name();
        } else if (srcEntity instanceof ClassDecl) {
            return ((ClassDecl) srcEntity).name();
        }
        return null;
    }
}
