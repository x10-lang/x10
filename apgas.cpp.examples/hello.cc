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

#include <apgas/Task.h>
#include <apgas/Pool.h>

#include <stdio.h>

using namespace apgas;

class MyTask : public Task {
  private:
    char *msg;
  public:
    MyTask(char *m) : msg(m) {}

    virtual void execute() {
        printf("\tI will say %s\n",msg);
    }
};

class MyFinish : public Task {
  private:
    int argc;
    char **argv;
  public:
    MyFinish(int ac, char** av) : argc(ac), argv(av) {}
    virtual void execute() {
        for (int i=1; i< argc; i++) {
            MyTask* task = new (Pool::alloc<MyTask>()) MyTask (argv[i]);
            myPool->runAsync(task);
        }
    }
};

class MyMain : public Task {
  private:
    int argc;
    char **argv;
  public:
    MyMain(int ac, char** av) : argc(ac), argv(av) {}
    virtual void execute() {
        MyFinish finish(argc, argv);
        printf("Hello World, I have %d things to say\n", argc-1);
        myPool->runFinish(&finish);
        printf("Goodbye World, I am done talking\n");
    }
};


int main(int argc, char **argv) {
    MyMain m(argc, argv);
    apgas::Pool aPool(&m);
    aPool.start();
}
