/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.visit;

import polyglot.visit.NodeVisitor;
import polyglot.frontend.Job;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ast.AmbTypeNode;
import polyglot.types.ClassType;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.Context;
import polyglot.util.Position;
import polyglot.util.ErrorQueue;
import polyglot.util.SilentErrorQueue;
import polyglot.util.ErrorInfo;
import x10.extension.X10Ext;
import x10.ast.AnnotationNode;
import x10.types.X10ClassType;
import x10.errors.Errors;

import java.util.List;
import java.io.File;

/**
This is the design:
There are 3 kinds of annotations in x10 files:
    @ERR
    @ShouldNotBeERR
    @ShouldBeErr

ErrorInfo has 3 corresponding error kinds:
    ERR_MARKER
    SHOULD_NOT_BE_ERR_MARKER
    SHOULD_BE_ERR_MARKER

This phase/visitor happens immediately after parsing
and it issues a corresponding error for every error annotation.
RunTestSuite later processes these errors.
 */
public class ErrChecker extends NodeVisitor {
    private final Job job;

    public ErrChecker(Job job) {
        this.job = job;
    }

    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
		List<AnnotationNode> annotations = ((X10Ext) n.ext()).annotations();
        for (AnnotationNode a : annotations) {
	        TypeNode at = a.annotationType();
            if (!(at instanceof AmbTypeNode)) continue;
            AmbTypeNode node = (AmbTypeNode) at;
            final String annotName = node.name().id().toString();
            boolean isERR = annotName.equals("ERR");
            boolean isShouldBeErr = annotName.equals("ShouldBeErr");
            boolean isShouldNotBeERR = annotName.equals("ShouldNotBeERR");
            if (!isERR && !isShouldBeErr && !isShouldNotBeERR) continue;
            Position p = a.position();
            assert (p!=null);
            job.extensionInfo().compiler().errorQueue().enqueue(
                isERR ? ErrorInfo.ERR_MARKER :
                isShouldBeErr ? ErrorInfo.SHOULD_BE_ERR_MARKER :
                        ErrorInfo.SHOULD_NOT_BE_ERR_MARKER , "Found @"+annotName+" annotation",
                    p
            );
        }
		return n;
	}

}
