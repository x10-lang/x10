/**
 * 
 */
package org.eclipse.imp.x10dt.core.builder;

import java.util.Iterator;
import polyglot.ast.ClassDecl;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Receiver;
import polyglot.ast.SourceFile;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.NullableType;
import polyglot.frontend.Job;
import polyglot.types.ArrayType;
import polyglot.types.ClassType;
import polyglot.types.ParsedClassType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.NodeVisitor;

/**
 * A Polyglot visitor that scans for type reference information in order to
 * build a map of compilation unit-level dependencies to be used in compilation.
 * @author rfuhrer
 */
class ComputeDependenciesVisitor extends NodeVisitor {
    private final Job fJob;
    private final TypeSystem fTypeSystem;
    private SourceFile fFromFile;
    private Type fFromType;
    private final PolyglotDependencyInfo fDependencyInfo;

    private static boolean DEBUG= false;

    public ComputeDependenciesVisitor(Job job, TypeSystem ts, PolyglotDependencyInfo di) {
        fJob= job;
        fTypeSystem= ts;
        fDependencyInfo= di;
    }
    private void recordTypeDependency(Type type) {
        if (type.isArray()) {
            ArrayType arrayType= (ArrayType) type;
            type= arrayType.base();
        }
        if (type.isClass()) {
            if (type instanceof NullableType)
        	type = ((NullableType) type).base();
            ClassType classType= (ClassType) type;
            if (!isBinary(classType) && !fFromType.equals(type)) {
        	if (DEBUG)
        	    System.out.println("  Reference to type: " + classType.fullName());
        	fDependencyInfo.addDependency(fFromType, type);
            }
        }
    }
    private boolean isBinary(ClassType classType) {
        // HACK for some reason ParsedClassType's show up even for class files(???)...
        return !(classType instanceof ParsedClassType) || classType.position().file().endsWith(".class");
    }
    public NodeVisitor enter(Node n) {
        if (n instanceof SourceFile) {
            fFromFile= (SourceFile) n;
            if (DEBUG)
        	System.out.println("Scanning file " + fFromFile.position() + " for dependencies.");
        } else if (n instanceof TypeNode) {
            TypeNode typeNode= (TypeNode) n;
            Type type= typeNode.type();

            recordTypeDependency(type);
        } else if (n instanceof Field) {
            Field field= (Field) n;
            Receiver rcvr= field.target();
            Type type= rcvr.type();

            recordTypeDependency(type);
        } else if (n instanceof ClassDecl) {
            ClassDecl classDecl= (ClassDecl) n;

            fFromType= classDecl.type();
            if (classDecl.superClass() != null) // interfaces have no superclass
        	recordTypeDependency(classDecl.superClass().type());
            for(Iterator intfs= classDecl.interfaces().iterator(); intfs.hasNext(); ) {
        	TypeNode typeNode= (TypeNode) intfs.next();

        	recordTypeDependency(typeNode.type());
            }
        } else if (n instanceof FieldDecl) {
            FieldDecl fieldDecl= (FieldDecl) n;

            recordTypeDependency(fieldDecl.type().type());
        } else if (n instanceof ProcedureDecl) {
            ProcedureDecl procedureDecl= (ProcedureDecl) n;

            for(Iterator iter= procedureDecl.formals().iterator(); iter.hasNext();) {
        	Formal formal= (Formal) iter.next();

        	recordTypeDependency(formal.type().type());
            }
        }
        return super.enter(n);
    }
}
