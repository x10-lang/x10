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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import polyglot.ast.SourceFile;
import polyglot.frontend.Job;
import polyglot.frontend.TargetFactory;
import polyglot.util.SimpleCodeWriter;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import x10cpp.visit.X10CPPTranslator;

/**
 * This class represents a collection of output streams. Each stream has an associated
 * type, specified by the extension string.
 * Operations are provided to create a new stream of a given type (see getNewStream), 
 * and to commit all streams. Committing causes the contents of all streams of a given 
 * type to be written out to the file associated with that type, in the order in which 
 * the streams of that type were created.
 * 
 * @author nvk
 * @author vj -- Moved out to its own separate class
 * @author igor -- completely rewritten
 */
public class WriterStreams {
    private Map<String, SimpleCodeWriter> codeWriters;
    private Vector<ClassifiedStream> streams;
    private Job job;
    private TargetFactory targetFactory;
    private String pkg;
    private String className;

    public WriterStreams(String className, String pkg, Job job, TargetFactory targetFactory) throws IOException {
        streams = new Vector<ClassifiedStream>();
        codeWriters = new TreeMap<String,SimpleCodeWriter>();
        this.targetFactory = targetFactory;
        this.job = job;
        this.pkg = pkg;
        this.className = className;
    }

    /**
     * Write out all the streams associated with this object to their corresponding files.
     * Note that all streams of the same type are written to the same file, their contents 
     * concatenated in the order in which the streams were created, as indicated by the
     * prepend argument to {@link #getNewStream(String, boolean)}.
     * @throws IOException
     */
    public void commitStreams() throws IOException {
        Set<String> extensions = new TreeSet<String>();
        for (ClassifiedStream s : streams) {
            extensions.add(s.ext);
        }
        for (String ext : extensions) {
            final File file = X10CPPTranslator.outputFile(job.extensionInfo().getOptions(), pkg, className, ext);
            codeWriters.put(ext,
                            new SimpleCodeWriter(targetFactory.outputWriter(file),
                                                 job.compiler().outputWidth()));
        }
        Map<String, Integer> startLineOffsets = CollectionFactory.newHashMap();
        for (ClassifiedStream s : streams) {
            int startLineOffset = startLineOffsets.containsKey(s.ext) ? startLineOffsets.get(s.ext) : 0;
            s.commit(codeWriters.get(s.ext), startLineOffset);
            startLineOffset += s.getStreamLineNumber()-1;
            startLineOffsets.put(s.ext, startLineOffset);
        }
        for (String ext : extensions) {
            SimpleCodeWriter w = codeWriters.get(ext);
            w.flush();
            w.close();
        }
    }

    /**
     * Create and return a new stream of type ext.
     * @param ext
     * @return a new stream of type ext
     */
    public ClassifiedStream getNewStream(String ext) { return getNewStream(ext, true); }

    /**
     * Create and return a new stream of type ext, inserting it either at the beginning
     * or at the end of the stream list.
     * @param ext
     * @param prepend Whether to prepend the new stream to all streams of its class (true)
     *                or append it (false)
     * @return a new stream of type ext
     */
    public ClassifiedStream getNewStream(String ext, boolean prepend) {
        ClassifiedStream cs = new ClassifiedStream(ext, job.compiler().outputWidth());
        if (prepend) {
            streams.add(0, cs);
        } else {
            streams.add(cs);
        }
        return cs;
    }

    /**
     * Create and return a new stream of type ext, inserting it either before or after
     * a given stream s.
     * @param ext
     * @param s
     * @param prepend Whether to prepend the new stream (true) or append it (false)
     * @return a new stream of type ext
     */
    public ClassifiedStream getNewStream(String ext, ClassifiedStream s, boolean prepend) {
        ClassifiedStream cs = new ClassifiedStream(ext, job.compiler().outputWidth());
        int i = streams.indexOf(s);
        if (prepend) {
            streams.add(i, cs);
        } else {
            streams.add(i+1, cs);
        }
        return cs;
    }

    /**
     * Return the name of the file for stream type ext.
     * @param ext
     * @return the name of the file
     */
    public String getStreamName(String ext) {
        return X10CPPTranslator.outputFileName(pkg, className, ext);
    }
}
// vim:tabstop=4:shiftwidth=4:expandtab
