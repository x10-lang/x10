/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

#include <ctype.h>
#include <stdlib.h>

#include <x10/lang/Char.h>

using namespace x10::lang;

x10_boolean CharNatives::isLowerCase(x10_char x) {
    /* FIXME: Unicode support */
    return islower((int) x.v);
}

x10_boolean CharNatives::isUpperCase(x10_char x) {
    /* FIXME: Unicode support */
    return isupper((int) x.v);
}

x10_boolean CharNatives::isTitleCase(x10_char x) {
    (void) x;
    UNIMPLEMENTED("isTitleCase");
    return false;
}

x10_boolean CharNatives::isDigit(x10_char x) {
    /* FIXME: Unicode support */
    return isdigit((int) x.v);
}

x10_boolean CharNatives::isLetter(x10_char x) {
    /* FIXME: Unicode support */
    return isalpha((int) x.v);
}

x10_boolean CharNatives::isLetterOrDigit(x10_char x) {
    /* FIXME: Unicode support */
    return isalnum((int) x.v);
}

x10_boolean CharNatives::isUnicodeIdentifierStart(x10_char x) {
    (void) x;
    UNIMPLEMENTED("isUnicodeIdentifierStart");
    return false;
}

x10_boolean CharNatives::isUnicodeIdentifierPart(x10_char x) {
    (void) x;
    UNIMPLEMENTED("isUnicodeIdentifierPart");
    return false;
}

x10_boolean CharNatives::isUnicodeIdentifierIgnorable(x10_char x) {
    (void) x;
    UNIMPLEMENTED("isUnicodeIdentifierIgnorable");
    return false;
}

x10_boolean CharNatives::isSpaceChar(x10_char x) {
    /* FIXME: Unicode support */
    return isWhitespace(x);
}

x10_boolean CharNatives::isWhitespace(x10_char x) {
    /* FIXME: Unicode support */
    return isspace((int) x.v);
}

x10_boolean CharNatives::isISOControl(x10_char x) {
    /* FIXME: Unicode support */
    return iscntrl((int) x.v);
}

x10_char CharNatives::toLowerCase(x10_char x) {
    /* FIXME: Unicode support */
    return (x10_char) tolower((int) x.v);
}

x10_char CharNatives::toUpperCase(x10_char x) {
    /* FIXME: Unicode support */
    return (x10_char) toupper((int) x.v);
}

x10_char CharNatives::toTitleCase(x10_char x) {
    /* FIXME: Proper title case support */
    return toUpperCase(x);
}
// vim:tabstop=4:shiftwidth=4:expandtab
