#ifndef X10AUX_ITABLES_H
#define X10AUX_ITABLES_H

#include <x10aux/config.h>
#include <x10aux/ref.h>

/*
 * Implementation of X10 interface dispatching using "searched itables."
 * A description of a number of alternative interface dispatching
 * mechanisms for Java, including this one, can be found in a paper by 
 * Alpern et al published in OOPSLA'01 http://portal.acm.org/citation.cfm?doid=504282.504291.
 *
 * The basic idea is that interfaces generate secondary vtable-like structures,
 * known as itables. For every <class C, interface I> pair where C implements I,
 * a unique itable is defined as part of the C's meta-data. Each itable for C
 * is stored (along with I's unique id) in an array of itables reachable from
 * instances of C via the _getITables method of Object.  To dispatch an interface
 * invocation, the generated code first calls findITable, passing in the
 * target object and the unique id of the interface being invoked.  findItable
 * gets the objects itables array, compares ids to find the right itable, and
 * returns it.  This returned itable is then indexed at a statically known offset
 * to get the desired function pointer, which is then invoked.
 *
 * There are a couple ways in which the implementation of itables in C++ for X10
 * diverges from the usual implementation for Java in JVMs.
 *   (a) C++ won't let us directly insert a pointer to a virtual function
 *       in the itable (whose nominal type needs to have a first argument of Object
 *       instead of C*).  Therefore we have to use static method thunks to a pointer
 *       to a function that casts an explicit receiver parameter and invokes the
 *       appropriate virtual method on it.
 *   (b) We're using the address of the interfaces rtt field as the interface id instead
 *       of generating a unique integer (or using the actual pointer to I's RuntimeType object).
 *       This is done to optimize the calling sequence for invokeinterface, since the address
 *       of the rtt field is a link-time constant.
 *   (c) X10 doesn't suffer from Java's "incompatible class change error" problem, because it
 *       has a less dynamic notion of linking a program.  This implies that (modulo compiler bugs),
 *       a search of an itable will always succeed.  For debugging purposes, we still pad itables
 *       with an extra sentinel entry, but failing to find an itable is a fatal error that aborts
 *       the program, not a runtime exception.
 */


namespace x10 { namespace lang { class Object; }}

namespace x10aux {
    class RuntimeType;
    
    /*
     * An itables array is an array of itable_entry.
     * Each interface declares a unique struct for its itable,
     * therfore in the itable entry it must be declared as a void*.
     */
    struct itable_entry {
        itable_entry(RuntimeType** id_, void* itable_) : id(id_), itable(itable_) {}
        RuntimeType** id;
        void* itable;
    };

    /*
     * Find the itable for obj that matches the given id
     */
    extern void* findItable(ref<x10::lang::Object> obj, RuntimeType **id);
}

#define X10_ITABLE_INVOKE(obj, id, type, method) ((type)(x10aux::findItable(obj, id)))->method

#endif
