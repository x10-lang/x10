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

#include <stdio.h>

#include <apgas/Task.h>
#include <apgas/Pool.h>

namespace apgas {

    Pool::Pool(Task* mainTask);

    void Pool::start() {
        fprintf(stderr, "TODO: implement Pool.start\n");
    }
        
    void Pool::runAsync(Task* task) {
        fprintf(stderr, "TODO: implement Pool.runAsync\n");
    }
        
    void Pool::runFinish(Task* task) {
        fprintf(stderr, "TODO: implement Pool.runAsync\n");
    }        
}
