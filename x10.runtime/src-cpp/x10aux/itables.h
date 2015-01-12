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

#ifndef X10AUX_ITABLES_H
#define X10AUX_ITABLES_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

/*
 * Implementation of X10 interface dispatching using "searched itables."
 * A description of a number of alternative interface dispatching
 * mechanisms for Java, including this one, can be found in a paper by 
 * Alpern et al published in OOPSLA'01
 * http://portal.acm.org/citation.cfm?doid=504282.504291.
 *
 * The basic idea is that interfaces generate secondary vtable-like structures,
 * known as itables. For every <class C, interface I> pair where C implements I,
 * a unique itable is defined as part of the C's meta-data. Each itable for C
 * is stored (along with I's unique id) in an array of itables reachable from
 * instances of C via the _getITables method of Reference.  To dispatch an interface
 * invocation, the generated code first calls findITable, passing in the
 * target object and the unique id of the interface being invoked.  findItable
 * gets the objects itables array, compares ids to find the right itable, and
 * returns it.  This returned itable is then indexed at a statically known offset
 * to get the desired function pointer, which is then invoked.
 *
 * There are a couple ways in which the implementation of itables in C++ for X10
 * diverges from the usual implementation for Java in JVMs.
 *   (a) C++ won't easily let us directly insert a pointer to a virtual function
 *       in the itable (whose nominal type needs to have a first argument of Reference
 *       instead of C*).  Therefore we have to use static method thunks to a pointer
 *       to a function that casts an explicit receiver parameter and invokes the
 *       appropriate virtual method on it.  There is a way to use templates to
 *       work around this, but it results in more complex itable instantiation code,
 *       so we are deferring using it in the initial implementation.
 *   (b) We're using the address of the interface'ss rtt field as the interface id
 *       instead of generating a unique integer. This is done to optimize the calling
 *       sequence for invokeinterface, since the address of the rtt field is a
 *       link-time constant.
 *   (c) X10 doesn't suffer from Java's "incompatible class change error" problem
 *       because it has a less dynamic notion of linking a program.  This implies that
 *       (modulo compiler bugs), a search of an itable will always succeed.
 *       For debugging purposes, we still pad itables with an extra sentinel entry,
 *       but failing to find an itable is a fatal error that aborts the program,
 *       not a runtime exception.
 */

namespace x10 { namespace lang { class Reference; }}

namespace x10aux {
    class RuntimeType;

    /*
     * An itables array is an array of itable_entry.
     * Each interface declares a unique struct for its itable,
     * therfore in the itable entry it must be declared as a void*.
     */
    struct itable_entry {
        itable_entry(const RuntimeType* (initFunction_)(), void* itable_) : id(NULL), itable(itable_), initFunction(initFunction_) {}
        volatile const RuntimeType* id;
        void* itable;
        const RuntimeType* (*initFunction)();
    };

    void* outlinedITableLookup(itable_entry* itables, const RuntimeType* targetInterface);
    
    /*
     * Search itables to find the itable that matches I and return it.
     * The inline sequence simply does a series of == tests to handle the
     * common case.  If that fails then either itable lookup failed, or
     * I is a generic type and we need to do a more complex series of tests
     * to find the desired interface. To avoid code space bloat, both of these
     * cases are handled in the out-of-line outlinedITableLookup routine.
     */
    template<class Iface> inline typename Iface::template itable< ::x10::lang::Reference>* findITable(itable_entry* itables) {
        const RuntimeType *id = &Iface::rtt; // NOTE: Iface::rtt may be uninitialized, but that's ok here. Make common case as fast as possible.
        for (int i=0; true; i++) {
            if (itables[i].id == id) {
                return (typename Iface::template itable< ::x10::lang::Reference>*)(itables[i].itable);
            }
            if (NULL == itables[i].id) {
                // Either itables hasn't been initialized yet, or we've hit the end of itables and
                // we need to deal with complex cases involving generic types.
                // By calling getRTT<I>(), we ensure that I::rtt will now be initialized before we need to look at its content.
                // in the body of outlineITableLookup.
                return (typename Iface::template itable< ::x10::lang::Reference>*)outlinedITableLookup(itables, ::x10aux::getRTT<Iface>());
            }
        }
    }

}

#endif
