
//
// Licensed Material 
// (C) Copyright IBM Corp, 2006
//

package x10.parser;

public interface X10KWLexersym {
    public final static int
      Char_a = 3,
      Char_b = 19,
      Char_c = 7,
      Char_d = 16,
      Char_e = 1,
      Char_f = 12,
      Char_g = 22,
      Char_h = 13,
      Char_i = 5,
      Char_j = 26,
      Char_k = 20,
      Char_l = 6,
      Char_m = 17,
      Char_n = 9,
      Char_o = 10,
      Char_p = 14,
      Char_q = 24,
      Char_r = 4,
      Char_s = 8,
      Char_t = 2,
      Char_u = 11,
      Char_v = 21,
      Char_w = 15,
      Char_x = 23,
      Char_y = 18,
      Char_z = 27,
      Char_EOF = 25;

    public final static String orderedTerminalSymbols[] = {
                 "",
                 "e",
                 "t",
                 "a",
                 "r",
                 "i",
                 "l",
                 "c",
                 "s",
                 "n",
                 "o",
                 "u",
                 "f",
                 "h",
                 "p",
                 "w",
                 "d",
                 "m",
                 "y",
                 "b",
                 "k",
                 "v",
                 "g",
                 "x",
                 "q",
                 "EOF",
                 "j",
                 "z"
             };

    public final static int numTokenKinds = orderedTerminalSymbols.length;
    public final static boolean isValidForParser = true;
}
