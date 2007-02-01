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

#define OP_IS_READ	0x01
#define OP_DOES_READ	0x02
#define OP_IS_WRITE	0x04
#define OP_DOES_WRITE	0x08
enum optype { no_op, read_op, write_op, atomic_op, end_op, };
#define N_OP_TYPES end_op

int op_properties[N_OP_TYPES] = { 0 };
char op_code[N_OP_TYPES] = { 0 };
char op_decode[256] = { 0 };

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
int task_vars[MAX_TASKS][MAX_STMTS];	/* Variable-access map. */
int task_rvals[MAX_TASKS][MAX_STMTS];	/* Value-loaded map. */
int task_wvals[MAX_TASKS][MAX_STMTS];	/* Value-stored map. */
int task_lineno[MAX_TASKS][MAX_STMTS];	/* Source line number. */

/* Dependency maps. */

dependency_map_t dep_map;	/* Explicitly specified dependencies */
dependency_map_t dep_map_lineno;/* Source line number. */
dependency_map_t po_map;	/* Program-order dependencies. */
dependency_map_t prop_map;	/* CCCC-propagated dependencies. */
dependency_map_t hb;		/* CCCC per-task happens-before dependencies. */

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
			task_vars[i][j] = -1;
			task_rvals[i][j] = -1;
			task_wvals[i][j] = -1;
			task_lineno[i][j] = 0;
		}
	}
	clear_dependency_map(dep_map);
	clear_dependency_map(dep_map_lineno);
	clear_dependency_map(po_map);
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
