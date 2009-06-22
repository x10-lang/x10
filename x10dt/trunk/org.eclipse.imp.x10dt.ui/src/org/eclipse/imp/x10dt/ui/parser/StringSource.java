/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.x10dt.ui.parser;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

public class StringSource extends polyglot.frontend.FileSource
{
    private String contents;
    private String filePath;
    private CharBufferReader reader;

    public StringSource(String contents, File file, String filePath) throws IOException {
        super(file);
        this.contents = contents;
        this.filePath = filePath;
    }

    public boolean equals(Object o) {
        if (o instanceof StringSource) {
            StringSource s = (StringSource) o;
            return filePath.equals(s.filePath);
        }

        return false;
    }

    public int hashCode() {
        return filePath.hashCode();
    }

    /** Open the source file. */
    public Reader open() throws IOException {
        if (reader == null) {
            reader = new CharBufferReader(contents.toCharArray());
        }

        return reader;
    }

    /** Close the source file. */
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }

    public String toString() {
        return file.getPath();
    }
}