package org.eclipse.imp.x10dt.ui.parser;

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