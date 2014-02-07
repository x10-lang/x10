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

#include <apgas/Task.h>
#include <apgas/Pool.h>

#include <stdio.h>

using namespace apgas;

class FibAsync : public Task {
  public:
    int myN;
    int result;
    FibAsync(int n) : myN(n), result(0) {}

    virtual void execute() {
        if (myN < 2) {
            result = 1;
        } else {
            FibAsync child1(myN-1);
            FibAsync child2(myN-2);
            Task* myTasks[2];
            myTasks[0] = &child1;
            myTasks[1] = &child2;
            myPool->runFinish(2, myTasks);
            result = child1.result + child2.result;
        }
    }
};
    
    
int main(int argc, char **argv) {
    int N = 10;
    if (argc > 1) {
        int n2 = atoi(argv[1]);
        N = n2;
    }
    printf("Computing Fib of %d\n", N);
    
    FibAsync fibTask(N);
    apgas::Pool aPool(&fibTask);
    aPool.start();

    printf("Fib(%d) = %d\n", N, fibTask.result);
}
