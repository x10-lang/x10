/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2013.
 */

#ifndef __X10_LANG_REMOTEOPS_H
#define __X10_LANG_REMOTEOPS_H

#include <x10aux/config.h>

namespace x10 {
    namespace lang { 
        template<class T> class GlobalRef;
        template<class T> class Rail;

        class RemoteOps {
        public:
            static void remoteAdd(GlobalRef<Rail<x10_ulong>*> target, x10_long idx, x10_ulong val);
            static void remoteAdd(GlobalRef<Rail<x10_long>*> target, x10_long idx, x10_long val);

            static void remoteAnd(GlobalRef<Rail<x10_ulong>*> target, x10_long idx, x10_ulong val);
            static void remoteAnd(GlobalRef<Rail<x10_long>*> target, x10_long idx, x10_long val);

            static void remoteOr(GlobalRef<Rail<x10_ulong>*> target, x10_long idx, x10_ulong val);
            static void remoteOr(GlobalRef<Rail<x10_long>*> target, x10_long idx, x10_long val);

            static void remoteXor(GlobalRef<Rail<x10_ulong>*> target, x10_long idx, x10_ulong val);
            static void remoteXor(GlobalRef<Rail<x10_long>*> target, x10_long idx, x10_long val);
        };
    }
}

#endif /* __X10_LANG_REMOTEOPS_H */
        
