/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

#include <x10aux/config.h>
#include <x10aux/string_utils.h>
#include <x10aux/rail_utils.h>
#include <x10aux/alloc.h>
#include <x10aux/math.h>

#include <x10/lang/String.h>
#include <x10/lang/Rail.h>

using namespace x10::lang;
using namespace x10aux;

ref<String> x10aux::nullString = String::Lit("null");


Rail<ref<String> > *x10aux::convert_args(int ac, char **av) {
    assert(ac>=1);
    x10_int x10_argc = ac  - 1;
    Rail<ref<String> > *arr = alloc_rail<ref<String>, Rail<ref<String> > > (x10_argc);
    for (int i = 1; i < ac; i++) {
        ref<String> val = String::Lit(av[i]);
        (*arr)[i-1] = val;
    }
    return arr;
}

void x10aux::free_args(ref<Rail<ref<String> > > arr) {
    if (arr==x10aux::null) return;
    //std::cerr << "free_args: freeing " << arr->FMGL(length) << " elements" << std::endl;
    //x10_int length = arr->length;
    //std::cerr << "free_args: freeing array " << arr.get() << std::endl;
    free_rail<ref<String>, Rail<ref<String> > >(arr);
    //std::cerr << "free_args: freed array " << arr.get() << std::endl;
}

ref<String> x10aux::vrc_to_string(ref<ValRail<x10_char> > v) {
    nullCheck(v);
    char *str = alloc<char>(v->FMGL(length)+1);
    for (int i = 0; i < v->FMGL(length); ++i)
        str[i] = (*v)[i].v;
    str[v->FMGL(length)] = '\0';
    return String::Steal(str);
}

// vim:tabstop=4:shiftwidth=4:expandtab
