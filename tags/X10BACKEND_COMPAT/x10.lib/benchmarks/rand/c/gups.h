/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: gups.h,v 1.2 2007-05-03 11:40:34 srkodali Exp $
 * This file is part of X10 Runtime System.
 *
 * Original version of this code using LAPI C/C++ is due to
 * Hanhong Xue <hanhong@us.ibm.com>
 */

/** GUPS X10LIB header file **/
#ifndef __GUPS_X10LIB_H
#define __GUPS_X10LIB_H

#include <x10/x10lib.h>
#include <stdio.h>
#include <stdlib.h>

#define X10RC(statement) \
do { \
	int rc = statement; \
	if (rc != X10_OK) { \
		printf(#statement " rc = %d, line %d\n", rc, __LINE__); \
		exit(1); \
	} \
} while (0)

/* flags for testing falsification */
#undef FALSE
#undef TRUE
typedef enum {
	FALSE = 0, TRUE
} bool_t;

/* default values */
#define TABLE_SIZE 26
#define OUTPUT_FILE "gups.out"
#define TIME_BOUND 60
#define SAMPLE_FACTOR 100

/* macro for table updation */
#define UPDATE(offset, ran) table[offset]++

/* for random number generator */
#define POLY 7ULL
#define PERIOD 1317624576693539401ULL

/* bound for aggregation */
#define AGG_LIMIT 1024

/* upper bound for number of tasks */
#define MAX_TASKS 64

/* data struct for buckets */
typedef struct _element {
	int dest;
	int count;
	struct _element *before, *after;
	uint64_t update[AGG_LIMIT];
} element;

/* dbase record for results */
typedef struct _results_rec {
	int num_tasks;
	int log_table_size;
	uint64_t table_size;
	int log_num_updates;
	uint64_t num_updates;
	uint64_t done_updates;
	double gups;
	double duration; /* seconds */
	long nsends;
	long search_count;
	int do_aggregate;
	int do_interrupt;
	int do_timebound;
	int do_embarassing;
} results_rec;

/* file: comm.c */
extern int num_tasks;
extern int my_id;

void x10lib_init(void);
void x10lib_term(void);

/* file: gups.c */
extern lapi_handle_t hndl;
extern int log_num_updates;
extern uint64_t num_updates;
extern int log_table_size;
extern uint64_t table_size;
extern uint64_t *table;
extern bool_t do_aggregate;
extern bool_t do_embarassing;
extern bool_t do_interrupt;
extern bool_t do_timebound;
extern long timebound;
extern uint64_t timebound_updates;
extern void *addr_tbl[];
extern uint64_t update_buffer[];
extern char *prog_name;
extern results_rec rrec;

void clean_up(void);
void usage(void);

/* file: utility.c */
double cur_time(void);
uint64_t start_ran(int64_t n);

/* file: buckets.c */
extern element bucket[];
extern element *head[];
extern long nsends;
extern long nelements;
extern long max_bucket_size;
extern long search_count;

void init_buckets(void);
void push(element *e);
void deque(element *e);
void add_to_bucket(int dest, uint64_t ran);
void flush_buckets(void);

/* file: update.c */
extern uint64_t update_buffer[];

void batch_update(uint64_t *value, ulong msg_len);
void complete_update(lapi_handle_t *hndl, void *completion_param);
void *receive_update(lapi_handle_t *hndl, void *uhdr,
		uint *uhdr_len, ulong *msg_len, compl_hndlr_t **ucomp,
		void **uinfo);
void send_update(int dest, uint64_t value);

#endif /* __GUPS_X10LIB_H */
