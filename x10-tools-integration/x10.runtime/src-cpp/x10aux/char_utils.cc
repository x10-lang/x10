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

#include <x10aux/char_utils.h>
#include <x10aux/basic_functions.h>

#include <x10/lang/String.h>

#include <ctype.h>

using namespace x10::lang;
using namespace std;
using namespace x10aux;

x10_boolean x10aux::char_utils::isLowerCase(x10_char x) {
    /* FIXME: Unicode support */
    return islower((int) x.v);
}

x10_boolean x10aux::char_utils::isUpperCase(x10_char x) {
    /* FIXME: Unicode support */
    return isupper((int) x.v);
}

x10_boolean x10aux::char_utils::isTitleCase(x10_char x) {
    (void) x;
    UNIMPLEMENTED("isTitleCase");
    return false;
}

x10_boolean x10aux::char_utils::isDigit(x10_char x) {
    /* FIXME: Unicode support */
    return isdigit((int) x.v);
}

x10_boolean x10aux::char_utils::isLetter(x10_char x) {
    /* FIXME: Unicode support */
    return isalpha((int) x.v);
}

x10_boolean x10aux::char_utils::isLetterOrDigit(x10_char x) {
    /* FIXME: Unicode support */
    return isalnum((int) x.v);
}

x10_boolean x10aux::char_utils::isUnicodeIdentifierStart(x10_char x) {
    (void) x;
    UNIMPLEMENTED("isUnicodeIdentifierStart");
    return false;
}

x10_boolean x10aux::char_utils::isUnicodeIdentifierPart(x10_char x) {
    (void) x;
    UNIMPLEMENTED("isUnicodeIdentifierPart");
    return false;
}

x10_boolean x10aux::char_utils::isUnicodeIdentifierIgnorable(x10_char x) {
    (void) x;
    UNIMPLEMENTED("isUnicodeIdentifierIgnorable");
    return false;
}

x10_boolean x10aux::char_utils::isSpaceChar(x10_char x) {
    /* FIXME: Unicode support */
    return isWhitespace(x);
}

x10_boolean x10aux::char_utils::isWhitespace(x10_char x) {
    /* FIXME: Unicode support */
    return isspace((int) x.v);
}

x10_boolean x10aux::char_utils::isISOControl(x10_char x) {
    /* FIXME: Unicode support */
    return iscntrl((int) x.v);
}

x10_char x10aux::char_utils::toLowerCase(x10_char x) {
    /* FIXME: Unicode support */
    return (x10_char) tolower((int) x.v);
}

x10_char x10aux::char_utils::toUpperCase(x10_char x) {
    /* FIXME: Unicode support */
    return (x10_char) toupper((int) x.v);
}

x10_char x10aux::char_utils::toTitleCase(x10_char x) {
    /* FIXME: Proper title case support */
    return toUpperCase(x);
}
// vim:tabstop=4:shiftwidth=4:expandtab
