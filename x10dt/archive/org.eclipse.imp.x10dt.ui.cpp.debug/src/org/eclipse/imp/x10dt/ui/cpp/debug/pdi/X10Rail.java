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
 * An in-memory representation of the X10 Rail class. Gets the element type as argument. Layout:
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
 *        ||???        |
 *        || (8 bytes) |
 *        |------------| 24
 *        ||| Settable |
 *        ||| vtable   |
 *        ||| (8 bytes)|
 *        |||----------| 32
 *        ||| ???      |
 *        ||| (32 bts) |
 *        |------------| 64
 *        || AnyRail   |
 *        || vtable    |
 *        || (8 bytes) |
 *        ||-----------| 72
 *        || ???       |
 *        || (8 bytes) |
 *        |------------| 80
 *        | length (4) |
 *        |------------| 84
 *        | pad (4)    |
 *        |------------| 88
 *        | contents   |  :
 *        |     ...    |  '
 * </pre>
 */
@SuppressWarnings("restriction")
public class X10Rail extends X10AnyRail {
  
  public X10Rail(final DebuggeeProcess process, final DebuggeeThread thread, final Location location, 
                 final String address, final int elementSize) throws MemoryException {
    super(process, thread, location, address, elementSize);
  }

  protected int headerSize() {
    return PTR_SIZE * 10;
  }
}
