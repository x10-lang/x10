package org.eclipse.imp.x10dt.ui.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import polyglot.ast.*;
import polyglot.types.CodeInstance;
import polyglot.types.FieldInstance;
import polyglot.types.LocalInstance;
import polyglot.types.ProcedureInstance;
import polyglot.types.VarInstance;
import polyglot.visit.NodeVisitor;

import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IOccurrenceMarker;
import org.eclipse.imp.x10dt.ui.parser.PolyglotNodeLocator;

public class X10OccurrenceIdentifier implements ILanguageService, IOccurrenceMarker {
    //public class OccurrenceMarker implements ILanguageService, IOccurrenceMarker {

    private List fOccurrences= Collections.EMPTY_LIST;

    public String getKindName() {
        return "X10 Occurrence Marker";
    }

    public List<Object> getOccurrencesOf(IParseController parseController, Object node) {
        if (node == null) {
            return Collections.EMPTY_LIST;
        }

        // Check whether we even have an AST in which to find occurrences
        Node root= (Node) parseController.getCurrentAst();

        if (root == null) {
            return Collections.EMPTY_LIST;
        }

        Node astNode= (Node) node;
        PolyglotNodeLocator locator= (PolyglotNodeLocator) parseController.getNodeLocator();

        fOccurrences= new ArrayList();

        if (node instanceof Id) {
            node= locator.getParentNodeOf(node, root);
            astNode= (Node) node;
        } else if (node instanceof TypeNode) {
            Node parent= (Node) locator.getParentNodeOf(node, root);
            if (parent instanceof New) {
                node= parent;
                astNode= (Node) node;
            }
        }

        final VarInstance[] theVarInstance= new VarInstance[1];
        final ProcedureInstance[] theProcInstance= new ProcedureInstance[1];

        theVarInstance[0]= getVarInstance(astNode);
        theProcInstance[0]= getProcInstance(astNode);

        if (theVarInstance[0] == null && theProcInstance[0] == null) {
            return fOccurrences;
        }

        root.visit(new NodeVisitor() {
            @Override
            public NodeVisitor enter(Node n) {
                VarInstance vi= getVarInstance(n);

                if (theVarInstance[0] != null && vi != null) {
                    if (theVarInstance[0].equals(vi)) {
                        fOccurrences.add(n);
                    }
                } else if (theProcInstance[0] != null) {
                    ProcedureInstance pi= getProcInstance(n);
                    if (pi != null && theProcInstance[0].equals(pi)) {
                        fOccurrences.add(n);
                    }
                }
                return super.enter(n);
            }
        });
        return fOccurrences;
    }

    private VarInstance getVarInstance(Node n) {
        if (n instanceof NamedVariable) {
            return ((NamedVariable) n).varInstance();
        } else if (n instanceof FieldDecl) {     	
            FieldDecl fieldDecl = (FieldDecl) n;
            FieldInstance fi = fieldDecl.fieldDef().asInstance();//PORT1.7 was fieldDecl.varInstance()
			return fi;
        } else if (n instanceof VarDecl) {
            VarDecl varDecl = (VarDecl) n;
            VarInstance vi = varDecl.localDef().asInstance();//PORT1.7 was varDecl.varInstance()
			return vi;
        }
        return null;
    }

    private ProcedureInstance getProcInstance(Node n) {
        if (n instanceof ProcedureDecl) {  	
            ProcedureDecl pDecl = (ProcedureDecl) n;           
            CodeInstance ci = pDecl.procedureInstance().asInstance();//PORT1.7 was procedureDecl.procedureInstance()
            ProcedureInstance pi = (ProcedureInstance)ci; //PORT1.7  cast should succeed b/c ProcedureDef.asInstance() always returns a ProcedureInstance?
			return pi;//PORT1.7 was procedureDecl.procedureInstance();
        } else if (n instanceof Call) {
            return ((Call) n).procedureInstance();
        } else if (n instanceof ConstructorCall) {
            return ((ConstructorCall) n).procedureInstance();
        } else if (n instanceof New) {
            return ((New) n).procedureInstance();
        }
        return null;
    }
}
