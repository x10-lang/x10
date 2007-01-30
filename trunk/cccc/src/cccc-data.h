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

enum optype { no_op, read_op, write_op, };

typedef int dependency_map_t[MAX_TASKS][MAX_STMTS][MAX_TASKS][MAX_STMTS];
typedef int var_dependency_t[MAX_TASKS][MAX_STMTS];
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
int task_vals[MAX_TASKS][MAX_STMTS];	/* Value loaded/stored map. */
int task_lineno[MAX_TASKS][MAX_STMTS];	/* Source line number. */

/* Dependency maps. */

dependency_map_t dep_map;	/* Explicitly specified dependencies */
dependency_map_t dep_map_lineno;/* Source line number. */
dependency_map_t po_map;	/* Program-order dependencies. */
dependency_map_t prop_map;	/* CCCC-propagated dependencies. */
dependency_map_t hb;		/* CCCC per-task happens-before dependencies. */
var_dependency_t vb;        /* Rank in per-variable total order.*/

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
void clear_var_dependency(void)
{
	int i;
	int j;
	
	for (i = 0; i < MAX_TASKS; i++) {
		for (j = 0; j < MAX_STMTS; j++) {
		    vb[i][j]=0;
		}
	}
}

/*
 * Like the name says, initialize the above data.
 */
void initialize(void)
{
	int i;
	int j;

	for (i = 0; i < MAX_TASKS; i++) {
		task_last_stmt[i] = -1;
		task_n_stmts[i] = 0;
		for (j = 0; j < MAX_STMTS; j++) {
			task_stmts[i][j] = -1;
			task_stmt_map[i][j] = -1;
			task_ops[i][j] = no_op;
			task_vars[i][j] = -1;
			task_vals[i][j] = -1;
			task_lineno[i][j] = 0;
		}
	}
	clear_dependency_map(dep_map);
	clear_dependency_map(dep_map_lineno);
	clear_dependency_map(po_map);
	clear_dependency_map(prop_map);
	clear_dependency_map(hb);
	clear_var_dependency();
}

/*
 * Does the specified statement exist in the specified task?
 */
int statement_exists(int task, int stmt)
{
	return task_stmt_map[task][stmt] != -1;
}

/*
 * Is the specified statement in the specfied task a read?
 */
int statement_is_read(int task, int stmt)
{
	if (!statement_exists(task, stmt)) {
		abort();
	}
	return task_ops[task][stmt] == read_op;
}

/*
 * Is the specified statement in the specfied task a write?
 */
int statement_is_write(int task, int stmt)
{
	if (!statement_exists(task, stmt)) {
		abort();
	}
	return task_ops[task][stmt] == write_op;
}
