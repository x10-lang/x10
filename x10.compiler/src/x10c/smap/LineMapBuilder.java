/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
 ******************************************************************************/

package x10c.smap;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import polyglot.util.InternalCompilerError;

public class LineMapBuilder {
    private static final String LINE_INFO_COMMENT_PREFIX= "//#line ";

    private static final Pattern LINE_COMMENT_REGEXP= Pattern.compile("\\s*//#line ([0-9]+)(\\s*(.)*\"(.*)\")?");

    private String fFileName;

    private List<LineElem> fLineElems;

    private Map<Integer, LineElem> fLineMap;

    public LineMapBuilder(String filename) {
        this.fFileName= filename;
        build();
    }

    public List<LineElem> get() {
        return fLineElems;
    }

    public Map<Integer, LineElem> getLineMap() {
        return fLineMap;
    }

    private void build() {
        fLineElems= new ArrayList<LineElem>();
        fLineMap= new HashMap<Integer, LineElem>();
        LineNumberReader ln = null;
        try {
            ln= new LineNumberReader(new FileReader(fFileName + ".java"));
            String line= null;
            int origSrcLine= -1;
            int javaStart= -1;
            int javaEnd= -1;
            while ((line= ln.readLine()) != null) {
                if (line.contains(LINE_INFO_COMMENT_PREFIX)) {
                    if (origSrcLine != -1) {
                        LineElem le= new LineElem(origSrcLine, javaStart, javaEnd - javaStart);
                        fLineElems.add(le);
                        fLineMap.put(new Integer(origSrcLine), le);
                    }
                    origSrcLine= getNumber(line);
                    javaStart= ln.getLineNumber() + 1;
                    javaEnd= javaStart;
                } else {
                    if (origSrcLine != -1)
                        javaEnd++;
                }
            }
            if (origSrcLine != -1) {
                LineElem le= new LineElem(origSrcLine, javaStart, javaEnd - javaStart);
                fLineElems.add(le);
                fLineMap.put(new Integer(origSrcLine), le);
            }

        } catch (FileNotFoundException e) {
            // This is not necessarily a problem for us: the compiler may have failed to produce
            // a .java file for one or more (error-laden) source files in a given project.
        } catch (IOException e) {
            throw new InternalCompilerError("Exception encountered while building line map", e);
        } finally {
        	if (ln != null) {
        		try {
					ln.close();
				} catch (IOException e) {
				    throw new InternalCompilerError("Exception encountered while building line map", e);
				}
        	}
        }
    }

    private static int getNumber(String line) {
        Matcher m = LINE_COMMENT_REGEXP.matcher(line);
        if (m.matches()) {
            return Integer.parseInt(m.group(1));
        }
        throw new IllegalArgumentException("Invalid format for line comment: " + line);
    }
}
