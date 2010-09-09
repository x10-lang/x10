/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.pdi;

import com.ibm.debug.internal.pdt.model.DebuggeeProcess;
import com.ibm.debug.internal.pdt.model.DebuggeeThread;
import com.ibm.debug.internal.pdt.model.Location;
import com.ibm.debug.internal.pdt.model.MemoryException;

/**
 * An in-memory representation of the X10 String object. Layout:
 * 
 * <pre>
 * ptr -&gt; .------------.  0
 *        | vtable     |
 *        |  (8 bytes) |
 *        |------------|  8
 *        || Object    |
 *        || vtable    |
 *        || (8 bytes) |
 *        ||-----------| 16
 *        || ???       |
 *        || (8 bytes) |
 *        |------------| 24
 *        | content    |
 *        |  (8 bytes) |
 *        |------------| 32
 *        | length     |
 *        |  (8 bytes) |
 *        '------------'
 * </pre>
 */
@SuppressWarnings("restriction")
public class X10String extends X10Object {
  
  public X10String(final DebuggeeProcess process, final DebuggeeThread thread, final Location location, 
                   final String address) throws MemoryException {
    super(process, thread, location, address);
    addField("contents", PTR_SIZE * 3, 'p');
    addField("length", PTR_SIZE * 4, 'l');
  }

  public final long getLength() throws MemoryException {
    return getLong(getFieldOffset("length"));
  }

  public final MemoryObject getContents() throws MemoryException {
    long ptr = getPointer(getFieldOffset("contents"));
    int length = (int) getLength();
    return new MemoryObject(getProcess(), getThread(), getLocation(), ptr, length);
  }
}
