/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * Copyright (c) 2006 IBM Corporation
 * 
 */

package polyglot.parse;

import polyglot.ast.*;
import polyglot.types.*;
import polyglot.types.Package;
import polyglot.util.*;

/**
 * Represents an ambiguous, possibly qualified, identifier encountered while parsing.
 */
public class ParsedName {
    public final ParsedName prefix;
    public final Id name;
    public final Position pos;
    public final NodeFactory nf;
    public final TypeSystem ts;
    
    public ParsedName(NodeFactory nf, TypeSystem ts, Position pos, Id name) {
        this(nf, ts, pos, null, name);
    }
    
    public ParsedName(NodeFactory nf, TypeSystem ts, Position pos, ParsedName prefix, Id name) {
        this.nf = nf;
        this.ts = ts;
        this.pos = pos != null ? pos : Position.COMPILER_GENERATED;
        this.prefix = prefix;
        this.name = name;
    }
    
    /**A field for keeping the atomicplus modifier for each type.
     * This is for data-centric synchronization.*/
    public FlagsNode flags = null;
    
    public FlagsNode getModifierFlags() {
    	return flags;
    }
    
    public boolean hasModifierFlags() {
    	return flags != null;
    }
    
    public void setFlagsNode(FlagsNode flagsNode) {
    	assert flagsNode != null;
    	this.flags = flagsNode;
    }

    // expr
    public Expr toExpr() {
    	this.checkNoFlags("calling expression: " + this.toString());
        if (prefix == null) {
            return nf.AmbExpr(pos, name);
        }

        return nf.Field(pos, prefix.toReceiver(), name);
    }

    // expr or type
    public Receiver toReceiver() {
    	this.checkNoFlags("calling toPrefix: " + this.toString());
        if (prefix == null) {
            return nf.AmbReceiver(pos, name);
        }

        return nf.AmbReceiver(pos, prefix.toPrefix(), name);
    }

    // expr, type, or package
    public Prefix toPrefix() {
    	this.checkNoFlags("calling toPrefix: " + this.toString());
        if (prefix == null) {
            return nf.AmbPrefix(pos, name);
        }

        return nf.AmbPrefix(pos, prefix.toPrefix(), name);
    }

    // package
    public PackageNode toPackage() {
    	this.checkNoFlags("calling toPackage: " + this.toString());
        if (prefix == null) {
            return nf.PackageNode(pos, Types.ref(ts.createPackage((Ref<? extends Package>) null, name.id())));
        }
        else {
            return nf.PackageNode(pos, Types.ref(ts.createPackage(prefix.toPackage().package_(), name.id())));
        }
    }

    // type
    /**
     * When converting a type to TypeNode, this method first checks whether
     * there is any associated atomicplus information in this <code>flags</code>
     * field. This is used for keep track of the data-centric synchronization
     * type info.
     * */
    public TypeNode toType() {
    	/*if there is atomicplus flag for data-centric synchronization*/
    	if(this.flags != null) {
    		if (prefix == null) {
            	AmbTypeNode amTypeNode = nf.AmbTypeNodeAtomicPlus(pos, name);
            	amTypeNode.setFlagsNode(this.flags); //set the atomicplus flag
                return amTypeNode;
            }

            AmbTypeNode amTypeNode = nf.AmbTypeNodeAtomicPlus(pos, prefix.toPrefix(), name);
            amTypeNode.setFlagsNode(this.flags);     //set the atomicplus flag
            return amTypeNode;
    	}
    	//creates type node without data-centric synchronization info
    	assert this.flags == null;
        if (prefix == null) {
        	AmbTypeNode amTypeNode = nf.AmbTypeNode(pos, name);
            return amTypeNode;
        }

        AmbTypeNode amTypeNode = nf.AmbTypeNode(pos, prefix.toPrefix(), name);
        return amTypeNode;
    }

    private void checkNoFlags(String str) {
    	if(this.flags != null) {
    		throw new RuntimeException(str);
    	}
    }
    
    public String toString() {
        if (prefix == null) {
            return name.toString();
        }

        return prefix.toString() + "." + name.toString();
    }
}
