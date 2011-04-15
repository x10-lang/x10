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


// x10.util.Vec is a special case -- its declaration cannot be generated from its type becaues the implementing class
// has 2 template params whereas the X10 type has 1 generic type parameter.

// In fact the C++ backend attempts to give it a declaration in the normal fashion which looks like
// x10::util::Vec<T>  This is the reason it has a different name here -- to avoid a clash.

// The actual definition is found in src-cpp/x10/util/Vec.h

namespace x10 { namespace util {
    template<class FMGL(T), x10_int SZ> class NativeVec;
} }

