/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#ifndef X10_LANG_CHAR_H
#define X10_LANG_CHAR_H

#include <x10aux/config.h>

namespace x10 {
    namespace lang {
        class String;
        
        class CharNatives {
        public:
            static x10_boolean isLowerCase(x10_char value);
            static x10_boolean isUpperCase(x10_char value);
            static x10_boolean isTitleCase(x10_char value);
            static x10_boolean isDigit(x10_char value);
            static x10_boolean isLetter(x10_char value);
            static x10_boolean isLetterOrDigit(x10_char value);
            static x10_boolean isUnicodeIdentifierStart(x10_char value);
            static x10_boolean isUnicodeIdentifierPart(x10_char value);
            static x10_boolean isUnicodeIdentifierIgnorable(x10_char value);
            static x10_boolean isSpaceChar(x10_char value);
            static x10_boolean isWhitespace(x10_char value);
            static x10_boolean isISOControl(x10_char value);
            static x10_char toLowerCase(x10_char value);
            static x10_char toUpperCase(x10_char value);
            static x10_char toTitleCase(x10_char value);
            static x10_char reverseBytes(x10_char value);
            static inline x10_int compareTo(x10_char v1, x10_char v2) {
                return v1 == v2 ? 0 : v1 < v2 ? -1 : 1;
            }
        };
    }
}

#endif /* X10_LANG_CHAR_H */

