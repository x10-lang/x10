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
/*
 * Created on Oct 6, 2005
 */
package org.eclipse.imp.x10dt.core.builder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import polyglot.frontend.FileSource;
import polyglot.frontend.Resource;

/**
 * A Polyglot Source whose input comes from an InputStream.<br>
 * @author rfuhrer
 */
public class StreamSource extends FileSource {
    public StreamSource(final InputStream s, final String fullPath) throws IOException {
    	super(new Resource() {
			public File file() {
				return new File(fullPath);
			}

			public InputStream getInputStream() throws IOException {
				return s;
			}

			public String name() {
				int idx= fullPath.lastIndexOf(File.separatorChar);
				return (idx > 0) ? fullPath.substring(idx+1) : fullPath;
			} }, true);
    }
}
