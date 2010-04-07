/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.eclipse.imp.x10dt.ui.launch.core.Messages;

/**
 * Adapts a {@link Reader} as an {@link InputStream}.
 * 
 * @author egeay
 */
public class ReaderInputStream extends InputStream {

  /**
   * Constructs the input stream from the reader provided with the default encoding.
   * 
   * @param reader The reader to consider.
   */
  public ReaderInputStream(final Reader reader) {
    this.fReader = reader;
  }

  /**
   * Constructs the input stream from the reader provided with the specified encoding.
   * 
   * @param reader The reader to consider.
   * @param encoding The encoding to use. Must not be <b>null</b>. Use {@link #ReaderInputStream(Reader)} instead.
   */
  public ReaderInputStream(final Reader reader, final String encoding) {
    this(reader);
    if (encoding == null) {
      throw new NullPointerException("Encoding must not be null"); //$NON-NLS-1$
    }
    this.fEncoding = encoding;
  }
  
  // --- Abstract methods implementation

  public synchronized int read() throws IOException {
    if (this.fReader == null) {
      throw new IOException(Messages.RIS_StreamClosed);
    }
    byte result;
    if (this.fBuffer != null && this.fBegin < this.fBuffer.length) {
      result = this.fBuffer[this.fBegin];
      if (++this.fBegin == this.fBuffer.length) {
        this.fBuffer = null;
      }
    } else {
      byte[] buf = new byte[1];
      if (read(buf, 0, 1) <= 0) {
        result = -1;
      }
      result = buf[0];
    }

    if (result < -1) {
      result += 256;
    }

    return result;
  }

  public synchronized int read(final byte[] byteArray, final int offset, final int length) throws IOException {
    if (this.fReader == null) {
      throw new IOException(Messages.RIS_StreamClosed);
    }

    while (this.fBuffer == null) {
      final char[] buffer = new char[length];
      final int nCharsRead = this.fReader.read(buffer);
      if (nCharsRead == -1) {
        return -1;
      }
      if (nCharsRead > 0) {
        this.fBuffer = new String(buffer, 0, nCharsRead).getBytes(this.fEncoding);
        this.fBegin = 0;
      }
    }

    final int finalLength;
    if (length > this.fBuffer.length - this.fBegin) {
      finalLength = this.fBuffer.length - this.fBegin;
    } else {
      finalLength = length;
    }

    System.arraycopy(this.fBuffer, this.fBegin, byteArray, offset, finalLength);

    if ((this.fBegin += finalLength) >= this.fBuffer.length) {
      this.fBuffer = null;
    }

    return finalLength;
  }

  public synchronized void mark(final int limit) {
    try {
      this.fReader.mark(limit);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe.getMessage());
    }
  }

  public synchronized int available() throws IOException {
    if (this.fReader == null) {
      throw new IOException(Messages.RIS_StreamClosed);
    }
    if (this.fBuffer != null) {
      return this.fBuffer.length - this.fBegin;
    }
    if (this.fReader.ready()) {
      return 1;
    } else {
      return 0;
    }
  }

  public boolean markSupported() {
    return false;
  }

  public synchronized void reset() throws IOException {
    if (this.fReader == null) {
      throw new IOException(Messages.RIS_StreamClosed);
    }
    this.fBuffer = null;
    this.fReader.reset();
  }

  public synchronized void close() throws IOException {
    if (this.fReader != null) {
      this.fReader.close();
      this.fBuffer = null;
      this.fReader = null;
    }
  }

  // --- Fields

  private Reader fReader;

  private String fEncoding = System.getProperty("file.encoding"); //$NON-NLS-1$

  private byte[] fBuffer;

  private int fBegin;

}
