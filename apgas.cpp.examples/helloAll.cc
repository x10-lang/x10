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

#include <apgas/Task.h>
#include <apgas/RemoteTask.h>
#include <apgas/Runtime.h>

#include <x10/lang/String.h>

#include <stdio.h>

using namespace apgas;

class MyTask : public RemoteTask {
  private:
    x10::lang::String* msg;
  public:
    MyTask(x10::lang::String* m) : msg(m) {}

    static const ::x10aux::serialization_id_t _serialization_id;
    static const ::x10aux::serialization_id_t _network_id;
    ::x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; }
    ::x10aux::serialization_id_t _get_network_id() { return _network_id; }
    
    void _serialize_body(::x10aux::serialization_buffer &buf) {
        buf.write(this->msg);
    }
    
    static x10::lang::Reference* _deserialize(::x10aux::deserialization_buffer &buf) {
        MyTask* storage = Runtime::alloc<MyTask>();
        buf.record_reference(storage);
        x10::lang::String* m = buf.read<x10::lang::String*>();
        MyTask* task = new (storage) MyTask(m);
        return task;
    }
    
    virtual void execute() {
        printf("\tAt Place(%d), I will say %s\n", getRuntime()->here(), msg->c_str());
    }
};

const ::x10aux::serialization_id_t MyTask::_serialization_id =
    ::x10aux::DeserializationDispatcher::addDeserializer(MyTask::_deserialize);
const ::x10aux::serialization_id_t MyTask::_network_id = 
    ::x10aux::NetworkDispatcher::addNetworkDeserializer(MyTask::_deserialize, ::x10aux::CLOSURE_KIND_ASYNC_CLOSURE);


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
            int np = myRuntime->numPlaces();
            for (int i=0; i<numTasks; i++) {
                RemoteTask* t = new (Runtime::alloc<MyTask>()) MyTask (x10::lang::String::Steal(argv[i+1]));
                myRuntime->runAsyncAt(i % myRuntime->numPlaces(), t);
            }
        } else {
            printf("Please give me something to say on the command line\n");
        }
    }
};


int main(int argc, char **argv) {
    MyMain m(argc, argv);

    Runtime* rt = Runtime::getRuntime();
    rt->start(argc, argv);
    rt->runSync(&m);
    printf("Goodbye World, I am done talking\n");
    rt->terminate();

    printf("exiting from main at %d\n", rt->here());
}
