/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.visit;

import polyglot.visit.NodeVisitor;
import polyglot.frontend.Job;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
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

import java.util.List;
import java.io.File;

/**
This is the design:
There are 3 kinds of annotations in x10 files:
    @ERR
    @ShouldNotBeERR
    @ShouldBeErr

ErrorInfo has 3 new error kinds:
GOOD_ERR_MARKERS
EXPECTED_ERR_MARKERS
FIX_ERR_MARKERS

This phase/visitor does the following:
it iterates over all the annotations, searching for one of the above 3 annotations.
then it tries to find the corresponding error message, and it replaces it with a new error whose kind is one of the 3 new kinds.
If it didn't find a corresponding error message, it adds a new error whose kind is one of the 3 new kinds.

When running the nightly tests, we should expect to see only GOOD_ERR_MARKERS.
Anything else is a failed test.

In my RunTestSuite I expect to see only GOOD_ERR_MARKERS or EXPECTED_ERR_MARKERS.
Anything else should be reported and fixed:
  * for regular errors, we should add an @ERR marker
  * for FIX_ERR_MARKERS we should fix the error markers: @ShouldBeErr->@ERR, @ERR->@ShouldBeErr, or @ShouldNotBeERR->removed.
 */
public class ErrChecker extends NodeVisitor {
    private final Job job;
    private final SilentErrorQueue errorQueue;

    public ErrChecker(Job job) {
        this.job = job;
        final ErrorQueue queue = job.extensionInfo().compiler().errorQueue();
        errorQueue = queue instanceof SilentErrorQueue ? (SilentErrorQueue) queue : null;
    }

    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        if (errorQueue==null) return n;
        
        if (ERR == null) init();

		List<AnnotationNode> annotations = ((X10Ext) n.ext()).annotations();
        for (AnnotationNode a : annotations) {
	        X10ClassType at = a.annotationInterface();
            boolean isERR = at.isSubtype(ERR, emptyContext);
            boolean isShouldBeErr = at.isSubtype(ShouldBeErr, emptyContext);
            boolean isShouldNotBeERR = at.isSubtype(ShouldNotBeERR, emptyContext);
            if (!isERR && !isShouldBeErr && !isShouldNotBeERR) continue;
            Position p = a.position();
            if (p==null) continue;
            // search for an error in the queue in this position
            boolean foundError = false;
            final List<ErrorInfo> errs = errorQueue.getErrors();
            for (ErrorInfo info : errs) {
                Position p2 = info.getPosition();
                if (p2!=null && p2.line()==p.line() && p2.file().equals(p.file())) {
                    foundError = true;
                    errs.remove(info);
                    int kind;
                    String fixMsg;
                    if (isERR) {
                        kind = ErrorInfo.GOOD_ERR_MARKERS;                                           
                        fixMsg = "Expected: the compiler reports an error for @ERR";
                    } else if (isShouldBeErr) {
                        kind = ErrorInfo.FIX_ERR_MARKERS;
                        fixMsg = "Good: Change @ShouldBeErr to @ERR";
                    } else {
                        assert isShouldNotBeERR;
                        kind = ErrorInfo.EXPECTED_ERR_MARKERS;
                        fixMsg = "Expected: the compiler still reports an error for @ShouldNotBeERR";
                    }
                    errs.add(new ErrorInfo(kind,fixMsg+"\n"+info.getMessage(),p2));
                    break;
                }
            }
            if (!foundError) {
                int kind;
                String fixMsg;
                if (isERR) {
                    kind = ErrorInfo.FIX_ERR_MARKERS;
                    fixMsg = "Bad: Change @ERR to @ShouldBeErr";
                } else if (isShouldBeErr) {
                    kind = ErrorInfo.EXPECTED_ERR_MARKERS;
                    fixMsg = "Expected: the compiler still does not report an error for @ShouldBeErr";
                } else {
                    assert isShouldNotBeERR;
                    kind = ErrorInfo.FIX_ERR_MARKERS;
                    fixMsg = "Good: remove @ShouldNotBeERR";
                }
                errs.add( new ErrorInfo(kind,fixMsg,p) );
            }
        }
		return n;
	}

    ClassType ERR, ShouldBeErr, ShouldNotBeERR;
    Context emptyContext;
	public void init() {
        TypeSystem ts = job.extensionInfo().typeSystem();
        emptyContext = ts.emptyContext();
        try {
            ERR = (ClassType) ts.systemResolver().find(QName.make("x10.compiler.tests.ERR"));
            ShouldBeErr = (ClassType) ts.systemResolver().find(QName.make("x10.compiler.tests.ShouldBeErr"));
            ShouldNotBeERR = (ClassType) ts.systemResolver().find(QName.make("x10.compiler.tests.ShouldNotBeERR"));
        } catch (SemanticException e) {
            throw new RuntimeException(e);
        }
    }
}
