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

#ifndef X10TM_H
#define X10TM_H

#if defined(__CYGWIN__) || defined(__FreeBSD__)
#undef __STRICT_ANSI__ // Strict ANSI mode is too strict in Cygwin and FreeBSD
#endif


namespace x10tm {

	typedef struct _TMThread {
		int id;
	} TMThread;

    char tm_load_char(char * ptr);
    short tm_load_short(short * ptr);
    int tm_load_int(int * ptr);
    long tm_load_long(long * ptr);
    long *tm_load_x10_class(long ** ptr);
    void tm_load_struct(char * ptr, char * p_val, unsigned long type_size);

    void tm_load_gen(TMThread *SelfTM, char * ptr, char * p_val, unsigned long type_size);

    template<class T> extern T tm_load(TMThread *SelfTM, T * ptr) {
    	T local_val;
    	//printf("tm_load: %lu\n", sizeof(T));
    	tm_load_gen(SelfTM, (char *)ptr, (char *)&local_val, sizeof(T));
    	return local_val;
    }

    template<class T> extern T tm_load(TMThread *SelfTM, volatile T * ptr) {
        	T local_val;
        	//printf("tm_load: %lu\n", sizeof(T));
        	tm_load_gen(SelfTM, (char *)ptr, (char *)&local_val, sizeof(T));
        	return local_val;
        }

    char tm_assign_char(char * ptr, char val);
    short tm_assign_short(short * ptr, short val);
    int tm_assign_int(int * ptr, int val);
    long tm_assign_long(long * ptr, long val);
    long *tm_assign_x10_class(long ** ptr, long *val);

    char *tm_assign_gen(TMThread *SelfTM, char * ptr, char * val, unsigned long type_size);

    template<class T> extern T tm_assign(TMThread *SelfTM, T * ptr, T val) {
    	//printf("tm_assign: %lu\n", sizeof(T));
    	tm_assign_gen(SelfTM, (char *)ptr, (char *)&val, sizeof(T));
    	return val;
    }

    template<class T> extern T tm_assign(TMThread *SelfTM, volatile T * ptr, T val) {
        	//printf("tm_assign: %lu\n", sizeof(T));
        	tm_assign_gen(SelfTM, (char *)ptr, (char *)&val, sizeof(T));
        	return val;
        }



    volatile int tm_assign_int(volatile int * ptr, volatile int val);
    volatile int tm_load_int(volatile int * ptr);

    /*extern void tm_enter() {
    	x10aux::is_tm_phase = 1;
    	return;
    }
    extern void tm_exit() {
    	x10aux::is_tm_phase = 0;
        return;
    }

    template<class T> extern T tm_load(T * ptr) {
    	return *ptr;
    }

    template<class T> extern T tm_assign(T * ptr, T val) {
		*ptr = val;
		return val;
    }*/
    void tm_test();
    TMThread *tm_enter();
    void tm_exit(TMThread *SelfTM);
    TMThread *tm_enter__tm__();
    void tm_exit__tm__(TMThread *SelfTM);

    //template<class T> T tm_load(T * ptr);
    //template<class T> T tm_assign(T * ptr, T val);

    /*template<class T> class TM  {
    	public:
    	T tm_load(T * ptr) {
    		return *ptr;
    	}

    	T tm_assign(T * ptr, T val) {
    		*ptr = val;
    		return val;
    	}
    };*/
    // Alex - TM - e

}

#endif

