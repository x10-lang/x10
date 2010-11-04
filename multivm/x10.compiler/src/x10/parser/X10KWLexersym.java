/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.parser;

public interface X10KWLexersym {
    public final static int
      Char_a = 3,
      Char_b = 19,
      Char_c = 6,
      Char_d = 16,
      Char_e = 1,
      Char_f = 12,
      Char_g = 22,
      Char_h = 13,
      Char_i = 8,
      Char_j = 26,
      Char_k = 20,
      Char_l = 9,
      Char_m = 17,
      Char_n = 10,
      Char_o = 7,
      Char_p = 14,
      Char_q = 27,
      Char_r = 4,
      Char_s = 5,
      Char_t = 2,
      Char_u = 11,
      Char_v = 21,
      Char_w = 18,
      Char_x = 23,
      Char_y = 15,
      Char_z = 28,
      Char_Z = 24,
      Char_EOF = 25;

    public final static String orderedTerminalSymbols[] = {
                 "",
                 "e",
                 "t",
                 "a",
                 "r",
                 "s",
                 "c",
                 "o",
                 "i",
                 "l",
                 "n",
                 "u",
                 "f",
                 "h",
                 "p",
                 "y",
                 "d",
                 "m",
                 "w",
                 "b",
                 "k",
                 "v",
                 "g",
                 "x",
                 "Z",
                 "EOF",
                 "j",
                 "q",
                 "z"
             };

    public final static int numTokenKinds = orderedTerminalSymbols.length;
    public final static boolean isValidForParser = true;
}
