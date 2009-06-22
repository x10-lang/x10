/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.pdi;

import static org.eclipse.imp.x10dt.ui.cpp.debug.utils.X10Utils.FMGL;

import com.ibm.debug.internal.pdt.model.DebuggeeProcess;
import com.ibm.debug.internal.pdt.model.DebuggeeThread;
import com.ibm.debug.internal.pdt.model.Location;
import com.ibm.debug.internal.pdt.model.MemoryException;

/**
 * An in-memory representation of some X10 Rail class. Gets the element type as argument. Subclasses must override the
 * headerSize() method. Layout:
 * 
 * <pre>
 * ptr -&gt; .------------.  0
 *        || header    |
 *        || (N bytes) |
 *        |------------|  N
 *        | length (4) |
 *        |------------|  N+4
 *        | pad (4)    |
 *        |------------|  N+8
 *        | contents   |  :
 *        |     ...    |  '
 * </pre>
 */
@SuppressWarnings("restriction")
public abstract class X10AnyRail extends X10Object {

  private int elementSize;

  public final int getElementSize() {
    return elementSize;
  }

  public X10AnyRail(final DebuggeeProcess process, final DebuggeeThread thread, final Location location, 
                    final String address, final int elementSize) throws MemoryException {
    super(process, thread, location, address);
    addField(FMGL("length"), headerSize(), 'i');
    this.elementSize = elementSize;
  }

  protected abstract int headerSize();

  private int getContentOffset() {
    return getFieldOffset(FMGL("length")) + INT_SIZE + INT_SIZE;
  }

  public final int getLength() throws MemoryException {
    return getInt(getFieldOffset(FMGL("length")));
  }

  private int retrieveContents() throws MemoryException {
    int length = getLength();
    getEnoughMemory(getContentOffset() + length * elementSize);
    return length;
  }

  private void checkBounds(final int index) throws MemoryException {
    int length = retrieveContents();
    assert (index >= 0 && index < length);
  }

  public final int getIntAt(final int index) throws MemoryException {
    checkBounds(index);
    assert (elementSize == INT_SIZE);
    return getInt(getContentOffset() + index * elementSize);
  }

  public final long getLongAt(final int index) throws MemoryException {
    checkBounds(index);
    assert (elementSize == LONG_SIZE);
    return getLong(getContentOffset() + index * elementSize);
  }

  public final long getPointerAt(final int index) throws MemoryException {
    checkBounds(index);
    assert (elementSize == PTR_SIZE);
    return getPointer(getContentOffset() + index * elementSize);
  }

  public final MemoryObject getObjectAt(final int index) throws MemoryException { // TODO: create an object of the right type(!)
    checkBounds(index);
    assert (elementSize == PTR_SIZE);
    return getObject(getContentOffset() + index * elementSize);
  }

  public final X10String getStringAt(final int index) throws MemoryException {
    checkBounds(index);
    assert (elementSize == PTR_SIZE);
    return getString(getContentOffset() + index * elementSize);
  }

  public final byte[] getRawContents() throws MemoryException {
    return getRawContents(0, getLength());
  }

  public final byte[] getRawContents(final int offset, final int length) throws MemoryException {
    return getBytes(getContentOffset() + offset * elementSize, length * elementSize);
  }
}
