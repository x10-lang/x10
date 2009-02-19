/**
 * 
 */
package x10c.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import polyglot.ast.SourceFile;
import polyglot.ext.x10cpp.visit.X10CPPTranslator.DelegateTargetFactory;
import polyglot.frontend.Job;
import polyglot.util.SimpleCodeWriter;

/**
 * This class represents a collection of output streams. Each stream has an associated
 * type, specified by StreamClass. There is one output file associated with each type.
 * Operations are provided to create a new stream of a given type (see getNewStream), 
 * and to commit all streams. Committing causes the contents of all streams of a given 
 * type to be written out to the file associated with that type, in the order in which 
 * the streams of that type were created.
 * 
 * @author nvk
 * @author vj -- Moved out to its own separate class.
 *
 */
public class WriterStreams {
	public static enum StreamClass {Header("h"), CC("cc"), Closures("inc");
		String ext;
		private StreamClass(String e) { ext=e;}
		public String toString() { return ext;}
	};
	private Map<StreamClass,SimpleCodeWriter> codeWriters;
	private Map<StreamClass, File> codeFiles;
	private Vector<ClassifiedStream> streams;
	private DelegateTargetFactory targetFactory;
	Job job;

	public WriterStreams(String className, SourceFile sfn, String pkg, DelegateTargetFactory tf, List exports, Job job)
	throws IOException{
		streams = new Vector<ClassifiedStream>();
		codeWriters = new TreeMap<StreamClass,SimpleCodeWriter>();
		codeFiles = new TreeMap<StreamClass,File>();
		targetFactory = tf;
		this.job=job;

		//List exports = exports(sfn);

		for (StreamClass sc : StreamClass.values()) {
			final File file = tf.integratedOutputFile(pkg, className,sfn.source(), 
					sc.toString());
			codeFiles.put(sc, file);
		}
		
	}

	/**
	 * Write out all the streams associated with this object to their correspodnding files.
	 * Note that all streams of the same type are written to the same file, their contents 
	 * concatenated in the order in which the streams were created.
	 * @throws IOException
	 */
	public void commitStreams() throws IOException {
		for (StreamClass sc : StreamClass.values()) {
			final File file = codeFiles.get(sc);
			codeWriters.put(sc, new SimpleCodeWriter(targetFactory.outputWriter(file),
					job.compiler().outputWidth()));
		}
		for (ClassifiedStream s : streams) {
			s.flush();
			codeWriters.get(s.sClass).write(s.toString());
		}
		for (StreamClass sc : StreamClass.values()) {
			SimpleCodeWriter w = codeWriters.get(sc);
			w.flush();
			w.close();
		}
	}
	public File getFile(StreamClass sc) {
		return codeFiles.get(sc);
	}
	public File getHeader() {
		return codeFiles.get(StreamClass.Header);
	}
	/**
	 * Return the current stream of type sc, creating one if there is none.
	 * @param sc
	 * @return
	 */
	public ClassifiedStream getCurStream(StreamClass sc) {
		ClassifiedStream last = null;
		for (ClassifiedStream cfs: streams) {
			if (cfs.sClass != sc) continue;
			last = cfs;
		}
		if (last != null) return last;
		return getNewStream(sc);
	}
	/**
	 * Get the first stream of type sc, creating one if there is none.
	 * @param sc
	 * @return the first stream of type sc
	 */
	public ClassifiedStream getFirstStream(StreamClass sc) {
		for (ClassifiedStream cfs: streams) {
			if (cfs.sClass != sc) continue;
			return cfs;
		}
		return getNewStream(sc);
	}
	private int headerCount = 0;
	/**
	 * Create and return a new stream of type sc. Until a new stream of 
	 * type sc is created, this stream will be the current stream of type sc.
	 * Can only create a new stream for the non-header class.
	 * @param sc
	 * @return a new stream of type sc
	 */
	public ClassifiedStream getNewStream(StreamClass sc) { return getNewStream(sc,true); }

	/**
	 * Create and return a new stream of type sc. Until a new stream of 
	 * type sc is created, this stream will be the current stream of type sc.
	 * Can only create a new stream for the non-header class.
	 * @param sc
	 * @param prepend Whether to prepend the new stream (true) or append it (false)
	 * @return a new stream of type sc
	 */
	public ClassifiedStream getNewStream(StreamClass sc, boolean prepend) {
		if (sc == StreamClass.Header) headerCount++;
		assert (sc != StreamClass.Header || headerCount < 2);
		ClassifiedStream cs = new ClassifiedStream(sc, job.compiler().outputWidth());
		if (prepend) {
                        streams.add(0, cs);
                } else {
                        streams.add(cs);
                }
		return cs;
	}

}
