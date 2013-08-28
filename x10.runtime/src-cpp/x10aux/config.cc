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

#include <cstdlib>
#include <cstring>
#include <stdio.h>

//#define TM_DEBUG 1
//
//x10_byte x10aux::tm_load_x10_byte(x10_byte * ptr) {
//#ifdef TM_DEBUG
//	printf("tm_load_x10_byte: ptr=%p val=%c\n", (void *)ptr, *ptr);
//#endif
//	return *ptr;
//}
//
//x10_short x10aux::tm_load_x10_short(x10_short * ptr) {
//#ifdef TM_DEBUG
//	printf("tm_load_x10_short: ptr=%p val=%hd\n", (void *)ptr, *ptr);
//#endif
//	return *ptr;
//}
//
//x10_int x10aux::tm_load_x10_int(x10_int * ptr) {
//#ifdef TM_DEBUG
//	printf("tm_load_x10_int: ptr=%p val=%d\n", (void *)ptr, *ptr);
//#endif
//	return *ptr;
//}
//
//x10_long x10aux::tm_load_x10_long(x10_long * ptr) {
//#ifdef TM_DEBUG
//	printf("tm_load_x10_long: ptr=%p val=%lld\n", (void *)ptr, *ptr);
//#endif
//	return *ptr;
//}
//
//x10_long *x10aux::tm_load_x10_class(x10_long ** ptr) {
//#ifdef TM_DEBUG
//	printf("tm_load_x10_class: ptr=%p val=%p\n", (void **)ptr, (void *)*ptr);
//#endif
//	return *ptr;
//}
//
//x10_byte x10aux::tm_assign_x10_byte(x10_byte * ptr, x10_byte val) {
//#ifdef TM_DEBUG
//	printf("tm_assign_x10_byte: ptr=%p new_val=%c\n", (void *)ptr, val);
//#endif
//	*ptr = val;
//	return val;
//}
//
//x10_short x10aux::tm_assign_x10_short(x10_short * ptr, x10_short val) {
//#ifdef TM_DEBUG
//	printf("tm_assign_x10_short: ptr=%p new_val=%hd\n", (void *)ptr, val);
//#endif
//	*ptr = val;
//	return val;
//}
//
//x10_int x10aux::tm_assign_x10_int(x10_int * ptr, x10_int val) {
//#ifdef TM_DEBUG
//	printf("tm_assign_x10_int: ptr=%p new_val=%d\n", (void *)ptr, val);
//#endif
//	*ptr = val;
//	return val;
//}
//
//x10_long x10aux::tm_assign_x10_long(x10_long * ptr, x10_long val) {
//#ifdef TM_DEBUG
//	printf("tm_assign_x10_long: ptr=%p new_val=%lld\n", (void *)ptr, val);
//#endif
//	*ptr = val;
//	return val;
//}
//
//x10_long *x10aux::tm_assign_x10_class(x10_long ** ptr, x10_long *val) {
//#ifdef TM_DEBUG
//	printf("tm_assign_x10_class: ptr=%p new_val=%p\n", (void *)ptr, val);
//#endif
//	*ptr = val;
//	return val;
//}
//
//volatile x10_int x10aux::tm_load_x10_int(volatile x10_int * ptr) {
//#ifdef TM_DEBUG
//	printf("tm_load_x10_int: ptr=%p val=%d\n", (void *)ptr, *ptr);
//#endif
//	return *ptr;
//}
//
//volatile x10_int x10aux::tm_assign_x10_int(volatile x10_int * ptr, volatile x10_int val) {
//#ifdef TM_DEBUG
//	printf("tm_assign_x10_int: ptr=%p new_val=%d\n", (void *)ptr, val);
//#endif
//	*ptr = val;
//	return val;
//}
//
//void x10aux::tm_load_gen(char * ptr, char * p_val, unsigned long type_size) {
//#ifdef TM_DEBUG
//	printf("tm_load_gen: ptr=%p type_size=%lu\n", (void *)ptr, type_size);
//#endif
//	if (1 == type_size) {
//		*(x10_byte *)p_val = x10aux::tm_load_x10_byte((x10_byte *)ptr);
//	} else if (2 == type_size) {
//		*(x10_short *)p_val = x10aux::tm_load_x10_short((x10_short *)ptr);
//	} else if (4 == type_size) {
//		*(x10_int *)p_val = x10aux::tm_load_x10_int((x10_int *)ptr);
//	} else if (8 == type_size) {
//		*(x10_long *)p_val = x10aux::tm_load_x10_long((x10_long *)ptr);
//	} else
//	{
//		printf("ERROR: x10aux::tm_load_gen - unknown type_size [%lu]\n", type_size);
//	}
//}
//
//char g_tm_arr[100] = {0,};
//
//char * x10aux::tm_assign_gen(char * ptr, char * p_val, unsigned long type_size) {
//#ifdef TM_DEBUG
//	printf("tm_assign_gen: ptr=%p type_size=%lu\n", (void *)ptr, type_size);
//#endif
//	for (int i=0; i < type_size; i++)
//	{
//		g_tm_arr[i] = p_val[i];
//	}
//
//	if (1 == type_size) {
//		x10aux::tm_assign_x10_byte((x10_byte *)ptr, *(x10_byte *)p_val);
//	} else if (2 == type_size) {
//		x10aux::tm_assign_x10_short((x10_short *)ptr, *(x10_short *)p_val);
//	} else if (4 == type_size) {
//		x10aux::tm_assign_x10_int((x10_int *)ptr, *(x10_int *)p_val);
//	} else if (8 == type_size) {
//		x10aux::tm_assign_x10_long((x10_long *)ptr, *(x10_long *)p_val);
//	} else
//	{
//		printf("ERROR: x10aux::tm_assign_gen - unknown type_size [%lu]\n", type_size);
//	}
//
//	return (char *)g_tm_arr;
//}
//
//template<class T> T tm_load(T * ptr) {
//	T local_val;
//	printf("tm_load: %lu\n", sizeof(T));
//	x10aux::tm_load_gen((char *)ptr, (char *)&local_val, sizeof(T));
//	return local_val;
//}
//
//template<class T> T tm_assign(T * ptr, T val) {
//	printf("tm_assign: %lu\n", sizeof(T));
//	x10aux::tm_assign_gen((char *)ptr, (char *)&p_val, sizeof(T));
//	return val;
//}
//
//x10_int x10aux::is_tm_phase = 0;
//
//void x10aux::tm_enter() {
//    x10aux::is_tm_phase = 1;
//    return;
//}
//
//void x10aux::tm_exit() {
//	x10aux::is_tm_phase = 0;
//	return;
//}

//template<class T> T x10aux::tm_load(T * ptr) {
//	return *ptr;
//}
//
//template<class T> T x10aux::tm_assign(T * ptr, T val) {
//	*ptr = val;
//	return val;
//}

/*void x10aux::tm_test() {
	x10_int num_int = 0;
	x10_long num_long = 0;

	num_int = x10aux::tm_load<x10_int>(&num_int);
	x10aux::tm_assign<x10_int>(&num_int, 1);

	num_long = x10aux::tm_load<x10_long>(&num_long);
	x10aux::tm_assign<x10_long>(&num_long, 1);

}*/

bool getBoolEnvVar(const char* name) {
    char* value = getenv(name);
    return (value && !(strcasecmp("false", value) == 0) && !(strcasecmp("0", value) == 0) && !(strcasecmp("f", value) == 0));
}

size_t getSizeTEnvVar(const char* name) {
    char* value = getenv(name);
    return value!=NULL ? strtoull(value,NULL,0) : 0;
}

size_t x10aux::getMemSizeEnvVar(const char* name, bool* defined) {
    char *value = getenv(name);
    if (NULL == value) {
        *defined = false;
        return 0;
    } else {
        *defined = true;
        char* end = NULL;
        size_t size = strtoul(value, &end, 10);
        if (NULL != end) {
            if (*end == 'k' || *end == 'K') {
                size *= 1024;
            } else if (*end == 'm' || *end == 'M') {
                size *= (1024 * 1024);
            }
        }
        return size;
    }
}

const bool x10aux::trace_ansi_colors = getBoolEnvVar("X10_TRACE_ANSI_COLORS");
const bool x10aux::trace_static_init = getBoolEnvVar("X10_TRACE_STATIC_INIT") || getBoolEnvVar("X10_TRACE_ALL");
const bool x10aux::trace_x10rt = getBoolEnvVar("X10_TRACE_X10RT") || getBoolEnvVar("X10_TRACE_NET") || getenv("X10_TRACE_ALL");
const bool x10aux::trace_ser = getBoolEnvVar("X10_TRACE_SER") || getBoolEnvVar("X10_TRACE_NET") || getBoolEnvVar("X10_TRACE_ALL");
const bool x10aux::trace_rxtx = getBoolEnvVar("X10_TRACE_RXTX") || getBoolEnvVar("X10_TRACE_NET") || getBoolEnvVar("X10_TRACE_ALL");

const bool x10aux::disable_dealloc = getBoolEnvVar("X10_DISABLE_DEALLOC");

const bool x10aux::x10__assertions_enabled = !getBoolEnvVar("X10_DISABLE_ASSERTIONS");

const bool x10aux::x10_native_debug_messages = getBoolEnvVar("X10_NATIVE_DEBUG_MESSAGES");

const bool x10aux::congruent_huge = getBoolEnvVar(ENV_CONGRUENT_HUGE);

char* x10aux::get_congruent_base() {
    return getenv(ENV_CONGRUENT_BASE);
}

char* x10aux::get_congruent_size() {
    return getenv(ENV_CONGRUENT_SIZE);
}

const size_t x10aux::congruent_offset = getSizeTEnvVar(ENV_CONGRUENT_OFFSET);
const size_t x10aux::congruent_period = getSizeTEnvVar(ENV_CONGRUENT_PERIOD);

size_t x10aux::get_remote_op_batch (void)
{
    const char *env = getenv("X10_REMOTE_OP_BATCH");
    if (env == NULL) return 64;
    return strtoul(env, NULL, 10);
}

const bool x10aux::static_broadcast_naive = getBoolEnvVar("X10_STATIC_BROADCAST_NAIVE");
