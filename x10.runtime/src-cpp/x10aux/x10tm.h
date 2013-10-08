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

#include <setjmp.h>

#define MAX_RS (512)
#define MAX_WS (256)
#define MAX_NESTING (100)

namespace x10tm {
	typedef struct _LogEntry {
		union {
			volatile long *addr_l;
			volatile int *addr_i;
			volatile short *addr_s;
			volatile char *addr_c;
		};
		union {
			volatile long old_value_l;
			volatile int old_value_i;
			volatile short old_value_s;
			volatile char old_value_c;
		};

		long old_version;
		long stripe_index;
		int type_size;
	} LogEntry;

	typedef struct _RSEntry {
		long stripe_index;
	} RSEntry;

	typedef struct _TMThread {
		long is_init;
		long n_nesting_level;
		long place_id;
		long uniq_id;
		long th_id_system;
		long is_aborted;
		long last_result;

		RSEntry read_set[MAX_RS];
		LogEntry log_set[MAX_WS];
		int rs_max_index;
		int ls_max_index;

		long rv;
		jmp_buf * on_failure[MAX_NESTING];
		jmp_buf abort_retry[MAX_NESTING];

		long num_of_cur_aborts;
		long num_of_aborts;
		long num_of_success;

	} TMThread;

    void tm_load_gen(TMThread *SelfTM, char * ptr, char * p_val, unsigned long type_size);
    char *tm_store_gen(TMThread *SelfTM, char * ptr, char * val, unsigned long type_size);

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

    template<class T> extern T tm_store(TMThread *SelfTM, T * ptr, T val) {
    	tm_store_gen(SelfTM, (char *)ptr, (char *)&val, sizeof(T));
    	return val;
    }

    /*template<class T> extern T tm_store(TMThread *& SelfTM, T * ptr, T val) {
		tm_store_gen(SelfTM, (char *)ptr, (char *)&val, sizeof(T));
		return val;
	}*/

    template<class T> extern T tm_store(TMThread *SelfTM, volatile T * ptr, T val) {
		tm_store_gen(SelfTM, (char *)ptr, (char *)&val, sizeof(T));
		return val;
	}

    void tm_system_init(long place_id);
    void tm_system_finish(long place_id);

    long tm_get_next_thread_id();

    void tm_thread_init(TMThread *SelfTM, void *p_x10tm_obj, long place_id, long th_id);
    void tm_thread_finish(TMThread *SelfTM);

    long tm_thread_get_uniq_id(TMThread *SelfTM);

    long tm_thread_get_result(int tm_thread_id);
    long tm_thread_abort(int tm_thread_id);

    void tm_start(TMThread *SelfTM, jmp_buf *jb);
	void tm_commit(TMThread *SelfTM);
	void tm_commit_place(TMThread *SelfTM, int is_success);
	void tm_commit_closure(TMThread *SelfTM);
    void tm_start__tm__(TMThread *SelfTM, jmp_buf *jb);
    void tm_commit__tm__(TMThread *SelfTM);


    TMThread *tm_get_self(int place_id, void *p_x10tm_obj, long th_id);



}

#define TM_START__tm__(SelfTM, AbortActions) TM_START(SelfTM, AbortActions)

#define TM_START(SelfTM, AbortActions) {            \
  if (setjmp (SelfTM->abort_retry[SelfTM->n_nesting_level]) != 0) {       \
	x10aux::event_probe(); \
	AbortActions;                                   \
  }                                                 \
  tm_start (SelfTM, &SelfTM->abort_retry[SelfTM->n_nesting_level]) ;           \
}

#define TM_START_CLOSURE_VOID(SelfTM) {            \
  SelfTM->last_result = 1; \
  if (setjmp (SelfTM->abort_retry[SelfTM->n_nesting_level]) != 0) {       \
	x10aux::event_probe(); \
	x10::lang::X10TM::failCommits__tm__(SelfTM, SelfTM->uniq_id); \
	SelfTM->last_result = 0; \
	return;                     \
  }                                                 \
  tm_start(SelfTM, &SelfTM->abort_retry[SelfTM->n_nesting_level]) ;           \
}

#define TM_START_CLOSURE_RET(SelfTM, ret_var) {            \
  SelfTM->last_result = 1; \
  if (setjmp (SelfTM->abort_retry[SelfTM->n_nesting_level]) != 0) {       \
	x10aux::event_probe(); \
	x10::lang::X10TM::failCommits__tm__(SelfTM, SelfTM->uniq_id); \
  	SelfTM->last_result = 0; \
    return ret_var;                     \
  }                                                 \
  tm_start(SelfTM, &SelfTM->abort_retry[SelfTM->n_nesting_level]) ;           \
}

#define TM_END__tm__(SelfTM) TM_END(SelfTM)

#define TM_END(SelfTM) { tm_commit (SelfTM); }

#define TM_END_CLOSURE(SelfTM) { tm_commit_closure (SelfTM); }

#define TM_END_PLACE(SelfTM, is_success) { tm_commit_place(SelfTM, is_success); }

#endif

