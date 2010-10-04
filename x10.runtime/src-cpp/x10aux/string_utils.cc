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
#include <x10/array/Array.h>

using namespace x10::array;
using namespace x10::lang;
using namespace x10aux;

ref<Array<ref<String> > >x10aux::convert_args(int ac, char **av) {
    assert(ac>=1);
    x10_int x10_argc = ac  - 1;
    ref<Array<ref<String> > > arr(Array<ref<String> >::_make(x10_argc));
    for (int i = 1; i < ac; i++) {
        ref<String> val = String::Lit(av[i]);
        arr->set(val, i-1);
    }
    return arr;
}

ref<String> x10aux::vrc_to_string(ref<Rail<x10_char> > v) {
    nullCheck(v);
    char *str = alloc<char>(v->FMGL(length)+1);
    for (int i = 0; i < v->FMGL(length); ++i)
        str[i] = (*v)[i].v;
    str[v->FMGL(length)] = '\0';
    return String::Steal(str);
}

ref<String> x10aux::string_utils::lit(const char* s) {
    return String::Lit(s);
}

const char* x10aux::string_utils::cstr(ref<String> s) {
    return s->c_str();
}

// vim:tabstop=4:shiftwidth=4:expandtab
