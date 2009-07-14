#include <x10aux/itables.h>
#include <x10/lang/Object.h>

using namespace x10aux;
using namespace x10::lang;

#define TRACE_ITABLES 1

void*
x10aux::findITable(ref<Object> obj, const RuntimeType **id) {
#if TRACE_ITABLES
    printf("Begin ITable search on %p for interface %p\n", obj.get(), id);
    printf("\tObject RTT is %s\n", obj->_type()->name());
    printf("\tInterface RTT is %s\n", (*id)->name());
#endif
    itable_entry* itables = obj->_getITables();
#if TRACE_ITABLES
    printf("\tFound itables array %p\n", itables);
#endif    
    
    int i = 0;
    do {
#if TRACE_ITABLES
        printf("\tChecking %d:  %p\n", i, itables[i].id);
        printf("\t\tRTT of candidate %s\n", (*(itables[i].id))->name());
#endif        
        if (itables[i].id == id) {
#if TRACE_ITABLES
            printf("\tMatch: Returning %p\n", itables[i].itable);
#endif            
            return itables[i].itable;
        }
    } while (itables[++i].id != 0);
    
    fprintf(stderr, "ITable search failed: receiver class %s, target interface %s\n", obj->_type()->name(), (*id)->name());
    abort();
}
