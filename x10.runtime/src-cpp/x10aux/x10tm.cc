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

#include "x10tm.h"
#include "atomic_ops.h"

#include <stdio.h>
#include <unistd.h>
#include <pthread.h>

//#define IS_DEBUG_TRACE
//#define IS_DEBUG_ABORT
//#define IS_DEBUG_RESTORE_LOG
//#define IS_DEBUG_START
//#define IS_DEBUG_COMMIT
//#define IS_DEBUG_CLOSURE


#ifdef IS_DEBUG_TRACE
#define TRACE(format, ...) printf(format, __VA_ARGS__)
#else
#define TRACE(format, ...)
#endif

#ifdef IS_DEBUG_START
#define TRACE_START(format, ...) printf(format, __VA_ARGS__)
#else
#define TRACE_START(format, ...)
#endif

#ifdef IS_DEBUG_COMMIT
#define TRACE_COMMIT(format, ...) printf(format, __VA_ARGS__)
#else
#define TRACE_COMMIT(format, ...)
#endif

#ifdef IS_DEBUG_ABORT
#define TRACE_ABORT(format, ...) printf(format, __VA_ARGS__)
#else
#define TRACE_ABORT(format, ...)
#endif

#ifdef IS_DEBUG_RESTORE_LOG
#define TRACE_RESTORE_LOG(format, ...) printf(format, __VA_ARGS__)
#else
#define TRACE_RESTORE_LOG(format, ...)
#endif

#ifdef IS_DEBUG_CLOSURE
#define TRACE_CLOSURE(format, ...) printf(format, __VA_ARGS__)
#else
#define TRACE_CLOSURE(format, ...)
#endif

#define MAX_THREADS (40)
#define LOCKS_NUM (1 << 18)
#define LOCKS_MASK (LOCKS_NUM-1)
#define STRIPE_SHIFT (6)
#define STRIPE_INDEX(addr) (((unsigned long)addr >> 6) & LOCKS_MASK)

#define ADDR_ALIGN_TO64BIT(addr) ((long *)((long)addr & ~0x7))
#define ADDR_GET64BIT_OFFSET(addr) ((long)addr & 0x7)

namespace x10tm {
    static TMThread thread_selfs[MAX_THREADS];
    static long locks_table[LOCKS_NUM];

    static int g_tm_process_id = -1;
    static long g_is_tm_system_init = 0;
    static long g_tm_system_init_lock = 0;
    static pthread_mutex_t g_init_lock;
	static long g_thread_id = 0;
	static volatile long g_clock;

	static long g_num_of_success;
	static long g_num_of_aborts;

	void tm_fail() {
		int *p = 0;
		*p = 1;
	}

	long atomic_inc(volatile long *p_num, long delta) {
		long cur_num;
		long new_num;

		while (1) {
			cur_num = *p_num;
			new_num = cur_num + delta;
			if (x10aux::atomic_ops::compareAndSet_64((volatile x10_long *)p_num, cur_num, new_num) == cur_num) {
				return new_num;
			}
		}
	}

	long try_lock(volatile long *p_lock) {
		volatile long cur_lock = *p_lock;
		if (cur_lock == 0) {
			if (x10aux::atomic_ops::compareAndSet_64((volatile x10_long *)p_lock, 0, 1) == 0) {
				return 1;
			}
		}
		return 0;
	}

	long release_lock(volatile long *p_lock) {
		*p_lock = 0;
	}


	void tm_system_init(long place_id) {
		(void)pthread_mutex_init(&g_init_lock, NULL);

		while (!try_lock(&g_tm_system_init_lock)) {}

		if (g_is_tm_system_init) {
			release_lock(&g_tm_system_init_lock);
			return;
		}

		memset(thread_selfs, 0, sizeof(TMThread) * MAX_THREADS);

		for (int i=0; i < LOCKS_NUM; i++) {
			locks_table[i] = 0;
		}

		g_num_of_success = 0;
		g_num_of_aborts = 0;

		g_tm_process_id = (int)getpid();

		g_is_tm_system_init = 1;

		release_lock(&g_tm_system_init_lock);
		printf("tm_system_init: initialized [place_id = %ld, process_id = %d]\n", place_id, g_tm_process_id);

	}

	void tm_system_finish(long place_id) {
		while (!try_lock(&g_tm_system_init_lock)) {}

		if (g_is_tm_system_init == 0) {
			release_lock(&g_tm_system_init_lock);
			return;
		}

		g_is_tm_system_init = 0;
		release_lock(&g_tm_system_init_lock);

		printf("---------------------------------------------\n");
		printf("num_of_success = %ld\n", g_num_of_success);
		printf("num_of_aborts = %ld\n", g_num_of_aborts);
		printf("tm_system_finish: done [place_id = %ld process_id = %d]\n", place_id, g_tm_process_id);
	}


	long tm_get_next_thread_id() {
		long local_id;
		pthread_mutex_lock(&g_init_lock);
		local_id = g_thread_id;
		g_thread_id++;
		pthread_mutex_unlock(&g_init_lock);

		//printf("tm_get_next_thread_id: %ld\n", local_id);
		return local_id;
	}

	void tm_thread_init(TMThread *SelfTM, long place_id, long th_id) {
		if (SelfTM->is_init) {
			return;
		}

		SelfTM->place_id = place_id;
		SelfTM->uniq_id = th_id;
		SelfTM->th_id_system = (long)pthread_self();
		SelfTM->rv = 0;
		SelfTM->num_of_aborts = 0;
		SelfTM->num_of_success = 0;
		SelfTM->num_of_cur_aborts = 0;

		for (int i=0; i < MAX_NESTING; i++) {
			SelfTM->on_failure[i] = NULL;
		}

		SelfTM->is_init = 1;
		printf("[%ld] tm_thread_init: initialized thread uniq_id=%ld system_id=%ld\n", SelfTM->place_id, SelfTM->uniq_id, SelfTM->th_id_system);
		return;
	}

	void tm_thread_finish(TMThread *SelfTM) {
		if (!SelfTM->is_init) {
			printf("[%ld][%ld] tm_thread_finish: SelfTM is not initialized \n", SelfTM->place_id, SelfTM->uniq_id);
			tm_fail();
		}

		atomic_inc(&g_num_of_success, SelfTM->num_of_success);
		atomic_inc(&g_num_of_aborts, SelfTM->num_of_aborts);

		printf("[%ld] tm_thread_finish: finished thread %ld\n", SelfTM->place_id, SelfTM->uniq_id);
		SelfTM->is_init = 0;
		return;
	}

	long tm_thread_get_uniq_id(TMThread *SelfTM) {
		return SelfTM->uniq_id;
	}
	long GVRead() {
		return g_clock << 1;
	}

	long GVGenerate(TMThread *SelfTM) {
		long cur_clock;
		long new_clock;
		TRACE("[%ld][%ld] GVGenerate: start\n", SelfTM->place_id, SelfTM->uniq_id);
		while (1) {
			cur_clock = g_clock;
			new_clock = cur_clock + 1;
			if (x10aux::atomic_ops::compareAndSet_64((volatile x10_long *)&g_clock, cur_clock, new_clock) == cur_clock) {
				TRACE("[%ld][%ld] GVGenerate: new_clock = %ld\n", SelfTM->place_id, SelfTM->uniq_id, new_clock);
				return new_clock << 1;
			}
			TRACE("[%ld][%ld] GVGenerate: failed advancing the clock g_clock = %ld\n", SelfTM->place_id, SelfTM->uniq_id, g_clock);

		}
		TRACE("[%ld][%ld] GVGenerate: abort...\n", SelfTM->place_id, SelfTM->uniq_id);
		tm_fail();
	}

	void tm_restore_log(TMThread *SelfTM) {
		long stripe_index;
		long old_version;
		int type_size;

		TRACE_RESTORE_LOG("[%ld][%ld] tm_abort: restore logged locations\n", SelfTM->place_id, SelfTM->uniq_id);
		for (int index = SelfTM->ls_max_index-1; index >= 0; index--) {
			old_version = SelfTM->log_set[index].old_version;
			type_size = SelfTM->log_set[index].type_size;
			stripe_index = SelfTM->log_set[index].stripe_index;

			switch (type_size) {
			case sizeof(char):
				TRACE_RESTORE_LOG("[%ld][%ld] tm_abort: restore char addr=%p old_val=%d stripe_index=%ld\n",
						SelfTM->place_id,
						SelfTM->uniq_id,
						SelfTM->log_set[index].addr_c,
						SelfTM->log_set[index].old_value_c,
						stripe_index);
				*(SelfTM->log_set[index].addr_c) = SelfTM->log_set[index].old_value_c;
				break;
			case sizeof(short):
				TRACE_RESTORE_LOG("[%ld][%ld] tm_abort: restore short addr=%p old_val=%d stripe_index=%ld\n",
						SelfTM->place_id,
						SelfTM->uniq_id,
						SelfTM->log_set[index].addr_s,
						SelfTM->log_set[index].old_value_s,
						stripe_index);
				*(SelfTM->log_set[index].addr_s) = SelfTM->log_set[index].old_value_s;
				break;
			case sizeof(int):
				TRACE_RESTORE_LOG("[%ld][%ld] tm_abort: restore int addr=%p old_val=%d stripe_index=%ld\n",
						SelfTM->place_id,
						SelfTM->uniq_id,
						SelfTM->log_set[index].addr_i,
						SelfTM->log_set[index].old_value_i,
						stripe_index);
				*(SelfTM->log_set[index].addr_i) = SelfTM->log_set[index].old_value_i;
				break;
			case sizeof(long):
				TRACE_RESTORE_LOG("[%ld][%ld] tm_abort: restore long addr=%p old_val=%ld stripe_index=%ld\n",
						SelfTM->place_id,
						SelfTM->uniq_id,
						SelfTM->log_set[index].addr_l,
						SelfTM->log_set[index].old_value_l,
						stripe_index);
				*(SelfTM->log_set[index].addr_l) = SelfTM->log_set[index].old_value_l;
				break;
			}
			TRACE_RESTORE_LOG("[%ld][%ld] tm_abort: restored\n", SelfTM->place_id, SelfTM->uniq_id);
			locks_table[stripe_index] = old_version;
			TRACE_RESTORE_LOG("[%ld][%ld] tm_abort: locks_table[%ld] = %ld\n", SelfTM->place_id, SelfTM->uniq_id, stripe_index, old_version);
		}

		SelfTM->ls_max_index = 0;
	}

	int tm_abort(TMThread *SelfTM) {

		TRACE_ABORT("[%ld][%ld][%ld][ABORT] tm_abort: start\n", SelfTM->place_id, SelfTM->uniq_id, (long)pthread_self());

		if (SelfTM->n_nesting_level > 0) {
			SelfTM->n_nesting_level--;
		}

		if (SelfTM->n_nesting_level > 0) {
			TRACE_ABORT("[%ld][%ld] tm_abort: nesting level %ld\n", SelfTM->place_id, SelfTM->uniq_id, SelfTM->n_nesting_level);


			if (SelfTM->on_failure[SelfTM->n_nesting_level] != NULL) {
				TRACE_ABORT("[%ld][%ld] tm_abort: making jump\n", SelfTM->place_id, SelfTM->uniq_id);
				SelfTM->is_aborted = 1;
				longjmp (*(SelfTM->on_failure[SelfTM->n_nesting_level]), 1) ;
			} else {
				TRACE_ABORT("[%ld][%ld] tm_abort: ERROR: jump is NULL !!!\n", SelfTM->place_id, SelfTM->uniq_id);
				tm_fail();
			}
		}

		tm_restore_log(SelfTM);

		SelfTM->num_of_aborts++;
		SelfTM->num_of_cur_aborts++;

		TRACE_ABORT("[%ld][%ld] tm_abort: before if jump\n", SelfTM->place_id, SelfTM->uniq_id);
		if (SelfTM->on_failure[SelfTM->n_nesting_level] != NULL) {
			TRACE_ABORT("[%ld][%ld] tm_abort: making jump\n", SelfTM->place_id, SelfTM->uniq_id);
			longjmp (*(SelfTM->on_failure[SelfTM->n_nesting_level]), 1) ;
		}

		TRACE_ABORT("[%ld][%ld] tm_abort: jump failed...\n", SelfTM->place_id, SelfTM->uniq_id);
	}

	long tm_get_thread_lock_mask(TMThread *SelfTM) {
		return (SelfTM->uniq_id << 1) | 1;
	}

	void tm_track_read(TMThread *SelfTM, long stripe_index) {
		SelfTM->read_set[SelfTM->rs_max_index].stripe_index = stripe_index;
		SelfTM->rs_max_index++;
		if (SelfTM->rs_max_index >= MAX_RS) {
			printf("[%ld][%ld] ERROR: RS MAX reached %d\n", SelfTM->place_id, SelfTM->uniq_id, SelfTM->rs_max_index);
		}
	}

	int tm_load_process(TMThread *SelfTM,
						long stripe_index,
						volatile long lock1,
						volatile long lock2) {
		TRACE("[%ld][%ld] tm_load_process: start\n", SelfTM->place_id, SelfTM->uniq_id);

		if (((lock1 & 0x1) == 0) && (lock1 == lock2) && (lock1 <= SelfTM->rv)) {
			tm_track_read(SelfTM, stripe_index);
			TRACE("[%ld][%ld] tm_load_process: track read-set\n", SelfTM->place_id, SelfTM->uniq_id);
			TRACE("[%ld][%ld] tm_load_process: end\n", SelfTM->place_id, SelfTM->uniq_id);
			return 1;
		} else {
			if (lock1 == tm_get_thread_lock_mask(SelfTM)) {
				TRACE("[%ld][%ld] tm_load_process: locked by me\n", SelfTM->place_id, SelfTM->uniq_id);
				TRACE("[%ld][%ld] tm_load_process: return 1\n", SelfTM->place_id, SelfTM->uniq_id);
				return 1;
			}
		}

		TRACE_ABORT("[%ld][%ld] tm_load_process: failed. lock1=%ld lock2=%ld stripe_index=%ld\n", SelfTM->place_id, SelfTM->uniq_id, lock1, lock2, stripe_index);
		return 0;
	}

	char tm_load_char(TMThread *SelfTM, volatile char * addr) {
		TRACE("[%ld][%ld] tm_load_char: start. ptr=%p\n", SelfTM->place_id, SelfTM->uniq_id, addr);
		long stripe_index = STRIPE_INDEX(addr);
		volatile long lock1 = locks_table[stripe_index];
		volatile char value = *addr;
		volatile long lock2 = locks_table[stripe_index];

		TRACE("[%ld][%ld] tm_load_char: stripe_index = %ld\n", SelfTM->place_id, SelfTM->uniq_id, stripe_index);
		if (tm_load_process(SelfTM, stripe_index, lock1, lock2)) {
			TRACE("[%ld][%ld] tm_load_char: success. ptr=%p val=%d\n", SelfTM->place_id, SelfTM->uniq_id, addr, (int)value);
			return value;
		}

		TRACE_ABORT("[%ld][%ld] tm_load_char: abort ptr=%p val=%d stripe_index=%ld\n", SelfTM->place_id, SelfTM->uniq_id, addr, value, stripe_index);
		tm_abort(SelfTM);
	}

	short tm_load_short(TMThread *SelfTM, volatile short * addr) {
		TRACE("[%ld][%ld] tm_load_short: start. ptr=%p\n", SelfTM->place_id, SelfTM->uniq_id, addr);
		long stripe_index = STRIPE_INDEX(addr);
		volatile long lock1 = locks_table[stripe_index];
		volatile short value = *addr;
		volatile long lock2 = locks_table[stripe_index];

		TRACE("[%ld][%ld] tm_load_short: stripe_index = %ld\n", SelfTM->place_id, SelfTM->uniq_id, stripe_index);
		if (tm_load_process(SelfTM, stripe_index, lock1, lock2)) {
			TRACE("[%ld][%ld] tm_load_short: success. ptr=%p val=%d\n", SelfTM->place_id, SelfTM->uniq_id, addr, (int)value);
			return value;
		}

		TRACE_ABORT("[%ld][%ld] tm_load_short: abort ptr=%p val=%d stripe_index=%ld\n", SelfTM->place_id, SelfTM->uniq_id, addr, value, stripe_index);
		tm_abort(SelfTM);
	}

	int tm_load_int(TMThread *SelfTM, volatile int * addr) {
		TRACE("[%ld][%ld] tm_load_int: start. ptr=%p\n", SelfTM->place_id, SelfTM->uniq_id, addr);
		long stripe_index = STRIPE_INDEX(addr);
		volatile long lock1 = locks_table[stripe_index];
		volatile int value = *addr;
		volatile long lock2 = locks_table[stripe_index];

		TRACE("[%ld][%ld] tm_load_int: stripe_index = %ld\n", SelfTM->place_id, SelfTM->uniq_id, stripe_index);
		if (tm_load_process(SelfTM, stripe_index, lock1, lock2)) {
			TRACE("[%ld][%ld] tm_load_int: success. ptr=%p val=%d\n", SelfTM->place_id, SelfTM->uniq_id, addr, (int)value);
			return value;
		}

		TRACE_ABORT("[%ld][%ld] tm_load_int: abort ptr=%p val=%d stripe_index=%ld\n", SelfTM->place_id, SelfTM->uniq_id, addr, value, stripe_index);
		tm_abort(SelfTM);
	}

	long tm_load_long(TMThread *SelfTM, volatile long * addr) {
		TRACE("[%ld][%ld] tm_load_long: start. ptr=%p\n", SelfTM->place_id, SelfTM->uniq_id, addr);
		long stripe_index = STRIPE_INDEX(addr);
		volatile long lock1 = locks_table[stripe_index];
		volatile long value = *addr;
		volatile long lock2 = locks_table[stripe_index];

		TRACE("[%ld][%ld] tm_load_long: stripe_index = %ld\n", SelfTM->place_id, SelfTM->uniq_id, stripe_index);
		if (tm_load_process(SelfTM, stripe_index, lock1, lock2)) {
			TRACE("[%ld][%ld] tm_load_long: success. ptr=%p val=%ld\n", SelfTM->place_id, SelfTM->uniq_id, addr, value);
			return value;
		}

		TRACE_ABORT("[%ld][%ld] tm_load_long: abort ptr=%p val=%ld stripe_index=%ld\n", SelfTM->place_id, SelfTM->uniq_id, addr, value, stripe_index);
		tm_abort(SelfTM);
	}

	void tm_save_to_log_char(TMThread *SelfTM, volatile char * addr, long old_version) {
		SelfTM->log_set[SelfTM->ls_max_index].addr_c = addr;
		SelfTM->log_set[SelfTM->ls_max_index].old_value_c = *addr;
		SelfTM->log_set[SelfTM->ls_max_index].old_version = old_version;
		SelfTM->log_set[SelfTM->ls_max_index].stripe_index = STRIPE_INDEX(addr);
		SelfTM->log_set[SelfTM->ls_max_index].type_size = sizeof(char);
		SelfTM->ls_max_index++;
		if (SelfTM->ls_max_index >= MAX_WS) {
			printf("[%ld][%ld] ERROR: WS MAX reached %d\n", SelfTM->place_id, SelfTM->uniq_id, SelfTM->ls_max_index);
		}
	}

	void tm_save_to_log_short(TMThread *SelfTM, volatile short * addr, long old_version) {
		SelfTM->log_set[SelfTM->ls_max_index].addr_s = addr;
		SelfTM->log_set[SelfTM->ls_max_index].old_value_s = *addr;
		SelfTM->log_set[SelfTM->ls_max_index].old_version = old_version;
		SelfTM->log_set[SelfTM->ls_max_index].stripe_index = STRIPE_INDEX(addr);
		SelfTM->log_set[SelfTM->ls_max_index].type_size = sizeof(short);
		SelfTM->ls_max_index++;
		if (SelfTM->ls_max_index >= MAX_WS) {
			printf("[%ld][%ld] ERROR: WS MAX reached %d\n", SelfTM->place_id, SelfTM->uniq_id, SelfTM->ls_max_index);
		}
	}

	void tm_save_to_log_int(TMThread *SelfTM, volatile int * addr, long old_version) {
		SelfTM->log_set[SelfTM->ls_max_index].addr_i = addr;
		SelfTM->log_set[SelfTM->ls_max_index].old_value_i = *addr;
		SelfTM->log_set[SelfTM->ls_max_index].old_version = old_version;
		SelfTM->log_set[SelfTM->ls_max_index].stripe_index = STRIPE_INDEX(addr);
		SelfTM->log_set[SelfTM->ls_max_index].type_size = sizeof(int);
		SelfTM->ls_max_index++;
		if (SelfTM->ls_max_index >= MAX_WS) {
			printf("[%ld][%ld] ERROR: WS MAX reached %d\n", SelfTM->place_id, SelfTM->uniq_id, SelfTM->ls_max_index);
		}
	}

	void tm_save_to_log_long(TMThread *SelfTM, volatile long * addr, long old_version) {
		SelfTM->log_set[SelfTM->ls_max_index].addr_l = addr;
		SelfTM->log_set[SelfTM->ls_max_index].old_value_l = *addr;
		SelfTM->log_set[SelfTM->ls_max_index].old_version = old_version;
		SelfTM->log_set[SelfTM->ls_max_index].stripe_index = STRIPE_INDEX(addr);
		SelfTM->log_set[SelfTM->ls_max_index].type_size = sizeof(long);
		SelfTM->ls_max_index++;
		if (SelfTM->ls_max_index >= MAX_WS) {
			printf("[%ld][%ld] ERROR: WS MAX reached %d\n", SelfTM->place_id, SelfTM->uniq_id, SelfTM->ls_max_index);
		}
	}


	int tm_store_process(TMThread *SelfTM, volatile long * addr, long *old_version) {
		TRACE("[%ld][%ld] tm_store_process: start\n", SelfTM->place_id, SelfTM->uniq_id);
		long stripe_index = STRIPE_INDEX(addr);
		long thread_lock_mask = tm_get_thread_lock_mask(SelfTM);
		volatile long lock = locks_table[stripe_index];

		if (((lock & 0x1) == 0) && lock <= SelfTM->rv) {
			TRACE("[%ld][%ld] tm_store_process: stripe %ld not locked. version = %p\n", SelfTM->place_id, SelfTM->uniq_id, stripe_index, (long *)locks_table[stripe_index]);
			if (x10aux::atomic_ops::compareAndSet_64((volatile x10_long *)&(locks_table[stripe_index]), lock, thread_lock_mask) != lock) {
				TRACE_ABORT("[%ld][%ld] tm_store_process: failed locking - abort\n", SelfTM->place_id, SelfTM->uniq_id);
				tm_abort(SelfTM);
			}
			TRACE("[%ld][%ld] tm_store_process: success locking stripe %ld. version = %p\n", SelfTM->place_id, SelfTM->uniq_id, stripe_index, (long *)locks_table[stripe_index]);
			*old_version = lock;
			return 1;
		} else if (lock == thread_lock_mask) {
			TRACE("[%ld][%ld] tm_store_process: stripe %ld locked by me. version = %p\n", SelfTM->place_id, SelfTM->uniq_id, stripe_index, (long *)locks_table[stripe_index]);
			*old_version = lock;
			return 1;
		}
		TRACE("[%ld][%ld] tm_store_process: stripe %ld locked by other [me=%ld, other=%ld]. version = %p\n", SelfTM->place_id, SelfTM->uniq_id, stripe_index, thread_lock_mask, lock, (long *)locks_table[stripe_index]);
		return 0;

	}
	void tm_store_char(TMThread *SelfTM, volatile char * addr, char value) {
		TRACE("[%ld][%ld] tm_store_char: start p=%p val=%d\n", SelfTM->place_id, SelfTM->uniq_id, addr, (int)value);
		long old_version;
		long stripe_index = STRIPE_INDEX(addr);
		long *addr_aligned = ADDR_ALIGN_TO64BIT(addr);
		TRACE("[%ld][%ld] tm_store_char: ptr_aligned=%p\n", SelfTM->place_id, SelfTM->uniq_id, addr_aligned);

		char cur_value = tm_load_char(SelfTM, addr);
		if (cur_value == value) {
			return;
		}

		if (tm_store_process(SelfTM, addr_aligned, &old_version)) {
			TRACE("[%ld][%ld] tm_store_char: assign %p = %d\n", SelfTM->place_id, SelfTM->uniq_id, addr, (int)value);
			tm_save_to_log_char(SelfTM, addr, old_version);
			*addr = value;
			return;
		}
		TRACE_ABORT("[%ld][%ld] tm_store_char: abort\n", SelfTM->place_id, SelfTM->uniq_id);
		tm_abort(SelfTM);
	}

	void tm_store_short(TMThread *SelfTM, volatile short * addr, short value) {
		TRACE("[%ld][%ld] tm_store_short: start p=%p val=%d\n", SelfTM->place_id, SelfTM->uniq_id, addr, (int)value);
		long old_version;
		long stripe_index = STRIPE_INDEX(addr);
		long *addr_aligned = ADDR_ALIGN_TO64BIT(addr);
		TRACE("[%ld][%ld] tm_store_short: ptr_aligned=%p\n", SelfTM->place_id, SelfTM->uniq_id, addr_aligned);

		short cur_value = tm_load_short(SelfTM, addr);
		if (cur_value == value) {
			return;
		}
		if (tm_store_process(SelfTM, addr_aligned, &old_version)) {
			TRACE("[%ld][%ld] tm_store_short: assign %p = %d\n", SelfTM->place_id, SelfTM->uniq_id, addr, (int)value);
			tm_save_to_log_short(SelfTM, addr, old_version);
			*addr = value;
			return;
		}
		TRACE_ABORT("[%ld][%ld] tm_store_short: abort\n", SelfTM->place_id, SelfTM->uniq_id);
		tm_abort(SelfTM);
	}

	void tm_store_int(TMThread *SelfTM, volatile int * addr, int value) {
		TRACE("[%ld][%ld] tm_store_int: start p=%p val=%d\n", SelfTM->place_id, SelfTM->uniq_id, addr, (int)value);
		long old_version;
		long stripe_index = STRIPE_INDEX(addr);
		long *addr_aligned = ADDR_ALIGN_TO64BIT(addr);
		TRACE("[%ld][%ld] tm_store_int: ptr_aligned=%p\n", SelfTM->place_id, SelfTM->uniq_id, addr_aligned);

		int cur_value = tm_load_int(SelfTM, addr);
		if (cur_value == value) {
			return;
		}
		if (tm_store_process(SelfTM, addr_aligned, &old_version)) {
			TRACE("[%ld][%ld] tm_store_int: assign %p = %d\n", SelfTM->place_id, SelfTM->uniq_id, addr, (int)value);
			tm_save_to_log_int(SelfTM, addr, old_version);
			*addr = value;
			return;
		}
		TRACE_ABORT("[%ld][%ld] tm_store_int: abort\n", SelfTM->place_id, SelfTM->uniq_id);
		tm_abort(SelfTM);
	}

	void tm_store_long(TMThread *SelfTM, volatile long * addr, long value) {
		TRACE("[%ld][%ld] tm_store_long: start p=%p val=%ld\n", SelfTM->place_id, SelfTM->uniq_id, addr, (long)value);
		long old_version;
		long stripe_index = STRIPE_INDEX(addr);
		long *addr_aligned = ADDR_ALIGN_TO64BIT(addr);
		TRACE("[%ld][%ld] tm_store_long: ptr_aligned=%p\n", SelfTM->place_id, SelfTM->uniq_id, addr_aligned);

		long cur_value = tm_load_long(SelfTM, addr);
		if (cur_value == value) {
			return;
		}
		if (tm_store_process(SelfTM, addr_aligned, &old_version)) {
			TRACE("[%ld][%ld] tm_store_long: assign %p = %ld\n", SelfTM->place_id, SelfTM->uniq_id, addr, (long)value);
			tm_save_to_log_long(SelfTM, addr, old_version);
			*addr = value;
			return;
		}
		TRACE_ABORT("[%ld][%ld] tm_store_long: abort\n", SelfTM->place_id, SelfTM->uniq_id);
		tm_abort(SelfTM);
	}

	void tm_release_locks(TMThread *SelfTM, long wv) {
		long stripe_index;
		TRACE("[%ld][%ld] tm_release_locks: start wv=%ld\n", SelfTM->place_id, SelfTM->uniq_id, wv);

		for (int index = 0; index < SelfTM->ls_max_index; index++) {
			stripe_index = SelfTM->log_set[index].stripe_index;
			TRACE("[%ld][%ld] tm_release_locks: stripe_index=%ld\n", SelfTM->place_id, SelfTM->uniq_id, stripe_index);
			if (locks_table[stripe_index] == tm_get_thread_lock_mask(SelfTM)) {
				locks_table[stripe_index] = wv;
				TRACE("[%ld][%ld] tm_release_locks: released\n", SelfTM->place_id, SelfTM->uniq_id);
			}
		}
		x10aux::atomic_ops::store_load_barrier();

		TRACE("[%ld][%ld] tm_release_locks: end\n", SelfTM->place_id, SelfTM->uniq_id);
	}
	int tm_revalidate_read_set(TMThread *SelfTM) {
		long stripe_index;
		long thread_lock_mask = tm_get_thread_lock_mask(SelfTM);
		volatile long lock;

		TRACE("[%ld][%ld] tm_revalidate_read_set: start\n", SelfTM->place_id, SelfTM->uniq_id);

		for (int i=0; i < SelfTM->rs_max_index; i++) {
			stripe_index = SelfTM->read_set[i].stripe_index;
			lock = locks_table[stripe_index];
			if (lock & 0x1) {
				if (lock != thread_lock_mask) {
					TRACE("[%ld][%ld] tm_revalidate_read_set: fail. stripe_index=%ld lock=%ld\n", SelfTM->place_id, SelfTM->uniq_id, stripe_index, lock);
					return 0;
				}
			} else if (lock > SelfTM->rv) {
				TRACE("[%ld][%ld] tm_revalidate_read_set: fail. stripe_index=%ld lock=%ld\n", SelfTM->place_id, SelfTM->uniq_id, stripe_index, lock);
				return 0;
			}
		}

		TRACE("[%ld][%ld] tm_revalidate_read_set: success\n", SelfTM->place_id, SelfTM->uniq_id);
		return 1;
	}

	void tm_commit_internal(TMThread *SelfTM, int is_finish) {
		long wv;

		TRACE_COMMIT("[%ld][%ld]   tm_commit_internal: -----------------------------------\n", SelfTM->place_id, SelfTM->uniq_id);
		TRACE_COMMIT("[%ld][%ld]   tm_commit_internal: nesting level %ld\n", SelfTM->place_id, SelfTM->uniq_id, SelfTM->n_nesting_level-1);

		if (SelfTM->n_nesting_level == 0) {
			TRACE_COMMIT("[%ld][%ld]   tm_commit_internal: ERROR!!! end (no action) - nesting level %ld\n", SelfTM->place_id, SelfTM->uniq_id, SelfTM->n_nesting_level);
			tm_fail();
		} else {
			SelfTM->n_nesting_level--;
		}

		if (SelfTM->n_nesting_level > 0) {
			TRACE_COMMIT("[%ld][%ld]   tm_commit_internal: end (no action) - nesting level %ld\n", SelfTM->place_id, SelfTM->uniq_id, SelfTM->n_nesting_level);
			return;
		}

		if (SelfTM->ls_max_index == 0) {
			TRACE_COMMIT("[%ld][%ld]   tm_commit_internal: end - read only\n", SelfTM->place_id, SelfTM->uniq_id);
			if (!is_finish) {
				TRACE_COMMIT("[%ld][%ld]   tm_commit_internal: end - no finish (read-only)\n", SelfTM->place_id, SelfTM->uniq_id);
				SelfTM->num_of_cur_aborts = 0;
				return;
			}
			SelfTM->num_of_success++;
			SelfTM->num_of_cur_aborts = 0;
			return;
		}

		TRACE_COMMIT("[%ld][%ld]   tm_commit_internal: start\n", SelfTM->place_id, SelfTM->uniq_id);
		if (!tm_revalidate_read_set(SelfTM)) {
			TRACE_ABORT("[%ld][%ld]   tm_commit_internal: revalidate read-set failed\n", SelfTM->place_id, SelfTM->uniq_id);
			tm_abort(SelfTM);
		}

		if (!is_finish) {
			TRACE_COMMIT("[%ld][%ld]   tm_commit_internal: end - no finish\n", SelfTM->place_id, SelfTM->uniq_id);
			SelfTM->num_of_cur_aborts = 0;
			return;
		}

		wv = GVGenerate(SelfTM);

		tm_release_locks(SelfTM, wv);

		TRACE_COMMIT("[%ld][%ld]   tm_commit_internal: end success\n", SelfTM->place_id, SelfTM->uniq_id);

		SelfTM->num_of_success++;
		SelfTM->num_of_cur_aborts = 0;
		return;
	}

	void tm_commit(TMThread *SelfTM) {
		TRACE_COMMIT("[%ld][%ld] tm_commit: -----------------------------------\n", SelfTM->place_id, SelfTM->uniq_id);
		tm_commit_internal(SelfTM, 1);
	}

	void tm_commit_closure(TMThread *SelfTM) {

		TRACE_COMMIT("[%ld][%ld] tm_commit_closure: -----------------------------------\n", SelfTM->place_id, SelfTM->uniq_id);
		TRACE_COMMIT("[%ld][%ld] tm_commit_closure: nesting level %ld\n", SelfTM->place_id, SelfTM->uniq_id, SelfTM->n_nesting_level-1);

		tm_commit_internal(SelfTM, 0);
		SelfTM->last_result = 1;
		return;
	}

	void tm_commit_place(TMThread *SelfTM, int is_success) {
		long wv;

		TRACE_COMMIT("[%ld][%ld] tm_commit_place: -----------------------------------\n", SelfTM->place_id, SelfTM->uniq_id);

		if (SelfTM->n_nesting_level > 0) {
			TRACE_COMMIT("[%ld][%ld] tm_commit_place: nesting level is %ld - ignore call\n", SelfTM->place_id, SelfTM->uniq_id, SelfTM->n_nesting_level);
			SelfTM->last_result = 1;
			return;
		}

		if (is_success) {
			if (SelfTM->ls_max_index == 0) {
				SelfTM->num_of_success++;
				TRACE_COMMIT("[%ld][%ld] tm_commit_place: end - read only (nesting_level=%ld) \n", SelfTM->place_id, SelfTM->uniq_id, SelfTM->n_nesting_level);

			} else {
				wv = GVGenerate(SelfTM);
				tm_release_locks(SelfTM, wv);

				SelfTM->num_of_success++;
				SelfTM->num_of_cur_aborts = 0;
				TRACE_COMMIT("[%ld][%ld] tm_commit_place: end success (nesting_level=%ld) \n", SelfTM->place_id, SelfTM->uniq_id, SelfTM->n_nesting_level);
			}
		} else {
			TRACE_COMMIT("[%ld][%ld] tm_commit_place: fail (nesting_level=%ld) \n", SelfTM->place_id, SelfTM->uniq_id, SelfTM->n_nesting_level);
			tm_restore_log(SelfTM);
			SelfTM->num_of_aborts++;
			SelfTM->num_of_cur_aborts++;
		}

		SelfTM->last_result = 1;
		return;
	}

	void tm_verify_struct(char * ptr, unsigned long type_size) {
		if ( ((type_size % 4) != 0) || (((long)ptr & 0x7) != 0) ) {
			TRACE("tm_verify_struct: not aligned to 64bit. ptr=%p type_size=%lu\n", (void *)ptr, type_size);
			tm_fail();
		}
		return;
	}
	void tm_load_struct(TMThread *SelfTM, char * ptr, char * p_val, unsigned long type_size) {
		long *cur_ptr = (long *)ptr;
		long *cur_p_val = (long *)p_val;
		long num_of_longs = type_size / sizeof(long);

		TRACE("[%ld][%ld] tm_load_struct: start. size=%lu\n", SelfTM->place_id, SelfTM->uniq_id, type_size);
		tm_verify_struct(ptr, type_size);

		TRACE("[%ld][%ld] tm_load_struct: verify done\n", SelfTM->place_id, SelfTM->uniq_id);
		for (int i=0; i < num_of_longs; i++) {
			cur_p_val[i] = tm_load_long(SelfTM, &(cur_ptr[i]));
			TRACE("[%ld][%ld] tm_load_struct: loaded long %d with val=%ld\n", SelfTM->place_id, SelfTM->uniq_id, i+1, cur_p_val[i]);
		}

		TRACE("[%ld][%ld] tm_load_struct: end\n", SelfTM->place_id, SelfTM->uniq_id);
	}

	void tm_store_struct(TMThread *SelfTM, char * ptr, char *p_val, unsigned long type_size) {
		long *cur_ptr = (long *)ptr;
		long *cur_p_val = (long *)p_val;
		long num_of_longs = type_size / sizeof(long);

		TRACE("[%ld][%ld] tm_store_struct: start. size=%lu\n", SelfTM->place_id, SelfTM->uniq_id, type_size);
		tm_verify_struct(ptr, type_size);

		TRACE("[%ld][%ld] tm_store_struct: verify done\n", SelfTM->place_id, SelfTM->uniq_id);
		for (int i=0; i < num_of_longs; i++) {
			tm_store_long(SelfTM, &(cur_ptr[i]), cur_p_val[i]);
			TRACE("[%ld][%ld] tm_store_struct: stored long %d with val=%ld\n", SelfTM->place_id, SelfTM->uniq_id, i+1, cur_p_val[i]);
		}

		TRACE("[%ld][%ld] tm_store_struct: end\n", SelfTM->place_id, SelfTM->uniq_id);
	}

	void tm_load_gen(TMThread *SelfTM, char * ptr, char * p_val, unsigned long type_size) {

		if (1 == type_size) {
			TRACE("[%ld][%ld][LOAD_CHAR] tm_load_gen: ptr=%p type_size=%lu\n", SelfTM->place_id, SelfTM->uniq_id, (void *)ptr, type_size);
			*(char *)p_val = tm_load_char(SelfTM, (char *)ptr);
		} else if (2 == type_size) {
			TRACE("[%ld][%ld][LOAD_SHORT] tm_load_gen: ptr=%p type_size=%lu\n", SelfTM->place_id, SelfTM->uniq_id, (void *)ptr, type_size);
			*(short *)p_val = tm_load_short(SelfTM, (short *)ptr);
		} else if (4 == type_size) {
			TRACE("[%ld][%ld][LOAD_INT] tm_load_gen: ptr=%p type_size=%lu\n", SelfTM->place_id, SelfTM->uniq_id, (void *)ptr, type_size);
			*(int *)p_val = tm_load_int(SelfTM, (int *)ptr);
		} else if (8 == type_size) {
			TRACE("[%ld][%ld][LOAD_LONG] tm_load_gen: ptr=%p type_size=%lu\n", SelfTM->place_id, SelfTM->uniq_id, (void *)ptr, type_size);
			*(long *)p_val = tm_load_long(SelfTM, (long *)ptr);
		} else
		{
			TRACE("[%ld][%ld][LOAD_STRUCT] tm_load_gen: ptr=%p type_size=%lu\n", SelfTM->place_id, SelfTM->uniq_id, (void *)ptr, type_size);
			tm_load_struct(SelfTM, (char *)ptr, (char *)p_val, type_size);
		}
	}

	char g_tm_arr[100] = {0,};

	char * tm_store_gen(TMThread *SelfTM, char * ptr, char * p_val, unsigned long type_size) {
		for (unsigned int i=0; i < type_size; i++)
		{
			g_tm_arr[i] = p_val[i];
		}

		if (1 == type_size) {
			TRACE("[%ld][%ld][STORE_CHAR] tm_store_gen: ptr=%p type_size=%lu\n", SelfTM->place_id, SelfTM->uniq_id, (void *)ptr, type_size);
			tm_store_char(SelfTM, (char *)ptr, *(char *)p_val);
		} else if (2 == type_size) {
			TRACE("[%ld][%ld][STORE_SHORT] tm_store_gen: ptr=%p type_size=%lu\n", SelfTM->place_id, SelfTM->uniq_id, (void *)ptr, type_size);
			tm_store_short(SelfTM, (short *)ptr, *(short *)p_val);
		} else if (4 == type_size) {
			TRACE("[%ld][%ld][STORE_INT] tm_store_gen: ptr=%p type_size=%lu\n", SelfTM->place_id, SelfTM->uniq_id, (void *)ptr, type_size);
			tm_store_int(SelfTM, (int *)ptr, *(int *)p_val);
		} else if (8 == type_size) {
			TRACE("[%ld][%ld][STORE_LONG] tm_store_gen: ptr=%p type_size=%lu\n", SelfTM->place_id, SelfTM->uniq_id, (void *)ptr, type_size);
			tm_store_long(SelfTM, (long *)ptr, *(long *)p_val);

		} else
		{
			TRACE("[%ld][%ld][STORE_STRUCT] tm_store_gen: ptr=%p type_size=%lu\n", SelfTM->place_id, SelfTM->uniq_id, (void *)ptr, type_size);
			tm_store_struct(SelfTM, (char *)ptr, (char *)p_val, type_size);
		}

		return (char *)g_tm_arr;
	}

	void tm_reset(TMThread *SelfTM) {
		SelfTM->rs_max_index = 0;
		SelfTM->ls_max_index = 0;
		//SelfTM->on_failure = NULL;
		SelfTM->n_nesting_level = 0;
	}

	void tm_start(TMThread *SelfTM, jmp_buf *jb) {
		TRACE_START("[%ld][%ld] tm_start: -----------------------------------\n", SelfTM->place_id, SelfTM->uniq_id);

		if (SelfTM->n_nesting_level > 0) {
			if (SelfTM->is_aborted == 1) {
				tm_abort(SelfTM);
			}

			SelfTM->on_failure[SelfTM->n_nesting_level] = jb;
			SelfTM->n_nesting_level++;
			TRACE_START("[%ld][%ld] tm_start: [nested] nesting level %ld\n", SelfTM->place_id, SelfTM->uniq_id, SelfTM->n_nesting_level-1);
			return;
		}
		SelfTM->is_aborted = 0;

		tm_reset(SelfTM);

		SelfTM->rv = GVRead();
		SelfTM->on_failure[SelfTM->n_nesting_level] = jb;
		SelfTM->n_nesting_level++;
		TRACE_START("[%ld][%ld] tm_start: [first] nesting level %ld\n", SelfTM->place_id, SelfTM->uniq_id, SelfTM->n_nesting_level-1);
		return;
	}

	void tm_start__tm__(TMThread *SelfTM, jmp_buf *jb) {
		tm_start(SelfTM, jb);
	}

	void tm_commit__tm__(TMThread *SelfTM) {
		tm_commit(SelfTM);
	}

	TMThread *tm_get_self(int place_id, void *p_x10tm_obj, long th_id) {
		TMThread *SelfTM;

		if (g_is_tm_system_init == 0) {
			tm_system_init(place_id);
		}

		//printf("place_id = %d\n", place_id);

		SelfTM = &(thread_selfs[th_id]);
		if (SelfTM->is_init == 0) {
			//printf("[%ld][%ld] tm_get_self: starting init\n", SelfTM->place_id, SelfTM->uniq_id);
			/*if (th_id == 1) {
				sleep(120);
			}*/
			tm_thread_init(SelfTM, place_id, th_id);
		} else {
			//printf("[%ld][%ld] tm_get_self: already initialized\n", SelfTM->place_id, SelfTM->uniq_id);
		}
		return SelfTM;
	}

	long tm_thread_get_result(int tm_thread_id) {
		TMThread *SelfTM;

		SelfTM = &(thread_selfs[tm_thread_id]);

		TRACE_CLOSURE("[%ld][%ld] tm_thread_get_result: last_result = %ld\n", SelfTM->place_id, SelfTM->uniq_id, SelfTM->last_result);

		return SelfTM->last_result;

	}

	long tm_thread_abort(int tm_thread_id) {
		TMThread *SelfTM;

		SelfTM = &(thread_selfs[tm_thread_id]);

		TRACE_CLOSURE("[%ld][%ld] tm_thread_abort: called \n", SelfTM->place_id, SelfTM->uniq_id);

		tm_abort(SelfTM);
	}

}

