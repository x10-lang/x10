/**
 * 
 */
package x10c.util;

import java.io.ByteArrayOutputStream;


import polyglot.main.Options;
import polyglot.util.SimpleCodeWriter;


public class ClassifiedStream extends SimpleCodeWriter {
	final ByteArrayOutputStream stream;
	public final WriterStreams.StreamClass sClass;
	private ClassifiedStream(ByteArrayOutputStream bs, WriterStreams.StreamClass c, int width) {
		super(bs, width);
		stream = bs;
		this.sClass=c;
	}
	public ClassifiedStream(WriterStreams.StreamClass c, int width) {
		this(new ByteArrayOutputStream(), c, width);
	}
	public void forceNewline(){ newline();}
	public void forceNewline(int n){newline(n); }
	public String toString() { return stream.toString();}

}