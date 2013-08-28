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

//#define IS_DEBUG
//#define IS_DEBUG_ABORT

#ifdef IS_DEBUG
#define TRACE(format, ...) printf(format, __VA_ARGS__)
#define TRACE_ABORT(format, ...) printf(format, __VA_ARGS__)
#else

#ifdef IS_DEBUG_ABORT
#define TRACE_ABORT(format, ...) printf(format, __VA_ARGS__)
#else
#define TRACE_ABORT(format, ...)
#endif

#define TRACE(format, ...)
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
	void tm_system_init() {
		(void)pthread_mutex_init(&g_init_lock, NULL);

		g_clock = 0;

		memset(thread_selfs, 0, sizeof(TMThread) * MAX_THREADS);

		for (int i=0; i < LOCKS_NUM; i++) {
			locks_table[i] = 0;
		}

		g_num_of_success = 0;
		g_num_of_aborts = 0;

		g_tm_process_id = (int)getpid();

		g_is_tm_system_init = 1;

		printf("tm_system_init: initialized [place = %d]\n", g_tm_process_id);
	}

	void tm_system_finish() {
		printf("---------------------------------------------\n");
		printf("num_of_success = %ld\n", g_num_of_success);
		printf("num_of_aborts = %ld\n", g_num_of_aborts);
		printf("tm_system_finish: done [place = %d]\n", g_tm_process_id);
		g_is_tm_system_init = 0;
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

	void tm_thread_init(TMThread *SelfTM, long th_id) {
		if (SelfTM->is_init) {
			return;
		}

		SelfTM->uniq_id = th_id;
		SelfTM->th_id_system = (long)pthread_self();
		SelfTM->rv = 0;
		SelfTM->on_failure = NULL;
		SelfTM->num_of_aborts = 0;
		SelfTM->num_of_success = 0;

		SelfTM->is_init = 1;
		printf("[%d] tm_thread_init: initialized thread uniq_id=%ld system_id=%ld\n", g_tm_process_id, SelfTM->uniq_id, SelfTM->th_id_system);
		return;
	}

	void tm_thread_finish(TMThread *SelfTM) {
		if (!SelfTM->is_init) {
			printf("[%d][%ld] tm_thread_finish: SelfTM is not initialized \n", g_tm_process_id, SelfTM->uniq_id);
			tm_fail();
		}

		atomic_inc(&g_num_of_success, SelfTM->num_of_success);
		atomic_inc(&g_num_of_aborts, SelfTM->num_of_aborts);

		printf("[%d] tm_thread_finish: finished thread %ld\n", g_tm_process_id, SelfTM->uniq_id);
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
		TRACE("[%d][%ld] GVGenerate: start\n", g_tm_process_id, SelfTM->uniq_id);
		while (1) {
			cur_clock = g_clock;
			new_clock = cur_clock + 1;
			if (x10aux::atomic_ops::compareAndSet_64((volatile x10_long *)&g_clock, cur_clock, new_clock) == cur_clock) {
				TRACE("[%d][%ld] GVGenerate: new_clock = %ld\n", g_tm_process_id, SelfTM->uniq_id, new_clock);
				return new_clock << 1;
			}
			TRACE("[%d][%ld] GVGenerate: failed advancing the clock g_clock = %ld\n", g_tm_process_id, SelfTM->uniq_id, g_clock);

		}
		TRACE("[%d][%ld] GVGenerate: abort...\n", g_tm_process_id, SelfTM->uniq_id);
		tm_fail();
	}

	int tm_abort(TMThread *SelfTM) {
		long stripe_index;
		long old_version;
		int type_size;
		TRACE_ABORT("[%d][%ld][ABORT] tm_abort: start\n", g_tm_process_id, SelfTM->uniq_id);

		TRACE_ABORT("[%d][%ld] tm_abort: restore logged locations\n", g_tm_process_id, SelfTM->uniq_id);
		for (int index = SelfTM->ls_max_index-1; index >= 0; index--) {
			old_version = SelfTM->log_set[index].old_version;
			type_size = SelfTM->log_set[index].type_size;
			stripe_index = SelfTM->log_set[index].stripe_index;


			switch (type_size) {
			case sizeof(char):
				TRACE_ABORT("[%d][%ld] tm_abort: restore char addr=%p old_val=%d stripe_index=%ld\n",
						g_tm_process_id,
						SelfTM->uniq_id,
						SelfTM->log_set[index].addr_c,
						SelfTM->log_set[index].old_value_c,
						stripe_index);
				*(SelfTM->log_set[index].addr_c) = SelfTM->log_set[index].old_value_c;
				break;
			case sizeof(short):
				TRACE_ABORT("[%d][%ld] tm_abort: restore short addr=%p old_val=%d stripe_index=%ld\n",
						g_tm_process_id,
						SelfTM->uniq_id,
						SelfTM->log_set[index].addr_s,
						SelfTM->log_set[index].old_value_s,
						stripe_index);
				*(SelfTM->log_set[index].addr_s) = SelfTM->log_set[index].old_value_s;
				break;
			case sizeof(int):
				TRACE_ABORT("[%d][%ld] tm_abort: restore int addr=%p old_val=%d stripe_index=%ld\n",
						g_tm_process_id,
						SelfTM->uniq_id,
						SelfTM->log_set[index].addr_i,
						SelfTM->log_set[index].old_value_i,
						stripe_index);
				*(SelfTM->log_set[index].addr_i) = SelfTM->log_set[index].old_value_i;
				break;
			case sizeof(long):
				TRACE_ABORT("[%d][%ld] tm_abort: restore long addr=%p old_val=%ld stripe_index=%ld\n",
						g_tm_process_id,
						SelfTM->uniq_id,
						SelfTM->log_set[index].addr_l,
						SelfTM->log_set[index].old_value_l,
						stripe_index);
				*(SelfTM->log_set[index].addr_l) = SelfTM->log_set[index].old_value_l;
				break;
			}
			TRACE_ABORT("[%d][%ld] tm_abort: restored\n", g_tm_process_id, SelfTM->uniq_id);
			locks_table[stripe_index] = old_version;
			TRACE_ABORT("[%d][%ld] tm_abort: locks_table[%ld] = %ld\n", g_tm_process_id, SelfTM->uniq_id, stripe_index, old_version);
		}

		SelfTM->num_of_aborts++;

		TRACE_ABORT("[%d][%ld] tm_abort: before if jump\n", g_tm_process_id, SelfTM->uniq_id);
		if (SelfTM->on_failure != NULL) {
			TRACE_ABORT("[%d][%ld] tm_abort: making jump\n", g_tm_process_id, SelfTM->uniq_id);
			longjmp (*(SelfTM->on_failure), 1) ;
		}

		TRACE_ABORT("[%d][%ld] tm_abort: jump failed...\n", g_tm_process_id, SelfTM->uniq_id);
	}

	long tm_get_thread_lock_mask(TMThread *SelfTM) {
		return (SelfTM->uniq_id << 1) | 1;
	}

	void tm_track_read(TMThread *SelfTM, long stripe_index) {
		SelfTM->read_set[SelfTM->rs_max_index].stripe_index = stripe_index;
		SelfTM->rs_max_index++;
		if (SelfTM->rs_max_index >= MAX_RS) {
			printf("[%d][%ld] ERROR: RS MAX reached %d\n", g_tm_process_id, SelfTM->uniq_id, SelfTM->rs_max_index);
		}
	}

	int tm_load_process(TMThread *SelfTM,
						long stripe_index,
						volatile long lock1,
						volatile long lock2) {
		TRACE("[%d][%ld] tm_load_process: start\n", g_tm_process_id, SelfTM->uniq_id);

		if (((lock1 & 0x1) == 0) && (lock1 == lock2) && (lock1 <= SelfTM->rv)) {
			tm_track_read(SelfTM, stripe_index);
			TRACE("[%d][%ld] tm_load_process: track read-set\n", g_tm_process_id, SelfTM->uniq_id);
			TRACE("[%d][%ld] tm_load_process: end\n", g_tm_process_id, SelfTM->uniq_id);
			return 1;
		} else {
			if (lock1 == tm_get_thread_lock_mask(SelfTM)) {
				TRACE("[%d][%ld] tm_load_process: locked by me\n", g_tm_process_id, SelfTM->uniq_id);
				TRACE("[%d][%ld] tm_load_process: return 1\n", g_tm_process_id, SelfTM->uniq_id);
				return 1;
			}
		}

		TRACE("[%d][%ld] tm_load_process: failed. lock1=%ld lock2=%ld stripe_index=%ld\n", g_tm_process_id, SelfTM->uniq_id, lock1, lock2, stripe_index);
		return 0;
	}

	char tm_load_char(TMThread *SelfTM, volatile char * addr) {
		TRACE("[%d][%ld] tm_load_char: start. ptr=%p\n", g_tm_process_id, SelfTM->uniq_id, addr);
		long stripe_index = STRIPE_INDEX(addr);
		volatile long lock1 = locks_table[stripe_index];
		volatile char value = *addr;
		volatile long lock2 = locks_table[stripe_index];

		TRACE("[%d][%ld] tm_load_char: stripe_index = %ld\n", g_tm_process_id, SelfTM->uniq_id, stripe_index);
		if (tm_load_process(SelfTM, stripe_index, lock1, lock2)) {
			TRACE("[%d][%ld] tm_load_char: success. ptr=%p val=%d\n", g_tm_process_id, SelfTM->uniq_id, addr, (int)value);
			return value;
		}

		TRACE_ABORT("[%d][%ld] tm_load_int: abort ptr=%p val=%d stripe_index=%ld\n", g_tm_process_id, SelfTM->uniq_id, addr, value, stripe_index);
		tm_abort(SelfTM);
	}

	short tm_load_short(TMThread *SelfTM, volatile short * addr) {
		TRACE("[%d][%ld] tm_load_short: start. ptr=%p\n", g_tm_process_id, SelfTM->uniq_id, addr);
		long stripe_index = STRIPE_INDEX(addr);
		volatile long lock1 = locks_table[stripe_index];
		volatile short value = *addr;
		volatile long lock2 = locks_table[stripe_index];

		TRACE("[%d][%ld] tm_load_short: stripe_index = %ld\n", g_tm_process_id, SelfTM->uniq_id, stripe_index);
		if (tm_load_process(SelfTM, stripe_index, lock1, lock2)) {
			TRACE("[%d][%ld] tm_load_short: success. ptr=%p val=%d\n", g_tm_process_id, SelfTM->uniq_id, addr, (int)value);
			return value;
		}

		TRACE_ABORT("[%d][%ld] tm_load_int: abort ptr=%p val=%d stripe_index=%ld\n", g_tm_process_id, SelfTM->uniq_id, addr, value, stripe_index);
		tm_abort(SelfTM);
	}

	int tm_load_int(TMThread *SelfTM, volatile int * addr) {
		TRACE("[%d][%ld] tm_load_int: start. ptr=%p\n", g_tm_process_id, SelfTM->uniq_id, addr);
		long stripe_index = STRIPE_INDEX(addr);
		volatile long lock1 = locks_table[stripe_index];
		volatile int value = *addr;
		volatile long lock2 = locks_table[stripe_index];

		TRACE("[%d][%ld] tm_load_int: stripe_index = %ld\n", g_tm_process_id, SelfTM->uniq_id, stripe_index);
		if (tm_load_process(SelfTM, stripe_index, lock1, lock2)) {
			TRACE("[%d][%ld] tm_load_int: success. ptr=%p val=%d\n", g_tm_process_id, SelfTM->uniq_id, addr, (int)value);
			return value;
		}

		TRACE_ABORT("[%d][%ld] tm_load_int: abort ptr=%p val=%d stripe_index=%ld\n", g_tm_process_id, SelfTM->uniq_id, addr, value, stripe_index);
		tm_abort(SelfTM);
	}

	long tm_load_long(TMThread *SelfTM, volatile long * addr) {
		TRACE("[%d][%ld] tm_load_long: start. ptr=%p\n", g_tm_process_id, SelfTM->uniq_id, addr);
		long stripe_index = STRIPE_INDEX(addr);
		volatile long lock1 = locks_table[stripe_index];
		volatile long value = *addr;
		volatile long lock2 = locks_table[stripe_index];

		TRACE("[%d][%ld] tm_load_long: stripe_index = %ld\n", g_tm_process_id, SelfTM->uniq_id, stripe_index);
		if (tm_load_process(SelfTM, stripe_index, lock1, lock2)) {
			TRACE("[%d][%ld] tm_load_long: success. ptr=%p val=%ld\n", g_tm_process_id, SelfTM->uniq_id, addr, value);
			return value;
		}

		TRACE_ABORT("[%d][%ld] tm_load_int: abort ptr=%p val=%ld stripe_index=%ld\n", g_tm_process_id, SelfTM->uniq_id, addr, value, stripe_index);
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
			printf("[%d][%ld] ERROR: WS MAX reached %d\n", g_tm_process_id, SelfTM->uniq_id, SelfTM->ls_max_index);
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
			printf("[%d][%ld] ERROR: WS MAX reached %d\n", g_tm_process_id, SelfTM->uniq_id, SelfTM->ls_max_index);
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
			printf("[%d][%ld] ERROR: WS MAX reached %d\n", g_tm_process_id, SelfTM->uniq_id, SelfTM->ls_max_index);
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
			printf("[%d][%ld] ERROR: WS MAX reached %d\n", g_tm_process_id, SelfTM->uniq_id, SelfTM->ls_max_index);
		}
	}


	int tm_store_process(TMThread *SelfTM, volatile long * addr, long *old_version) {
		TRACE("[%d][%ld] tm_store_process: start\n", g_tm_process_id, SelfTM->uniq_id);
		long stripe_index = STRIPE_INDEX(addr);
		long thread_lock_mask = tm_get_thread_lock_mask(SelfTM);
		volatile long lock = locks_table[stripe_index];

		if (((lock & 0x1) == 0) && lock <= SelfTM->rv) {
			TRACE("[%d][%ld] tm_store_process: stripe %ld not locked. version = %p\n", g_tm_process_id, SelfTM->uniq_id, stripe_index, (long *)locks_table[stripe_index]);
			if (x10aux::atomic_ops::compareAndSet_64((volatile x10_long *)&(locks_table[stripe_index]), lock, thread_lock_mask) != lock) {
				TRACE_ABORT("[%d][%ld] tm_store_process: failed locking - abort\n", g_tm_process_id, SelfTM->uniq_id);
				tm_abort(SelfTM);
			}
			TRACE("[%d][%ld] tm_store_process: success locking stripe %ld. version = %p\n", g_tm_process_id, SelfTM->uniq_id, stripe_index, (long *)locks_table[stripe_index]);
			*old_version = lock;
			return 1;
		} else if (lock == thread_lock_mask) {
			TRACE("[%d][%ld] tm_store_process: stripe %ld locked by me. version = %p\n", g_tm_process_id, SelfTM->uniq_id, stripe_index, (long *)locks_table[stripe_index]);
			*old_version = lock;
			return 1;
		}
		TRACE("[%d][%ld] tm_store_process: stripe %ld locked by other [me=%ld, other=%ld]. version = %p\n", g_tm_process_id, SelfTM->uniq_id, stripe_index, thread_lock_mask, lock, (long *)locks_table[stripe_index]);
		return 0;

	}
	void tm_store_char(TMThread *SelfTM, volatile char * addr, char value) {
		TRACE("[%d][%ld] tm_store_char: start p=%p val=%d\n", g_tm_process_id, SelfTM->uniq_id, addr, (int)value);
		long old_version;
		long stripe_index = STRIPE_INDEX(addr);
		long *addr_aligned = ADDR_ALIGN_TO64BIT(addr);
		TRACE("[%d][%ld] tm_store_char: ptr_aligned=%p\n", g_tm_process_id, SelfTM->uniq_id, addr_aligned);

		char cur_value = tm_load_char(SelfTM, addr);
		if (cur_value == value) {
			return;
		}

		if (tm_store_process(SelfTM, addr_aligned, &old_version)) {
			TRACE("[%d][%ld] tm_store_char: assign %p = %d\n", g_tm_process_id, SelfTM->uniq_id, addr, (int)value);
			tm_save_to_log_char(SelfTM, addr, old_version);
			*addr = value;
			return;
		}
		TRACE_ABORT("[%d][%ld] tm_store_char: abort\n", g_tm_process_id, SelfTM->uniq_id);
		tm_abort(SelfTM);
	}

	void tm_store_short(TMThread *SelfTM, volatile short * addr, short value) {
		TRACE("[%d][%ld] tm_store_short: start p=%p val=%d\n", g_tm_process_id, SelfTM->uniq_id, addr, (int)value);
		long old_version;
		long stripe_index = STRIPE_INDEX(addr);
		long *addr_aligned = ADDR_ALIGN_TO64BIT(addr);
		TRACE("[%d][%ld] tm_store_short: ptr_aligned=%p\n", g_tm_process_id, SelfTM->uniq_id, addr_aligned);

		short cur_value = tm_load_short(SelfTM, addr);
		if (cur_value == value) {
			return;
		}
		if (tm_store_process(SelfTM, addr_aligned, &old_version)) {
			TRACE("[%d][%ld] tm_store_short: assign %p = %d\n", g_tm_process_id, SelfTM->uniq_id, addr, (int)value);
			tm_save_to_log_short(SelfTM, addr, old_version);
			*addr = value;
			return;
		}
		TRACE_ABORT("[%d][%ld] tm_store_short: abort\n", g_tm_process_id, SelfTM->uniq_id);
		tm_abort(SelfTM);
	}

	void tm_store_int(TMThread *SelfTM, volatile int * addr, int value) {
		TRACE("[%d][%ld] tm_store_int: start p=%p val=%d\n", g_tm_process_id, SelfTM->uniq_id, addr, (int)value);
		long old_version;
		long stripe_index = STRIPE_INDEX(addr);
		long *addr_aligned = ADDR_ALIGN_TO64BIT(addr);
		TRACE("[%d][%ld] tm_store_int: ptr_aligned=%p\n", g_tm_process_id, SelfTM->uniq_id, addr_aligned);

		int cur_value = tm_load_int(SelfTM, addr);
		if (cur_value == value) {
			return;
		}
		if (tm_store_process(SelfTM, addr_aligned, &old_version)) {
			TRACE("[%d][%ld] tm_store_int: assign %p = %d\n", g_tm_process_id, SelfTM->uniq_id, addr, (int)value);
			tm_save_to_log_int(SelfTM, addr, old_version);
			*addr = value;
			return;
		}
		TRACE_ABORT("[%d][%ld] tm_store_int: abort\n", g_tm_process_id, SelfTM->uniq_id);
		tm_abort(SelfTM);
	}

	void tm_store_long(TMThread *SelfTM, volatile long * addr, long value) {
		TRACE("[%d][%ld] tm_store_long: start p=%p val=%ld\n", g_tm_process_id, SelfTM->uniq_id, addr, (long)value);
		long old_version;
		long stripe_index = STRIPE_INDEX(addr);
		long *addr_aligned = ADDR_ALIGN_TO64BIT(addr);
		TRACE("[%d][%ld] tm_store_long: ptr_aligned=%p\n", g_tm_process_id, SelfTM->uniq_id, addr_aligned);

		long cur_value = tm_load_long(SelfTM, addr);
		if (cur_value == value) {
			return;
		}
		if (tm_store_process(SelfTM, addr_aligned, &old_version)) {
			TRACE("[%d][%ld] tm_store_long: assign %p = %ld\n", g_tm_process_id, SelfTM->uniq_id, addr, (long)value);
			tm_save_to_log_long(SelfTM, addr, old_version);
			*addr = value;
			return;
		}
		TRACE_ABORT("[%d][%ld] tm_store_long: abort\n", g_tm_process_id, SelfTM->uniq_id);
		tm_abort(SelfTM);
	}

	void tm_release_locks(TMThread *SelfTM, long wv) {
		long stripe_index;
		TRACE("[%d][%ld] tm_release_locks: start wv=%ld\n", g_tm_process_id, SelfTM->uniq_id, wv);

		for (int index = 0; index < SelfTM->ls_max_index; index++) {
			stripe_index = SelfTM->log_set[index].stripe_index;
			TRACE("[%d][%ld] tm_release_locks: stripe_index=%ld\n", g_tm_process_id, SelfTM->uniq_id, stripe_index);
			if (locks_table[stripe_index] == tm_get_thread_lock_mask(SelfTM)) {
				locks_table[stripe_index] = wv;
				TRACE("[%d][%ld] tm_release_locks: released\n", g_tm_process_id, SelfTM->uniq_id);
			}
		}
		x10aux::atomic_ops::store_load_barrier();

		TRACE("[%d][%ld] tm_release_locks: end\n", g_tm_process_id, SelfTM->uniq_id);
	}
	int tm_revalidate_read_set(TMThread *SelfTM) {
		long stripe_index;
		long thread_lock_mask = tm_get_thread_lock_mask(SelfTM);
		volatile long lock;

		TRACE("[%d][%ld] tm_revalidate_read_set: start\n", g_tm_process_id, SelfTM->uniq_id);

		for (int i=0; i < SelfTM->rs_max_index; i++) {
			stripe_index = SelfTM->read_set[i].stripe_index;
			lock = locks_table[stripe_index];
			if (lock & 0x1) {
				if (lock != thread_lock_mask) {
					TRACE("[%d][%ld] tm_revalidate_read_set: fail. stripe_index=%ld lock=%ld\n", g_tm_process_id, SelfTM->uniq_id, stripe_index, lock);
					return 0;
				}
			} else if (lock > SelfTM->rv) {
				TRACE("[%d][%ld] tm_revalidate_read_set: fail. stripe_index=%ld lock=%ld\n", g_tm_process_id, SelfTM->uniq_id, stripe_index, lock);
				return 0;
			}
		}

		TRACE("[%d][%ld] tm_revalidate_read_set: success\n", g_tm_process_id, SelfTM->uniq_id);
		return 1;
	}

	void tm_commit(TMThread *SelfTM) {
		long wv;

		TRACE("[%d][%ld]TM_COMMIT: -----------------------------------\n", g_tm_process_id, SelfTM->uniq_id);
		TRACE("[%d][%ld]TM_COMMIT: -----------------------------------\n", g_tm_process_id, SelfTM->uniq_id);
		TRACE("[%d][%ld]TM_COMMIT: -----------------------------------\n", g_tm_process_id, SelfTM->uniq_id);
		if (SelfTM->ls_max_index == 0) {
			TRACE("[%d][%ld] tm_commit: end - read only\n", g_tm_process_id, SelfTM->uniq_id);
			SelfTM->num_of_success++;
			return;
		}
		TRACE("[%d][%ld] tm_commit: start\n", g_tm_process_id, SelfTM->uniq_id);
		if (!tm_revalidate_read_set(SelfTM)) {
			TRACE_ABORT("[%d][%ld] tm_commit: revalidate read-set failed\n", g_tm_process_id, SelfTM->uniq_id);
			tm_abort(SelfTM);
		}

		wv = GVGenerate(SelfTM);

		tm_release_locks(SelfTM, wv);

		TRACE("[%d][%ld] tm_commit: end\n", g_tm_process_id, SelfTM->uniq_id);

		SelfTM->num_of_success++;
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

		TRACE("[%d][%ld] tm_load_struct: start. size=%lu\n", g_tm_process_id, SelfTM->uniq_id, type_size);
		tm_verify_struct(ptr, type_size);

		TRACE("[%d][%ld] tm_load_struct: verify done\n", g_tm_process_id, SelfTM->uniq_id);
		for (int i=0; i < num_of_longs; i++) {
			cur_p_val[i] = tm_load_long(SelfTM, &(cur_ptr[i]));
			TRACE("[%d][%ld] tm_load_struct: loaded long %d with val=%ld\n", g_tm_process_id, SelfTM->uniq_id, i+1, cur_p_val[i]);
		}

		TRACE("[%d][%ld] tm_load_struct: end\n", g_tm_process_id, SelfTM->uniq_id);
	}

	void tm_store_struct(TMThread *SelfTM, char * ptr, char *p_val, unsigned long type_size) {
		long *cur_ptr = (long *)ptr;
		long *cur_p_val = (long *)p_val;
		long num_of_longs = type_size / sizeof(long);

		TRACE("[%d][%ld] tm_store_struct: start. size=%lu\n", g_tm_process_id, SelfTM->uniq_id, type_size);
		tm_verify_struct(ptr, type_size);

		TRACE("[%d][%ld] tm_store_struct: verify done\n", g_tm_process_id, SelfTM->uniq_id);
		for (int i=0; i < num_of_longs; i++) {
			tm_store_long(SelfTM, &(cur_ptr[i]), cur_p_val[i]);
			TRACE("[%d][%ld] tm_store_struct: stored long %d with val=%ld\n", g_tm_process_id, SelfTM->uniq_id, i+1, cur_p_val[i]);
		}

		TRACE("[%d][%ld] tm_store_struct: end\n", g_tm_process_id, SelfTM->uniq_id);
	}

	void tm_load_gen(TMThread *SelfTM, char * ptr, char * p_val, unsigned long type_size) {

		if (1 == type_size) {
			TRACE("[%d][%ld][LOAD_CHAR] tm_load_gen: ptr=%p type_size=%lu\n", g_tm_process_id, SelfTM->uniq_id, (void *)ptr, type_size);
			*(char *)p_val = tm_load_char(SelfTM, (char *)ptr);
		} else if (2 == type_size) {
			TRACE("[%d][%ld][LOAD_SHORT] tm_load_gen: ptr=%p type_size=%lu\n", g_tm_process_id, SelfTM->uniq_id, (void *)ptr, type_size);
			*(short *)p_val = tm_load_short(SelfTM, (short *)ptr);
		} else if (4 == type_size) {
			TRACE("[%d][%ld][LOAD_INT] tm_load_gen: ptr=%p type_size=%lu\n", g_tm_process_id, SelfTM->uniq_id, (void *)ptr, type_size);
			*(int *)p_val = tm_load_int(SelfTM, (int *)ptr);
		} else if (8 == type_size) {
			TRACE("[%d][%ld][LOAD_LONG] tm_load_gen: ptr=%p type_size=%lu\n", g_tm_process_id, SelfTM->uniq_id, (void *)ptr, type_size);
			*(long *)p_val = tm_load_long(SelfTM, (long *)ptr);
		} else
		{
			TRACE("[%d][%ld][LOAD_STRUCT] tm_load_gen: ptr=%p type_size=%lu\n", g_tm_process_id, SelfTM->uniq_id, (void *)ptr, type_size);
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
			TRACE("[%d][%ld][STORE_CHAR] tm_store_gen: ptr=%p type_size=%lu\n", g_tm_process_id, SelfTM->uniq_id, (void *)ptr, type_size);
			tm_store_char(SelfTM, (char *)ptr, *(char *)p_val);
		} else if (2 == type_size) {
			TRACE("[%d][%ld][STORE_SHORT] tm_store_gen: ptr=%p type_size=%lu\n", g_tm_process_id, SelfTM->uniq_id, (void *)ptr, type_size);
			tm_store_short(SelfTM, (short *)ptr, *(short *)p_val);
		} else if (4 == type_size) {
			TRACE("[%d][%ld][STORE_INT] tm_store_gen: ptr=%p type_size=%lu\n", g_tm_process_id, SelfTM->uniq_id, (void *)ptr, type_size);
			tm_store_int(SelfTM, (int *)ptr, *(int *)p_val);
		} else if (8 == type_size) {
			TRACE("[%d][%ld][STORE_LONG] tm_store_gen: ptr=%p type_size=%lu\n", g_tm_process_id, SelfTM->uniq_id, (void *)ptr, type_size);
			tm_store_long(SelfTM, (long *)ptr, *(long *)p_val);

		} else
		{
			TRACE("[%d][%ld][STORE_STRUCT] tm_store_gen: ptr=%p type_size=%lu\n", g_tm_process_id, SelfTM->uniq_id, (void *)ptr, type_size);
			tm_store_struct(SelfTM, (char *)ptr, (char *)p_val, type_size);
		}

		return (char *)g_tm_arr;
	}

	void tm_reset(TMThread *SelfTM) {
		SelfTM->rs_max_index = 0;
		SelfTM->ls_max_index = 0;
		SelfTM->on_failure = NULL;
	}
	void tm_start(TMThread *SelfTM, jmp_buf *jb) {
		TRACE("[%d][%ld]TM_START: -----------------------------------\n", g_tm_process_id, SelfTM->uniq_id);
		TRACE("[%d][%ld]TM_START: -----------------------------------\n", g_tm_process_id, SelfTM->uniq_id);
		TRACE("[%d][%ld]TM_START: -----------------------------------\n", g_tm_process_id, SelfTM->uniq_id);
		tm_reset(SelfTM);
		SelfTM->rv = GVRead();
		SelfTM->on_failure = jb;
		return;
	}

	void tm_start__tm__(TMThread *SelfTM, jmp_buf *jb) {
		tm_start(SelfTM, jb);
	}

	void tm_commit__tm__(TMThread *SelfTM) {
		tm_commit(SelfTM);
	}

	TMThread *tm_get_self(int place_id, long th_id) {
		TMThread *SelfTM;

		if (g_is_tm_system_init == 0) {
			tm_system_init();
		}

		printf("place_id = %d\n", place_id);

		SelfTM = &(thread_selfs[th_id]);
		if (SelfTM->is_init == 0) {
			//printf("[%d][%ld] tm_get_self: starting init\n", g_tm_process_id, SelfTM->uniq_id);
			tm_thread_init(SelfTM, th_id);
		} else {
			//printf("[%d][%ld] tm_get_self: already initialized\n", g_tm_process_id, SelfTM->uniq_id);
		}
		return SelfTM;
	}

}

