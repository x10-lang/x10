/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.utils;

import com.ibm.debug.internal.pdt.PICLDebugTarget;
import com.ibm.debug.internal.pdt.model.DebuggeeProcess;
import com.ibm.debug.internal.pdt.model.Module;
import com.ibm.debug.internal.pdt.model.Part;
import com.ibm.debug.internal.pdt.model.View;
import com.ibm.debug.internal.pdt.model.ViewFile;

/**
 * PDT utility methods.
 * 
 * @author egeay
 */
@SuppressWarnings("restriction")
public final class PDTUtils {
  
  /**
   * Returns the view file from the list of parameters provided.
   * 
   * @param target The debug target to consider.
   * @param process The current process to consider.
   * @param fileName The file name for which one wants the related ViewFile instance.
   * @return The ViewFile related to the filename transmitted, or <b>null</b> if we could not find it.
   */
  public static ViewFile searchViewFile(final PICLDebugTarget target, final DebuggeeProcess process, 
                                        final String fileName) {
    for (final Module module : process.getModules(false)) {
      if (module != null) {
        final Part[] parts = module.getParts();
        if (parts == null || parts.length == 0) {
          continue;
        }
        for (final Part part : parts) {
          if (part != null) {
            final View view = part.getView(target.getDebugEngine().getSourceViewInformation());
            if (view != null) {
              for (final ViewFile vf : view.getViewFiles()) {
                if (vf != null) {
                  if (vf.getBaseFileName().equals(fileName)) {
                    return vf;
                  }
                }
              }
            }
          }
        }
      }
    }
    return null;
  }
  
  public static String toHexString(final byte[] rawBytes) {
    final byte[] hex = new byte[2 * rawBytes.length];
    int index = 0;
    for (final byte b : rawBytes) {
      int v = b & 0xFF;
      hex[index++] = HEX_CHAR_TABLE[v >>> 4];
      hex[index++] = HEX_CHAR_TABLE[v & 0xF];
    }
    return new String(hex);
  }
  
  public static String toHexString(final long value, final int length) {
    final String s = Long.toHexString(value);
    if (s.length() > length) {
      // Returns rightmost length chars 
      return s.substring(s.length() - length);
    } else if (s.length() < length) {
      // Pads on left with zeros. at most 15 will be prepended
      return "000000000000000".substring(0, length - s.length()) + s;
    } else {
      return s;
    }
  }
  
  // --- Private code
  
  private PDTUtils() {}
  
  // --- Fields
  
  private static final byte[] HEX_CHAR_TABLE = {
      (byte)'0', (byte)'1', (byte)'2', (byte)'3',
      (byte)'4', (byte)'5', (byte)'6', (byte)'7',
      (byte)'8', (byte)'9', (byte)'a', (byte)'b',
      (byte)'c', (byte)'d', (byte)'e', (byte)'f'
  };

  /**
   * Finds a matching bracket/brace/parenthesis, starting at a given position.
   * If the character at that position is an opening bracket, looks forward, otherwise looks backward.
   * @param str the string
   * @param pos starting position
   * @return the position of a matching bracket, or -1 if none (or if bracket not recognized).
   */
  public static int findMatch(final String str, int pos) {
    boolean forward;
    char c = str.charAt(pos);
    char orig = c;
    char match;
    switch (orig) {
    case '[': match = ']'; forward = true; break;
    case '(': match = ')'; forward = true; break;
    case '{': match = '}'; forward = true; break;
    case '<': match = '>'; forward = true; break;
    case ']': match = '['; forward = false; break;
    case ')': match = '('; forward = false; break;
    case '}': match = '{'; forward = false; break;
    case '>': match = '<'; forward = false; break;
    default: return -1;
    }
    int count = 1;
    int incr = forward ? 1 : -1;
    int limit = forward ? str.length() : -1;
    for (pos += incr; pos != limit; pos += incr) {
      c = str.charAt(pos);
      if (c == orig)
        count++;
      if (c == match) {
        count--;
        if (count == 0)
          return pos;
      }
    }
    return -1;
  }

  /**
   * Finds a character, skipping matching bracket/brace/parenthesis, starting at a given
   * position and going forward.
   * @param str the string
   * @param ch the character
   * @param pos starting position
   * @return the position of the character, or -1 if none (or if the character is a brace).
   */
  public static int indexOfSkipBraces(final String str, final char ch, int pos) {
    return findSkippingBraces(str, ch, pos, 1, str.length());
  }
  
  /**
   * Finds a character, skipping matching bracket/brace/parenthesis, starting at a given
   * position and going backward.
   * @param str the string
   * @param ch the character
   * @param pos starting position
   * @return the position of the character, or -1 if none (or if the character is a brace).
   */
  public static int lastIndexOfSkipBraces(final String str, final char ch, int pos) {
    return findSkippingBraces(str, ch, pos, -1, -1);
  }
  
  private static int findSkippingBraces(final String str, final char ch, int pos, final int incr, final int limit) {
    if (isOpeningBrace(ch) || isClosingBrace(ch))
      return -1;
    while (pos != limit) {
      char c = str.charAt(pos);
      if (c == ch)
        return pos;
      if ((isClosingBrace(c) && incr < 0) || (isOpeningBrace(ch) && incr > 0))
        pos = findMatch(str, pos);
      else
        pos += incr;
    }
    return -1;
  }
  private static boolean isOpeningBrace(char ch) {
    return "[({<".indexOf(ch) != -1;
  }
  private static boolean isClosingBrace(char ch) {
    return ">})]".indexOf(ch) != -1;
  }
}
