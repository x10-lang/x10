/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.visit;

import java.io.File;
import java.io.IOException;
import java.util.*;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.frontend.TargetFactory;
import polyglot.types.*;
import polyglot.types.Package;
import polyglot.util.*;

/**
 * A Translator generates output code from the processed AST.
 * Output is sent to one or more java file in the directory
 * <code>Options.output_directory</code>.  Each SourceFile in the AST
 * is output to exactly one java file.  The name of that file is
 * determined as follows:
 * <ul>
 * <li> If the SourceFile has a declaration of a public top-level class "C",
 * file name is "C.java".  It is an error for there to be more than one
 * top-level public declaration.
 * <li> If the SourceFile has no public declarations, the file name
 * is the input file name (e.g., "X.jl") with the suffix replaced with ".java"
 * (thus, "X.java").
 * </ul>
 *
 * To use:
 * <pre>
 *     new Translator(job, ts, nf, tf).translate(ast);
 * </pre>
 * The <code>ast</code> must be either a SourceFile or a SourceCollection.
 */
public class Translator extends PrettyPrinter implements Cloneable
{
    protected Job job;
    protected NodeFactory nf;
    protected TargetFactory tf;
    protected TypeSystem ts;

    /** The current typing context, or null if type information is unavailable in this subtree of the AST. */
    protected Context context;

    /**
     * Create a Translator.  The output of the visitor is a collection of files
     * whose names are added to the collection <code>outputFiles</code>.
     */
    public Translator(Job job, TypeSystem ts, NodeFactory nf, TargetFactory tf) {
        super();
        this.job = job;
        this.nf = nf;
        this.tf = tf;
        this.ts = ts;
        this.context = ts.emptyContext();
    }
    
    


   
    /**
     * Return the job associated with this Translator.
     */
    public Job job() { 
        return job;
    }

    /** Copy the translator. */
    protected Translator shallowCopy() {
        try {
            return (Translator) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalCompilerError("Java clone() weirdness.");
        }
    }

    /** Get the extension's type system. */
    public TypeSystem typeSystem() {
        return ts;
    }

    /** Get the extension's node factory. */
    public NodeFactory nodeFactory() {
        return nf;
    }
    
    /** Get the current typing context, or null. */
    public Context context() {
        return this.context;
    }

    /** Create a new <code>Translator</code> identical to <code>this</code> but
     * with new context <code>c</code> */
    public Translator context(Context c) {
        if (c == this.context) {
            return this;
        }
        Translator tr = shallowCopy();
        tr.context = c;
        return tr;
    }
    
    /** Print an ast node using the given code writer.  This method should not
     * be called directly to translate a source file AST; use
     * <code>translate(Node)</code> instead.  This method should only be called
     * by nodes to print their children.
     */
    public void print(Node parent, Node child, CodeWriter w) {
        Translator tr = this;
        
        if (context != null) {
            if (parent == null) {
                Context c = child.del().enterScope(context);
                tr = this.context(c);
            }
            else {
                Context c = parent.del().enterChildScope(child, context);
                tr = this.context(c);
            }
        }

        child.del().translate(w, tr);
        
        if (context != null) {
            child.addDecls(context);
        }
    }
    
    /** Translate the entire AST. */
    public boolean translate(Node ast) {
        if (ast instanceof SourceFile) {
            SourceFile sfn = (SourceFile) ast;
            return translateSource(sfn);
        }
        else if (ast instanceof SourceCollection) {
            SourceCollection sc = (SourceCollection) ast;

            boolean okay = true;

            for (SourceFile sfn : sc.sources()) {
                okay &= translateSource(sfn);
            }

            return okay;
        }
        else {
            throw new InternalCompilerError("AST root must be a SourceFile; " +
                                            "found a " + ast.getClass().getName());
        }
    }

    /** Translate a single SourceFile node */
    protected boolean translateSource(SourceFile sfn) {
    	TypeSystem ts = typeSystem();
    	NodeFactory nf = nodeFactory();
    	TargetFactory tf = this.tf;
    	int outputWidth = job.compiler().outputWidth();
    	
    	// Find the public declarations in the file.  We'll use these to
    	// derive the names of the target files.  There will be one
    	// target file per public declaration.  If there are no public
    	// declarations, we'll use the source file name to derive the
    	// target file name.
    	List<TopLevelDecl> exports = exports(sfn);

        CodeWriter w = null;
    	
    	try {
    	    File of;
    	    
            QName pkg = null;
            
            if (sfn.package_() != null) {
                Package p = sfn.package_().package_().get();
                pkg = p.fullName();
            }
            
            TopLevelDecl first = null;
            
            if (exports.size() == 0) {
                // Use the source name to derive a default output file name.
                of = tf.outputFile(pkg, sfn.source());
            }
            else {
                first = (TopLevelDecl) exports.get(0);
                of = tf.outputFile(pkg, first.name().id(), sfn.source());
            }
            
            String opfPath = of.getPath();
            if (!opfPath.endsWith("$")) job.compiler().addOutputFile(sfn, of.getPath());
            w = tf.outputCodeWriter(of, outputWidth);
            
            writeHeader(sfn, w);
            
            for (Iterator<TopLevelDecl> i = sfn.decls().iterator(); i.hasNext(); ) {
                TopLevelDecl decl = i.next();

                if (decl.flags().flags().isPublic() && decl != first) {
                    // We hit a new exported declaration, open a new file.
                    // But, first close the old file.
                    w.flush();
                    w.close();
                    
                    of = tf.outputFile(pkg, decl.name().id(), sfn.source());
                    job.compiler().addOutputFile(sfn, of.getPath());
                    w = tf.outputCodeWriter(of, outputWidth);
                    
                    writeHeader(sfn, w);
                }
                
                translateTopLevelDecl(w, sfn, decl);
                
                if (i.hasNext()) {
                    w.newline(0);
                }
            }
            
            w.flush();
            return true;
    	}
    	catch (IOException e) {
    	    job.compiler().errorQueue().enqueue(ErrorInfo.IO_ERROR,
    	            "I/O error while translating: " + e.getMessage());
    	    return false;
    	}
        finally {
            if (w != null) {
                try {
                    w.close();
                }
                catch (IOException e) {
                    job.compiler().errorQueue().enqueue(ErrorInfo.IO_ERROR,
                        "I/O error while closing output file: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Translate a top-level declaration <code>decl</code> of source file <code>source</code>.
     * @param w
     * @param source
     * @param decl
     */
    protected void translateTopLevelDecl(CodeWriter w, SourceFile source, TopLevelDecl decl) {
        Translator tr;
        Context c = source.del().enterScope(context);
        tr = this.context(c);
        decl.del().translate(w, tr);
    }

	
    /** Write the package and import declarations for a source file. */
    protected void writeHeader(SourceFile sfn, CodeWriter w) {
	if (sfn.package_() != null) {
	    w.write("package ");
	    sfn.package_().del().translate(w, this);
	    w.write(";");
	    w.newline(0);
	    w.newline(0);
	}

	boolean newline = false;

	for (Import imp : sfn.imports()) {
	    imp.del().translate(w, this);
	    newline = true;
	}

	if (newline) {
	    w.newline(0);
	}
    }

    /** Get the list of public top-level classes declared in the source file. */
    protected List<TopLevelDecl> exports(SourceFile sfn) {
	List<TopLevelDecl> exports = new ArrayList<TopLevelDecl>();

	for (Iterator<TopLevelDecl> i = sfn.decls().iterator(); i.hasNext(); ) {
	    TopLevelDecl decl = i.next();

	    if (decl.flags().flags().isPublic()) {
		exports.add(decl);
	    }
	}

	return exports;
    }

    public String toString() {
	return "Translator";
    }
}
