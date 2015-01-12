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

#include <apgas/Runtime.h>
#include <apgas/Task.h>

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

class MyMain : public Task {
  private:
    int argc;
    char **argv;
  public:
    MyMain(int ac, char** av) : argc(ac), argv(av) {}
    virtual void execute() {
        if (argc-1 > 0) {
            printf("Hello World, I have %d things to say\n", argc-1);
            int numTasks = argc-1;
            Task** myTasks = Runtime::alloc<Task*>(numTasks*sizeof(Task*));
            for (int i=0; i<numTasks; i++) {
                myTasks[i] = new (Runtime::alloc<MyTask>()) MyTask (argv[i+1]);
            }
            myRuntime->runFinish(numTasks, myTasks);
            printf("Goodbye World, I am done talking\n");
            for (int i=0; i<numTasks; i++) {
                Runtime::dealloc(myTasks[i]);
            }
            Runtime::dealloc(myTasks);
        } else {
            printf("Please give me something to say on the command line\n");
        }
    }
};


int main(int argc, char **argv) {
    MyMain m(argc, argv);

    Runtime* rt = Runtime::getRuntime();
    rt->start(argc, argv);
    if (rt->here() == 0) {
        rt->runSync(&m);
        rt->terminate();
    }
}
