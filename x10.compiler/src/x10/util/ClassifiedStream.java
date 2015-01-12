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

package x10.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import polyglot.util.SimpleCodeWriter;

public class ClassifiedStream extends SimpleCodeWriter {
    final ByteArrayOutputStream stream;
    public final String ext;
    private boolean committed = false;
    private int startLineOffset = -1;
    private int lineNumber = 1;
    private int linesToOmit = 0;
    private ClassifiedStream(ByteArrayOutputStream bs, String ext, int width) {
        super(bs, width);
        stream = bs;
        this.ext = ext;
    }
    public ClassifiedStream(String ext, int width) {
        this(new ByteArrayOutputStream(), ext, width);
    }
    public String contents() { output.flush(); return stream.toString(); }
    public void forceNewline() { pos = -1; newline(); }
    public void forceNewline(int n) { pos = -1; newline(n); }
    public String toString() { return "ClassifiedStream: '"+contents()+"'"; }

    public void write(String s, int l) {
        if (this.committed())
            throw new RuntimeException("Already committed");
        super.write(s, l);
    }

    public void newline() {
        if (this.committed())
            throw new RuntimeException("Already committed");
        if (pos != lmargin)
            lineNumber++;
        super.newline();
    }

    public void omitLines(int num) { linesToOmit = num; }
    
    public int getOmittedLines() 
    { 
    	int n = linesToOmit;
    	linesToOmit = 0;
    	return n;
    }
    
    public int getStreamLineNumber() { return lineNumber; }

    public int getStartLineOffset() { return startLineOffset; }

    private boolean committed() { return this.committed; }
    public void commit(SimpleCodeWriter w, int startLineOffset) throws IOException {
        if (this.committed())
            throw new RuntimeException("Already committed");
        this.flush();
        this.startLineOffset = startLineOffset;
        this.executeCommitListeners();
        this.committed = true;
        w.write(this.contents());
    }

    public static abstract class CommitListener {
        public abstract void run(ClassifiedStream s);
    }
    private ArrayList<CommitListener> commitListeners = new ArrayList<CommitListener>();
    public void registerCommitListener(CommitListener listener) {
        this.commitListeners.add(listener);
    }
    private void executeCommitListeners() {
        for (CommitListener listener : this.commitListeners) {
            listener.run(this);
        }
    }

    // Commented out debug support
//    int lvl = 0;
//    private static final String spaces = "                                                                    ";
//    private StackTraceElement getCaller() {
//        StackTraceElement[] stackTrace = new Exception().fillInStackTrace().getStackTrace();
//        for (int i = 1; i < stackTrace.length; i++) {
//            String method = stackTrace[i].getMethodName();
//            if (!method.equals("begin") && !method.equals("end"))
//                return stackTrace[i];
//        }
//        return null;
//    }
//    private Stack<StackTraceElement> callers = new Stack<StackTraceElement>();
//    private ArrayList<StackTraceElement[]> previous = new ArrayList<StackTraceElement[]>();
//    public void begin(int n) {
//        super.begin(n);
//        StackTraceElement caller = getCaller();
//        System.err.println(System.identityHashCode(this)+":"+spaces.substring(0, lvl)+"begin at "+caller);
//        lvl++;
//        callers.push(caller);
//    }
//    public void end() {
//        if (lvl == 0)
//            System.err.println("Invalid end:\n"+this);
//        StackTraceElement caller = getCaller();
//        if (!callers.peek().getMethodName().equals(caller.getMethodName())) {
//            System.err.println(System.identityHashCode(this)+": Unmatched end at "+caller+" begin was at "+callers.peek());
//            System.err.println("\tPrevious "+previous.size()+" calls:");
//            for (StackTraceElement[] p : previous) {
//                System.err.println("\t\t"+p[0]+" - "+p[1]);
//            }
//            throw new Error();
//        }
//        previous.add(new StackTraceElement[] { callers.peek(), caller });
//        if (previous.size() > 20)
//            previous.remove(0);
//        callers.pop();
//        --lvl;
//        System.err.println(System.identityHashCode(this)+":"+spaces.substring(0, lvl)+"end at "+caller);
//        super.end();
//    }
}
// vim:tabstop=4:shiftwidth=4:expandtab
