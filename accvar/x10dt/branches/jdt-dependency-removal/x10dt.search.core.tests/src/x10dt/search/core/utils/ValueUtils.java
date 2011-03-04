/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.utils;

import org.eclipse.imp.pdb.facts.ISet;
import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.IValue;


/**
 * Utility methods around {@link IValue} data structures.
 * 
 * @author egeay
 */
public final class ValueUtils {
  
  /**
   * Converts a set of tuples into an array.
   * 
   * @param tupleValueSet The set of tuples to convert.
   * @return A non-null, possibly empty, array.
   */
  public static IValue[][] toArray(final ISet tupleValueSet) {
    final IValue[][] valuesArray = new IValue[tupleValueSet.size()][];
    int i = 0;
    for (final IValue value : tupleValueSet) {
      final ITuple tuple = (ITuple) value;
      valuesArray[i] = new IValue[tuple.arity()];
      int j = -1;
      for (final IValue tupleValue : tuple) {
        valuesArray[i][++j] = tupleValue;
      }
      ++i;
    }
    return valuesArray;
  }
  
  // --- Private code
  
  private ValueUtils() {}

}
