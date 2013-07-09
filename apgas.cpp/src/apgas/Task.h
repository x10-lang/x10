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

#ifndef APGAS_TASK_H
#define APGAS_TASK_H

namespace apgas {
    class Pool;
    
    class Task {
    protected:
        Pool* myPool;
    public:
        virtual void execute() = 0;
        void setPool(Pool* p) { myPool = p; }
        Pool *getPool() { return myPool; }
    };


}



#endif /* APGAS_TASK_H */
