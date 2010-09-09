/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.pdi;

import java.util.ArrayList;

import org.eclipse.imp.x10dt.ui.cpp.debug.utils.PDTUtils;

import com.ibm.debug.internal.pdt.model.Address;
import com.ibm.debug.internal.pdt.model.DebuggeeProcess;
import com.ibm.debug.internal.pdt.model.DebuggeeThread;
import com.ibm.debug.internal.pdt.model.Location;
import com.ibm.debug.internal.pdt.model.MemoryException;

@SuppressWarnings("restriction")
public class X10Object extends MemoryObject {
  public static final char INT = 'i';
  public static final char LONG = 'l';
  public static final char STRING = 's';
  public static final char RAIL = 'r';
  public static final char POINTER = 'p';

  private static final class FieldDesc {
    public FieldDesc(final String name, final int offset, final char type) {
      this.name = name;
      this.offset = offset;
      this.type = type;
    }

    public final String name;
    public final int offset;
    public final char type;
  }

  private final ArrayList<FieldDesc> fields = new ArrayList<FieldDesc>();
  
  public final MemoryObject getObject(int offset) throws MemoryException { // TODO: create an object of the right type(!)
    long ptr = getPointer(offset);
    return new MemoryObject(getProcess(), getThread(), getLocation(), ptr);
  }

  public final X10String getString(int offset) throws MemoryException {
    long ptr = getPointer(offset);
    return new X10String(getProcess(), getThread(), getLocation(), "0x" + PDTUtils.toHexString(ptr, 16));
  }

  public final int getIntField(String name) throws MemoryException {
    assert (findField(name).type == INT);
    return getInt(getFieldOffset(name));
  }

  public final long getLongField(String name) throws MemoryException {
    assert (findField(name).type == LONG);
    return getLong(getFieldOffset(name));
  }

  public final long getPointerField(String name) throws MemoryException {
    assert (findField(name).type == POINTER || findField(name).type == RAIL);
    return getPointer(getFieldOffset(name));
  }

  public final MemoryObject getObjectField(String name) throws MemoryException { // TODO
    return getObject(getFieldOffset(name));
  }

  public final X10String getStringField(String name) throws MemoryException { // TODO
    assert (findField(name).type == STRING);
    return getString(getFieldOffset(name));
  }

  public X10Object(DebuggeeProcess process, DebuggeeThread thread, Location location, long ptr) throws MemoryException {
    super(process, thread, location, ptr);
  }

  public X10Object(DebuggeeProcess process, DebuggeeThread thread, Location location, String expr) throws MemoryException {
    super(process, thread, location, expr);
  }

  public X10Object(DebuggeeProcess process, DebuggeeThread thread, Location location, Address address) throws MemoryException {
    super(process, thread, location, address);
  }
  
  // --- Private code
  
  // TODO: compute the offset automatically
  protected final void addField(String name, int offset, char type) {
    fields.add(new FieldDesc(name, offset, type));
  }
  
  protected final int getFieldOffset(String name) {
    return findField(name).offset;
  }

  private FieldDesc findField(String name) {
    for (FieldDesc f : fields) {
      if (f.name.equals(name))
        return f;
    }
    return null;
  }
  
}