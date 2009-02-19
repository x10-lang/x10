/**
 * 
 */
package x10c.util;

import java.io.ByteArrayOutputStream;

import polyglot.util.SimpleCodeWriter;


public class ClassifiedStream extends SimpleCodeWriter {
    final ByteArrayOutputStream stream;
    public final StreamWrapper.StreamClass sClass;
    private ClassifiedStream(ByteArrayOutputStream bs, StreamWrapper.StreamClass c, int width) {
        super(bs, width);
        stream = bs;
        this.sClass = c;
    }
    public ClassifiedStream(StreamWrapper.StreamClass c, int width) {
        this(new ByteArrayOutputStream(), c, width);
    }
    public String contents() { output.flush(); return stream.toString(); }
    public void forceNewline() { pos = -1; newline(); }
    public void forceNewline(int n) { pos = -1; newline(n); }
    public String toString() { return "ClassifiedStream: '"+contents()+"'"; }
}
// vim:tabstop=4:shiftwidth=4:expandtab
