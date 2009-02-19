/**
 * 
 */
package x10c.util;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import polyglot.ast.SourceFile;
import polyglot.ext.x10cpp.visit.X10CPPTranslator.DelegateTargetFactory;
import polyglot.frontend.Job;
import polyglot.util.SimpleCodeWriter;
import x10c.util.StreamWrapper.StreamClass;

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
 */
public class WriterStreams {
    private Map<StreamClass, SimpleCodeWriter> codeWriters;
    private Map<StreamClass, File> codeFiles;
    private Vector<ClassifiedStream> streams;
    private DelegateTargetFactory targetFactory;
    Job job;

    public WriterStreams(String className, SourceFile sfn, String pkg,
                         DelegateTargetFactory tf, List exports, Job job) throws IOException
    {
        streams = new Vector<ClassifiedStream>();
        codeWriters = new TreeMap<StreamClass,SimpleCodeWriter>();
        codeFiles = new TreeMap<StreamClass,File>();
        targetFactory = tf;
        this.job = job;

        //List exports = exports(sfn);

        for (StreamClass sc : StreamClass.values()) {
            final File file = tf.integratedOutputFile(pkg, className,sfn.source(), sc.toString());
            codeFiles.put(sc, file);
        }
    }

    /**
     * Write out all the streams associated with this object to their correspodnding files.
     * Note that all streams of the same type are written to the same file, their contents 
     * concatenated in the order in which the streams were created, as indicated by the
     * prepend argument to {@link #getNewStream(StreamClass, boolean)}.
     * @throws IOException
     */
    public void commitStreams() throws IOException {
        Set<StreamClass> nonEmpty = new HashSet<StreamClass>();
        for (ClassifiedStream s : streams) {
            nonEmpty.add(s.sClass);
        }
        for (StreamClass sc : StreamClass.values()) {
//            if (!nonEmpty.contains(sc))
//                continue;
            final File file = codeFiles.get(sc);
            codeWriters.put(sc,
                            new SimpleCodeWriter(targetFactory.outputWriter(file),
                                                 job.compiler().outputWidth()));
        }
        for (ClassifiedStream s : streams) {
            s.flush();
            codeWriters.get(s.sClass).write(s.toString());
        }
        for (StreamClass sc : StreamClass.values()) {
//            if (!nonEmpty.contains(sc))
//                continue;
            SimpleCodeWriter w = codeWriters.get(sc);
            w.flush();
            w.close();
        }
    }

    private File getFile(StreamClass sc) {
        return codeFiles.get(sc);
    }

    private File getHeader() {
        return codeFiles.get(StreamClass.Header);
    }

    /**
     * Create and return a new stream of type sc.
     * @param sc
     * @return a new stream of type sc
     */
    ClassifiedStream getNewStream(StreamClass sc) { return getNewStream(sc, true); }

    /**
     * Create and return a new stream of type sc, inserting it either at the beginning
     * or at the end of the stream list.
     * @param sc
     * @param prepend Whether to prepend the new stream to all streams of its class (true)
     *                or append it (false)
     * @return a new stream of type sc
     */
    ClassifiedStream getNewStream(StreamClass sc, boolean prepend) {
        ClassifiedStream cs = new ClassifiedStream(sc, job.compiler().outputWidth());
        if (prepend) {
            streams.add(0, cs);
        } else {
            streams.add(cs);
        }
        return cs;
    }

    /**
     * Create and return a new stream of type sc, inserting it either before or after
     * a given stream s.
     * @param sc
     * @param s
     * @param prepend Whether to prepend the new stream (true) or append it (false)
     * @return a new stream of type sc
     */
    ClassifiedStream getNewStream(StreamClass sc, ClassifiedStream s, boolean prepend) {
        ClassifiedStream cs = new ClassifiedStream(sc, job.compiler().outputWidth());
        int i = streams.indexOf(s);
        if (prepend) {
            streams.add(i, cs);
        } else {
            streams.add(i+1, cs);
        }
        return cs;
    }
}
// vim:tabstop=4:shiftwidth=4:expandtab
