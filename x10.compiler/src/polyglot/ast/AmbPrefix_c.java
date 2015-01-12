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

package polyglot.ast;

import java.util.Map;

import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.*;
import polyglot.visit.*;
import x10.errors.Errors;
import x10.types.X10ClassType;
import x10.util.CollectionFactory;

/**
 * An <code>AmbPrefix</code> is an ambiguous AST node composed of dot-separated
 * list of identifiers that must resolve to a prefix.
 */
public class AmbPrefix_c extends Node_c implements AmbPrefix
{
    protected Prefix prefix;
    protected Id name;

    public AmbPrefix_c(Position pos, Prefix prefix, Id name) {
        super(pos);
        assert(name != null); // prefix may be null
        this.prefix = prefix;
        this.name = name;
    }
    
    /** Get the name of the prefix. */
    public Id nameNode() {
        return this.name;
    }
    
    /** Set the name of the prefix. */
    public AmbPrefix name(Id name) {
        AmbPrefix_c n = (AmbPrefix_c) copy();
        n.name = name;
        return n;
    }

    /** Get the prefix of the prefix. */
    public Prefix prefix() {
	return this.prefix;
    }

    /** Set the prefix of the prefix. */
    public AmbPrefix prefix(Prefix prefix) {
	AmbPrefix_c n = (AmbPrefix_c) copy();
	n.prefix = prefix;
	return n;
    }

    /** Reconstruct the prefix. */
    protected AmbPrefix_c reconstruct(Prefix prefix, Id name) {
	if (prefix != this.prefix || name != this.name) {
	    AmbPrefix_c n = (AmbPrefix_c) copy();
	    n.prefix = prefix;
            n.name = name;
	    return n;
	}

	return this;
    }

    /** Visit the children of the prefix. */
    public Node visitChildren(NodeVisitor v) {
	Prefix prefix = (Prefix) visitChild(this.prefix, v);
        Id name = (Id) visitChild(this.name, v);
        return reconstruct(prefix, name);
    }

    @Override
    public Node disambiguate(ContextVisitor ar) {
        try {
            return superDisambiguate(ar);
        } catch (SemanticException e) {
            Errors.issue(ar.job(), e, this);
            TypeSystem xts =  ar.typeSystem();
            X10ClassType ct = xts.createFakeClass(QName.make(null, name.id()), e);
            return ar.nodeFactory().CanonicalTypeNode(position(), ct);
        }
    }

    /** Disambiguate the prefix. */
    public Node superDisambiguate(ContextVisitor ar) throws SemanticException {
	Position pos = position();
	Disamb disamb = ar.nodeFactory().disamb();
	Node n = disamb.disambiguate(this, ar, pos, prefix, name);
	if (n instanceof Prefix) {
	    return n;
	}
	String typeName = (prefix != null ? prefix + "." : "") + name;
	SemanticException ex = 	new SemanticException("Could not find " + typeName, pos);
	Map<String, Object> map = CollectionFactory.newHashMap();
	map.put(CodedErrorInfo.ERROR_CODE_KEY, CodedErrorInfo.ERROR_CODE_TYPE_NOT_FOUND);
	map.put("TYPE", typeName);
	ex.setAttributes(map);
	throw ex;

    }

    public Node typeCheck(ContextVisitor tc) {
        // Didn't finish disambiguation; just return.
        return this;
    }

    /** Check exceptions thrown by the prefix. */
    public Node exceptionCheck(ExceptionChecker ec) {
	throw new InternalCompilerError(position(),
	    "Cannot exception check ambiguous node " + this + ".");
    } 

    /** Write the prefix to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	if (prefix != null) {
            print(prefix, w, tr);
            w.write(".");
        }
                
        tr.print(this, name, w);
    }

    public String toString() {
	return (prefix == null
		? name.toString()
		: prefix.toString() + "." + name.toString()) + "{amb}";
    }
}
