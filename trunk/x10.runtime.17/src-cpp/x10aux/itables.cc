#include <stdio.h>

#include<x10rt17.h>

using namespace x10aux;
using namespace x10::lang;

void x10aux::reportITableLookupFailure(itable_entry* itables, RuntimeType* targetInterface) {
    fprintf(stderr, "\nITable lookup failure!!\n");
    fprintf(stderr, "\tRTT of interface: %p: %s\n", (void*)targetInterface, targetInterface->name());
    fprintf(stderr, "\tRTT of interfaces implemented by receiver\n");
    int i = 0;
    for (; itables[i].id != 0; i++) {
        fprintf(stderr, "\t\t%p %s\n", (void*)(itables[i].id), itables[i].id->name());
    }
    fprintf(stderr, "\tRTT of receiver %p %s\n", itables[i].itable, itables[i].itable == NULL ? "NULL!" : ((RuntimeType*)(itables[i].itable))->name());
    fprintf(stderr, "\n");
    fflush(stderr);
}
