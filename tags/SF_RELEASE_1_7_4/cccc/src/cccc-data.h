/*
 * Data definitions and access functions for CCCC analysis toy.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * Copyright (c) 2007 Paul E. McKenney, IBM Corporation.
 */

#define MAX_TASKS	10	/* '0' .. '9' */
#define MAX_STMTS	10	/* '0' .. '9' */
#define MAX_VARS	26	/* 'a' .. 'z' */
#define MAX_VALS	10	/* '0' .. '9' */

#define OP_IS_READ	0x0001
#define OP_DOES_READ	0x0002
#define OP_IS_WRITE	0x0004
#define OP_DOES_WRITE	0x0008
#define OP_LOADLOAD	0x0010
#define OP_LOADSTORE	0x0020
#define OP_STORELOAD	0x0040
#define OP_STORESTORE	0x0080
#define OP_NULLLOAD	0x0100
#define OP_LOADNULL	0x0200
#define OP_NULLSTORE	0x0400
#define OP_STORENULL	0x0800
#define OP_MBMASK	0x0ff0

#define OP_DIVIDE	(OP_LOADLOAD|OP_LOADSTORE|OP_STORELOAD|OP_STORESTORE)
#define OP_SYNC		(OP_NULLLOAD|OP_LOADNULL|OP_NULLSTORE|OP_STORENULL)
#define OP_SYNC_BEFORE	(OP_LOADNULL|OP_STORENULL)
#define OP_SYNC_AFTER	(OP_NULLLOAD|OP_NULLSTORE)

enum optype { no_op, read_op, write_op, atomic_op, end_op, };
#define N_OP_TYPES end_op

int op_properties[N_OP_TYPES] = { 0 };
char op_code[N_OP_TYPES] = { 0 };
char op_decode[256] = { 0 };
int op_mb_decode[256] = { 0 };

struct membar {
	char *name;
	int properties;
};

struct membar mbdecode[] = {
	{ "mb", OP_NULLLOAD | OP_LOADNULL | OP_NULLSTORE | OP_STORENULL },
	{ "rmb", OP_NULLLOAD | OP_LOADNULL },
	{ "wmb", OP_NULLSTORE | OP_STORENULL },
	{ "acq", OP_LOADLOAD | OP_LOADSTORE },
	{ "rel", OP_LOADSTORE | OP_STORESTORE },
	{ "iacq", OP_NULLLOAD | OP_NULLSTORE },
	{ "irel", OP_LOADNULL | OP_STORENULL },
	{ NULL, 0 },
};

typedef int dependency_map_t[MAX_TASKS][MAX_STMTS][MAX_TASKS][MAX_STMTS];

typedef struct {
	int taskid;
	int stmtid;
} statement_t;

/* Track statements for parsing and analysis. */

int task_last_stmt[MAX_TASKS];		/* Last/largest statement number. */
int task_n_stmts[MAX_TASKS];		/* Number of statements. */
int task_stmts[MAX_TASKS][MAX_STMTS];	/* Per-task statement list. */
int task_stmt_map[MAX_TASKS][MAX_STMTS];/* Statement-present map. */
int task_ops[MAX_TASKS][MAX_STMTS];	/* Operations map (read/write...). */
int task_mbs[MAX_TASKS][MAX_STMTS];	/* Memory-barrier map. */
int task_vars[MAX_TASKS][MAX_STMTS];	/* Variable-access map. */
int task_rvals[MAX_TASKS][MAX_STMTS];	/* Value-loaded map. */
int task_wvals[MAX_TASKS][MAX_STMTS];	/* Value-stored map. */
int task_lineno[MAX_TASKS][MAX_STMTS];	/* Source line number. */

/* Dependency maps. */

dependency_map_t dep_map;	/* Explicitly specified dependencies */
dependency_map_t dep_map_lineno;/* Source line number. */
dependency_map_t po_map;	/* Program-order dependencies. */
dependency_map_t read_map;	/* Inferred read dependencies. */
dependency_map_t prop_map;	/* CCCC-propagated dependencies. */
dependency_map_t hb;		/* CCCC per-task happens-before dependencies. */

#include "cccc-traverse.h"

void clear_dependency_map(dependency_map_t dep)
{
	int i;
	int j;
	int k;
	int l;

	for (i = 0; i < MAX_TASKS; i++) {
		for (j = 0; j < MAX_STMTS; j++) {
			for (k = 0; k < MAX_TASKS; k++) {
				for (l = 0; l < MAX_STMTS; l++) {
					dep[i][j][k][l] = 0;
				}
			}
		}
	}
}

/*
 * Like the name says, initialize the above data.
 */
int initialize(void)
{
	int i;
	int j;
	int k;
	int l;

	for (i = 0; i < MAX_TASKS; i++) {
		task_last_stmt[i] = -1;
		task_n_stmts[i] = 0;
		for (j = 0; j < MAX_STMTS; j++) {
			task_stmts[i][j] = -1;
			task_stmt_map[i][j] = -1;
			task_ops[i][j] = no_op;
			task_mbs[i][j] = 0;
			task_vars[i][j] = -1;
			task_rvals[i][j] = -1;
			task_wvals[i][j] = -1;
			task_lineno[i][j] = 0;
		}
	}
	clear_dependency_map(dep_map);
	clear_dependency_map(dep_map_lineno);
	clear_dependency_map(po_map);
	clear_dependency_map(read_map);
	clear_dependency_map(prop_map);
	clear_dependency_map(hb);

	op_properties[no_op] = 0;
	op_properties[read_op] = OP_IS_READ | OP_DOES_READ;
	op_properties[write_op] = OP_IS_WRITE | OP_DOES_WRITE;
	op_properties[atomic_op] = OP_DOES_READ | OP_DOES_WRITE;
	op_code[no_op] = ' ';
	op_code[read_op] = 'r';
	op_code[write_op] = 'w';
	op_code[atomic_op] = 'a';
	op_decode['r'] = read_op;
	op_decode['w'] = write_op;
	op_decode['a'] = atomic_op;
	op_decode['R'] = read_op;
	op_decode['W'] = write_op;
	op_decode['A'] = atomic_op;
	op_mb_decode['R'] = OP_SYNC;
	op_mb_decode['W'] = OP_SYNC;
	op_mb_decode['A'] = OP_SYNC;
}

/*
 * Does the specified statement exist in the specified task?
 */
int statement_exists(int task, int stmt)
{
	return task_stmt_map[task][stmt] != -1;
}

/*
 * Does the specified statement in the specfied task do a read?
 */
int statement_does_read(int task, int stmt)
{
	if (!statement_exists(task, stmt)) {
		abort();
	}
	return (op_properties[task_ops[task][stmt]] & OP_DOES_READ) != 0;
}

/*
 * Does the specified statement in the specfied task do a write?
 */
int statement_does_write(int task, int stmt)
{
	if (!statement_exists(task, stmt)) {
		abort();
	}
	return (op_properties[task_ops[task][stmt]] & OP_DOES_WRITE) != 0;
}

/*
 * Is the specified statement in the specfied task a read?
 */
int statement_is_read(int task, int stmt)
{
	if (!statement_exists(task, stmt)) {
		abort();
	}
	return (op_properties[task_ops[task][stmt]] & OP_IS_READ) != 0;
}

/*
 * Is the specified statement in the specfied task a write?
 */
int statement_is_write(int task, int stmt)
{
	if (!statement_exists(task, stmt)) {
		abort();
	}
	return (op_properties[task_ops[task][stmt]] & OP_IS_WRITE) != 0;
}

/*
 * Is the specified statement depended on by some other statement?
 * Return the number of other such statements.
 */
int statement_is_depended_on(dependency_map_t dep, int task, int stmt)
{
	int tt;
	int ts;
	int cnt = 0;

	for_each_statement(tt, ts) {
		if (dep[task][stmt][tt][ts]) {
			cnt++;
		}
	}
	return cnt;
}

/*
 * Does the specified statement depend on some other statement?
 * Return the number of such statements.
 */
int statement_depends_on(dependency_map_t dep, int task, int stmt)
{
	int ft;
	int fs;
	int cnt = 0;

	for_each_statement(ft, fs) {
		if (dep[ft][fs][task][stmt]) {
			cnt++;
		}
	}
	return cnt;
}
