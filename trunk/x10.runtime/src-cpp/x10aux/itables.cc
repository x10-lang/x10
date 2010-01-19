#include <stdio.h>

#include <x10aux/itables.h>

using namespace x10aux;
using namespace x10::lang;

void* x10aux::outlinedITableLookup(itable_entry* itables, RuntimeType* targetInterface) {
    if (targetInterface->paramsc > 0) {
        // Have to look again considering type parameters.
        // Note: it would be wrong to just call subtypeOf(itables[i].id, targetInterface)
        // because we must ensure that itables[i].id->canonical == targetInterface->canonical
        // so that the returned itable has the layout the caller of findITable is expecting.
        for (int i=0; true; i++) {
            const RuntimeType *candidate = itables[i].id;
            if (NULL == candidate) {
                // Have hit the end of itables again; this is a lookup failure
                break;
            }
            if (candidate->canonical == targetInterface->canonical) {
                assert(candidate->paramsc == targetInterface->paramsc);
                bool allCompatible = true;
                for (int j=0; j<candidate->paramsc; j++) {
                    assert(targetInterface->variances[j] == candidate->variances[j]);
                    assert(candidate->params[j] != NULL);
                    assert(targetInterface->params[j] != NULL);
                    switch(candidate->variances[j]) {
                    case RuntimeType::covariant:
                        allCompatible = allCompatible && candidate->params[j]->subtypeOf(targetInterface->params[j]);
                        break;
                    case RuntimeType::contravariant:
                        allCompatible = allCompatible && targetInterface->params[j]->subtypeOf(candidate->params[j]);
                        break;
                    case RuntimeType::invariant:
                        allCompatible = allCompatible && candidate->params[j]->equals(targetInterface->params[j]);
                        break;
                    }
                }
                if (allCompatible) {
                    return itables[i].itable;
                }
            }
        }
    }

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
    abort();
}
