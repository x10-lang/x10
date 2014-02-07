/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Stack;

import polyglot.util.SimpleCodeWriter;

/**
 * A StreamWrapper represents a pair of ClassifiedStreams, header and body, plus a stream
 * designated as "current" (could be yet a third one).  All output operations are performed
 * by default with respect to the current ClassifiedStream.  Operations are also provided
 * to set the header and body streams. 
 * 
 * StreamWrapper must extend SimpleCodeWriter since polyglot chose to make SimpleCodeWriter
 * an abstract class rather than an interface. StreamWrapper does not actually use any of the
 * methods defined on SimpleCodeWriter, delegating them instead to the current ClassifiedStream.
 * 
 * TODO: Get Nate to make SimpleCodeWriter be an interface so we dont have to jump through
 * the hoops below.
 * 
 * @author nvk
 * @author vj
 * @author igor
 */
public class StreamWrapper extends SimpleCodeWriter {

    public static final String Header = "h";
    public static final String CC = "cc";

    // Desired API: getNewStream(class, pre/append), setCurrentStream, setHeader, setBody, header, body
    // 2 sets of streams - headers and primary file
    // Decouple stream class from stream destination file
    private final WriterStreams primaryFile;
    private final WriterStreams headers;
    private ClassifiedStream cs; // current stream;
    private Stack<ClassifiedStream> csStack;
    private ClassifiedStream h; // header stream
    private ClassifiedStream w; // body stream
    public StreamWrapper(WriterStreams pf, WriterStreams hf, int width) throws IOException {
        super(new ByteArrayOutputStream(), width);
        this.primaryFile = pf;
        this.headers = hf; 
        this.csStack = new Stack<ClassifiedStream>();
    }

    public void set(ClassifiedStream h, ClassifiedStream w) {
        this.h = h;
        this.w = w;
        this.cs = w;
    }

    public ClassifiedStream header() { return h; }
    public ClassifiedStream body() { return w; }
    public void setHeader(ClassifiedStream h) { this.h = h; }
    public void setBody(ClassifiedStream w) { this.w = w; }

    public ClassifiedStream currentStream() { return cs; }
    public void pushCurrentStream(ClassifiedStream s) { csStack.push(this.cs); this.cs = s; }
    public void popCurrentStream() { this.cs = csStack.pop(); }

    public ClassifiedStream getNewStream(String sc, ClassifiedStream s, boolean prepend) {
        if (sc.equals(CC)) {
            return primaryFile.getNewStream(sc, s, prepend);
        } else {
            return headers.getNewStream(sc, s, prepend);
        }
    }
    public ClassifiedStream getNewStream(String sc, boolean prepend) {
        if (sc.equals(CC)) {
            return primaryFile.getNewStream(sc, prepend);
        } else {
            return headers.getNewStream(sc, prepend);
        }
    }
    public ClassifiedStream getNewStream(String sc) {
        return getNewStream(sc, true);
    }

    @Override public void newline() { cs.newline(); }
    @Override public void newline(int n) { cs.newline(n); }
    @Override public void newline(int n, int level) { cs.newline(n, level); }
    @Override public void begin(int n) { cs.begin(n); }
    @Override public void end() { cs.end(); }
    @Override public void allowBreak(int n, int level, String alt, int altlen) {
        cs.allowBreak(n,level,alt,altlen);
    }
    @Override public void allowBreak(int n) { cs.allowBreak(n); }
    @Override public void allowBreak(int n, String alt) { cs.allowBreak(n, alt); }
    @Override public void unifiedBreak(int n, int level, String alt, int altlen) {
        cs.unifiedBreak(n, level, alt, altlen);
    }
    @Override public void unifiedBreak(int n) { cs.unifiedBreak(n); }
    @Override public void write(String str) { cs.write(str); }
    public void writeln(String str) { cs.writeln(str); }
    @Override public void write(String s, int length) { cs.write(s, length); }
    @Override public boolean flush() throws IOException { return cs.flush(); }
    @Override public boolean flush(boolean fmt) throws IOException { return cs.flush(fmt); }
    @Override public String toString() {
        assert false;
        return null;
    }
    @Override public void close() throws IOException { cs.close(); }
    public void forceNewline() { cs.forceNewline(); }
    public void forceNewline(int n) { cs.forceNewline(n); }

    public String getStreamName(String ext) {
        if (ext.equals(CC)) {
            return primaryFile.getStreamName(ext);
        } else {
            return headers.getStreamName(ext);            
        }
    }
}
// vim:tabstop=4:shiftwidth=4:expandtab
