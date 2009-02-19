/**
 * 
 */
package x10c.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import polyglot.util.SimpleCodeWriter;

/**
 * A StreamWrapper represents a stack of ClassifiedStreamss. All output operations
 * are performed with respect to the current ClassifiedStream (the one at the top of the
 * stack). Operations are also provided to push and pop ClassifiedStreams from the stack.
 * 
 * StreamWrapper must extend SimpleCodeWriter since polyglot chose to make SimpleCodeWriter
 * an abstract class rather than an interface. StreamWrapper does not actually use any of the
 * methods defined on SimpleCodeWriter, delegating them instead to the ClassifiedStream
 * on top of the stack.
 * 
 * TODO: Get Nate to make SimpleCodeWriter be an interface so we dont have to jump through
 * the hoops below.
 * 
 * @author nvk
 * @author vj
 *
 */
public class StreamWrapper extends SimpleCodeWriter {
	public WriterStreams ws;
	public ClassifiedStream cs; // current stream;
	java.util.Stack <ClassifiedStream> csStack; // Stream stack.
	public StreamWrapper(ClassifiedStream b, int w, WriterStreams ws) throws IOException{
		 // we override all methods, so super methods will never be called.
		// hence it is ok to pass null to the super constructor.
		super (new ByteArrayOutputStream(), w);
		this.ws = ws;
		this.cs = b;
		csStack = new java.util.Stack <ClassifiedStream>();
	}
	@Override public void newline(){ cs.newline(); }
	@Override public void newline(int n){ cs.newline(n);}
	@Override public void newline(int n, int level){ cs.newline(n, level);}
	@Override public void begin(int n){ cs.begin(n); }
	@Override public void end(){ cs.end(); }
	@Override public void allowBreak(int n, int level, String alt, int altlen) { cs.allowBreak(n,level,alt,altlen); }
	@Override public void allowBreak(int n) { cs.allowBreak(n); }
	@Override public void allowBreak(int n, String alt) { cs.allowBreak(n, alt); }
	@Override public void unifiedBreak(int n, int level, String alt, int altlen) {
			cs.unifiedBreak(n, level, alt, altlen);
	}
	@Override  public void unifiedBreak(int n) { cs.unifiedBreak(n); }
	@Override  public void write (String str){ cs.write (str);}
	@Override public void write(String s, int length) { cs.write(s, length);}
	@Override public boolean flush() throws IOException { return cs.flush();}
	@Override public boolean flush(boolean fmt) throws IOException { return cs.flush(fmt);}
	@Override public String toString() {
		assert false;
		return null;
	}
	@Override public void close() throws IOException { cs.close();}
	public void forceNewline(){ cs.forceNewline(); }
	public void forceNewline(int n){ cs.forceNewline(n); }
	public void popCurrentStream() { this.cs = csStack.pop(); }
	public void pushCurrentStream(ClassifiedStream cs) {
		csStack.push(this.cs);
		this.cs = cs;
	}
}