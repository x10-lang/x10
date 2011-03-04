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
package x10dt.ui.parser;

import java.io.IOException;
import java.io.Reader;

public class CharBufferReader extends Reader
{
    char [] buffer;
    
    CharBufferReader(char [] buffer)
    {
        this.buffer = buffer;
    }

    public char[] getBuffer() { return buffer; }
    
    public int read(char[] cbuf, int off, int len) throws IOException
    {
        // TODO Auto-generated method stub
        throw new IOException("Illegal read call in SafariReader");
    }

    public void close() throws IOException
    {
        // TODO Auto-generated method stub
    }
}