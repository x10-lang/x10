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
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.ast;

import polyglot.frontend.ExtensionInfo;
import polyglot.types.*;
import polyglot.types.Ref;
import polyglot.types.Package;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;

/**
 * A <code>PackageNode</code> is the syntactic representation of a 
 * Java package within the abstract syntax tree.
 */
public class PackageNode_c extends Node_c implements PackageNode
{
    protected Ref<? extends Package> package_;

    public PackageNode_c(Position pos, Ref<? extends Package> package_) {
	super(pos);
	assert(package_ != null);
	this.package_ = package_;
    }
    
    /** Get the package as a qualifier. */
    public Ref<? extends Qualifier> qualifierRef() {
        return this.package_;
    }

    /** Get the package. */
    public Ref<? extends Package> package_() {
	return this.package_;
    }

    /** Set the package. */
    public PackageNode package_(Ref<? extends Package> package_) {
	PackageNode_c n = (PackageNode_c) copy();
	n.package_ = package_;
	return n;
    }

    /** Write the package name to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        if (package_ == null) {
            w.write("<unknown-package>");
        }
        else {
	    package_.get().print(w);
        }
    }
    
    public void translate(CodeWriter w, Translator tr) {
        w.write(package_.get().translate(tr.context()));
    }

    public String toString() {
        return package_.toString();
    }
    

}
