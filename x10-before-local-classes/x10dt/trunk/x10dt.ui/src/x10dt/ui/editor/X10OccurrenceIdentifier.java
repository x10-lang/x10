package x10dt.ui.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IOccurrenceMarker;

import polyglot.ast.Call;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.Id;
import polyglot.ast.NamedVariable;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.TypeNode;
import polyglot.ast.VarDecl;
import polyglot.types.CodeInstance;
import polyglot.types.ConstructorDef;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.LocalDef;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.VarInstance;
import polyglot.visit.NodeVisitor;
import x10dt.ui.parser.PolyglotNodeLocator;

public class X10OccurrenceIdentifier implements ILanguageService, IOccurrenceMarker {
    private List<Object> fOccurrences= Collections.emptyList();

    public String getKindName() {
        return "X10 Occurrence Marker";
    }

    public List<Object> getOccurrencesOf(IParseController parseController, Object node) {
        if (node == null) {
            return Collections.emptyList();
        }

        // Check whether we even have an AST in which to find occurrences
        Node root= (Node) parseController.getCurrentAst();

        if (root == null) {
            return Collections.emptyList();
        }

        Node astNode= (Node) node;
        PolyglotNodeLocator locator= (PolyglotNodeLocator) parseController.getSourcePositionLocator();

        fOccurrences= new ArrayList<Object>();

        if (astNode instanceof Id) {
            Node parent= (Node) locator.getParentNodeOf(astNode, root);
            astNode= parent;
        } else if (astNode instanceof TypeNode) {
            Node parent= (Node) locator.getParentNodeOf(astNode, root);
            if (parent instanceof New || parent instanceof ProcedureDecl) {
                astNode= parent;
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
                if (theVarInstance[0] != null) {
                    VarInstance vi= getVarInstance(n);

                    if (vi != null && theVarInstance[0].equals(vi)) {
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
        if (n instanceof Field) {
            return ((Field) n).fieldInstance().def().asInstance();
        } else if (n instanceof NamedVariable) {
            return ((NamedVariable) n).varInstance();
        } else if (n instanceof FieldDecl) {     	
            FieldDecl fieldDecl = (FieldDecl) n;
            FieldDef fieldDef= fieldDecl.fieldDef();
            // TODO Remove the following null check once the compiler produces "bogus"/synthetic FieldDefs for dangling refs
            if (fieldDef == null) {
                return null;
            }
            FieldInstance fi = fieldDef.asInstance();
			return fi;
        } else if (n instanceof VarDecl) {
            VarDecl varDecl = (VarDecl) n;
            LocalDef varDef= varDecl.localDef();
            // TODO Remove the following null check once the compiler produces "bogus"/synthetic LocalDefs for dangling refs
            if (varDef == null) {
                return null;
            }
            VarInstance vi = varDef.asInstance();
			return vi;
        }
        return null;
    }

    private ProcedureInstance getProcInstance(Node n) {
        // In what follows, take care not to use the ProcedureInstance that one gets from various
        // sources as is. E.g., the ProcedureInstance associated with a method call site may have
        // additional type constraints beyond that of the decl. Instead, get the underlying
        // ProcedureDef first, and ask *that* for its ProcedureInstance.
        if (n instanceof ProcedureDecl) {
            ProcedureDecl pDecl = (ProcedureDecl) n;
            ProcedureInstance pi = (ProcedureInstance) ((CodeInstance) pDecl.procedureInstance().asInstance());

            return pi;
        } else if (n instanceof Call) {
            return (ProcedureInstance) ((ProcedureDef) ((Call) n).procedureInstance().def()).asInstance();
        } else if (n instanceof ConstructorCall) {
            return ((ConstructorDef) ((ConstructorCall) n).procedureInstance().def()).asInstance();
        } else if (n instanceof New) {
            return ((ConstructorDef) ((New) n).procedureInstance().def()).asInstance();
        }
        return null;
    }
}
