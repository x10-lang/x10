/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import java.util.List;

import polyglot.util.*;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;

/**
 * A <code>SourceCollection</code> represents a collection of source files.
 */
public class SourceCollection_c extends Node_c implements SourceCollection
{
    protected List<SourceFile> sources;

    public SourceCollection_c(Position pos, List<SourceFile> sources) {
	super(pos);
	assert(sources != null);
	this.sources = TypedList.copyAndCheck(sources, SourceFile.class, true);
    }

    public String toString() {
	return sources.toString();
    }

    /** Get the source files. */
    public List<SourceFile> sources() {
	return this.sources;
    }

    /** Set the statements of the block. */
    public SourceCollection sources(List<SourceFile> sources) {
	SourceCollection_c n = (SourceCollection_c) copy();
	n.sources = TypedList.copyAndCheck(sources, SourceFile.class, true);
	return n;
    }

    /** Reconstruct the collection. */
    protected SourceCollection_c reconstruct(List<SourceFile> sources) {
	if (! CollectionUtil.allEqual(sources, this.sources)) {
	    SourceCollection_c n = (SourceCollection_c) copy();
	    n.sources = TypedList.copyAndCheck(sources, SourceFile.class, true);
	    return n;
	}

	return this;
    }

    /** Visit the children of the block. */
    public Node visitChildren(NodeVisitor v) {
        List<SourceFile> sources = visitList(this.sources, v);
	return reconstruct(sources);
    }

    /** Write the source files to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        for (SourceFile s : sources) {
            print(s, w, tr);
            w.newline(0);
        }
    }
    

}
