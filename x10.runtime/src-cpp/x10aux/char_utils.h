#ifndef X10AUX_CHAR_UTILS_H
#define X10AUX_CHAR_UTILS_H

#include <x10aux/config.h>
#include <x10aux/ref.h>

namespace x10 {
    namespace lang {
        class String;
    }
}

namespace x10aux {
    class char_utils {
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
    };
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
