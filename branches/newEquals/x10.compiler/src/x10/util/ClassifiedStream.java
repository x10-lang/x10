/**
 * 
 */
package x10.util;

import java.io.ByteArrayOutputStream;

import polyglot.util.SimpleCodeWriter;

public class ClassifiedStream extends SimpleCodeWriter {
    final ByteArrayOutputStream stream;
    public final String ext;
    public int lineNumber = 1;
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

    // Should be in polyglot...
    public void writeln(String s) {
        write(s);
        newline();
    }
    
    public void newline() {
        if (pos != lmargin)
            lineNumber++;
        super.newline();
    }
    // FIXME: this is wrong -- need to take into account the whole file.
    public int getLineNumber() { return lineNumber; }
}
// vim:tabstop=4:shiftwidth=4:expandtab
