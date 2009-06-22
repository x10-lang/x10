/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.pdi;

import org.eclipse.debug.core.model.MemoryByte;
import org.eclipse.imp.x10dt.ui.cpp.debug.utils.PDTUtils;

import com.ibm.debug.internal.pdt.model.Address;
import com.ibm.debug.internal.pdt.model.DebuggeeProcess;
import com.ibm.debug.internal.pdt.model.DebuggeeThread;
import com.ibm.debug.internal.pdt.model.Location;
import com.ibm.debug.internal.pdt.model.Memory;
import com.ibm.debug.internal.pdt.model.MemoryException;

@SuppressWarnings("restriction")
public class MemoryObject {
  
  public static final int BYTE_SIZE = 1;
  public static final int CHAR_SIZE = 2;
  public static final int SHORT_SIZE = 2;
  public static final int INT_SIZE = 4;
  public static final int LONG_SIZE = 8;
  public static final int BOOLEAN_SIZE = INT_SIZE;
  public static final int FLOAT_SIZE = INT_SIZE;
  public static final int DOUBLE_SIZE = LONG_SIZE;
  public static final int PTR_SIZE = LONG_SIZE;
  private static final int OBJECT_HEADER_SIZE = PTR_SIZE * 4; // FIXME: initial size

  private Memory memory = null;
  
  private final DebuggeeProcess process;

  protected final DebuggeeProcess getProcess() {
    return process;
  }

  private final DebuggeeThread thread;

  protected final DebuggeeThread getThread() {
    return thread;
  }

  private final Location location;

  protected final Location getLocation() {
    return location;
  }

  private final Address address;

  public MemoryObject(final DebuggeeProcess process, final DebuggeeThread thread, final Location location, 
                      final long ptr) throws MemoryException {
    this(process, thread, location, "0x" + PDTUtils.toHexString(ptr, 16));
  }

  public MemoryObject(final DebuggeeProcess process, final DebuggeeThread thread, final Location location, 
                      final String expr) throws MemoryException {
    this(process, thread, location, process.convertToAddress(expr, location, thread));
  }

  public MemoryObject(final DebuggeeProcess process, final DebuggeeThread thread, final Location location, 
                      final Address address) throws MemoryException {
    this(process, thread, location, address, OBJECT_HEADER_SIZE);
  }

  public MemoryObject(final DebuggeeProcess process, final DebuggeeThread thread, final Location location, 
                      final long ptr, final int size) throws MemoryException {
    this(process, thread, location, "0x" + PDTUtils.toHexString(ptr, 16), size);
  }

  public MemoryObject(final DebuggeeProcess process, final DebuggeeThread thread, final Location location, 
                      final String expr, final int size) throws MemoryException {
    this(process, thread, location, process.convertToAddress(expr, location, thread), size);
  }

  public MemoryObject(final DebuggeeProcess process, final DebuggeeThread thread, final Location location, 
                      final Address address, final int size) throws MemoryException {
    final int vptrsz = address.getAddressSize();
    assert (vptrsz == MemoryObject.PTR_SIZE);
    memory = process.getMemory(address.getAddress(), size);
    this.process = process;
    this.thread = thread;
    this.location = location;
    this.address = address;
  }

  protected final MemoryByte[] getEnoughMemory(final int total) throws MemoryException {
    if (total > memory.getNumBytes()) {
      memory = process.getMemory(address.getAddress(), total);
    }
    MemoryByte[] mbytes = memory.getMemory();
    return mbytes;
  }

  public final byte[] getBytes() throws MemoryException {
    return getBytes(0, memory.getNumBytes());
  }

  public final byte[] getBytes(final int offset, final int length) throws MemoryException {
    MemoryByte[] mbytes = getEnoughMemory(offset + length);
    return extractBytes(mbytes, offset, length);
  }

  public final int getInt(final int offset) throws MemoryException {
    MemoryByte[] mbytes = getEnoughMemory(offset + INT_SIZE);
    return extractInt(mbytes, offset);
  }

  public final long getLong(final int offset) throws MemoryException {
    MemoryByte[] mbytes = getEnoughMemory(offset + LONG_SIZE);
    return extractLong(mbytes, offset);
  }

  public final long getPointer(final int offset) throws MemoryException {
    if (MemoryObject.PTR_SIZE == MemoryObject.LONG_SIZE)
      return getLong(offset);
    else
      return getInt(offset);
  }

  public static void dumpBytes(final MemoryByte[] cbytes) {
    for (int i = 0; i < cbytes.length; i++) {
      String s = PDTUtils.toHexString(cbytes[i].getValue() & 0xFF, 2);
      System.out.print(s + " ");
    }
    System.out.println();
  }

  public static byte[] extractBytes(final MemoryByte[] mbytes) {
    return extractBytes(mbytes, 0, mbytes.length);
  }

  public static byte[] extractBytes(final MemoryByte[] mbytes, final int offset, final int length) {
    byte[] bytes = new byte[length];
    for (int i = 0; i < length; i++) {
      bytes[i] = mbytes[offset + i].getValue();
    }
    return bytes;
  }

  public static long extractLong(final MemoryByte[] mbytes, final int offset) { // FIXME: endianness
    return ((mbytes[offset + 0].getValue() & 0xFFl) << 56) | ((mbytes[offset + 1].getValue() & 0xFFl) << 48)
           | ((mbytes[offset + 2].getValue() & 0xFFl) << 40) | ((mbytes[offset + 3].getValue() & 0xFFl) << 32)
           | ((mbytes[offset + 4].getValue() & 0xFFl) << 24) | ((mbytes[offset + 5].getValue() & 0xFFl) << 16)
           | ((mbytes[offset + 6].getValue() & 0xFFl) << 8) | ((mbytes[offset + 7].getValue() & 0xFFl) << 0);
  }

  static int extractInt(final MemoryByte[] mbytes, final int offset) { // FIXME: endianness
    return ((mbytes[offset + 0].getValue() & 0xFF) << 24) | ((mbytes[offset + 1].getValue() & 0xFF) << 16)
           | ((mbytes[offset + 2].getValue() & 0xFF) << 8) | ((mbytes[offset + 3].getValue() & 0xFF) << 0);
  }

  public static long extractPointer(final MemoryByte[] mbytes, final int offset) {
    if (MemoryObject.PTR_SIZE == MemoryObject.LONG_SIZE)
      return MemoryObject.extractLong(mbytes, offset);
    else
      return MemoryObject.extractInt(mbytes, offset);
  }
}
