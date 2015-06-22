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

#include <errno.h>

#include <x10/lang/Double.h>
#include <x10/lang/String.h>
#include <x10/lang/NumberFormatException.h>

using namespace x10::lang;
using namespace x10aux;

String* DoubleNatives::toHexString(x10_double value) {
    (void) value;
    UNIMPLEMENTED("toHexString");
    return NULL;
}

String* DoubleNatives::toString(x10_double value) {
    return to_string(value);
}

x10_double DoubleNatives::parseDouble(String* s) {
    const char *start = nullCheck(s)->c_str();
    char *end;
    // skip over leading whitespace
    int numChars = s->length();
    while (isspace(*start)) {
        start++;
        numChars--;
    }
    // attempt to parse as double
    errno = 0;
    x10_double ans = strtod(start, &end);
    if (errno != 0) {
        x10aux::throwException(x10::lang::NumberFormatException::_make(s));
    }
    // any trailing characters must be whitespace
    if (end-start != numChars) {
        for (int i = end-start; i<numChars; i++) {
            if (!isspace(start[i])) {
                // trailing non-space...error!
                x10aux::throwException(x10::lang::NumberFormatException::_make(s));
            }
        }
    }
    
    return ans;
}

// vim:tabstop=4:shiftwidth=4:expandtab
