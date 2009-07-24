#include <stdio.h>

#include<x10rt17.h>

using namespace x10aux;
using namespace x10::lang;

void x10aux::reportITableLookupFailure(itable_entry* itables, RuntimeType* targetInterface) {
    fprintf(stderr, "\nITable lookup failure!!\n");
    fprintf(stderr, "\tTarget interface was %s\n", targetInterface->name());
    fprintf(stderr, "\tInterfaces actually implemented by receiver\n");
    for (int i=0; itables[i].id != 0; i++) {
        fprintf(stderr, "\t\t%s\n", itables[i].id->name());
    }
    fprintf(stderr, "\n");
    fflush(stderr);
}
