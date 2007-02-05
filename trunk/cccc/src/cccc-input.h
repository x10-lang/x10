/*
 * Input and parsing functions for CCCC analysis toy.
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

/*
 * Parse the dependency in inputline.  Use lineno if needed for error
 * messages.  If valid, place the resulting dependency in dep_map.
 */
void parse_dependency(char *inputline, int lineno)
{
	int fromtask;
	int fromstmt;
	int totask;
	int tostmt;
	int task;
	int stmt;

	if ((strlen(inputline) != 10) ||
	    !isdigit(inputline[0]) ||
	    (inputline[1] != '.') ||
	    !isdigit(inputline[2]) ||
	    (inputline[3] != ' ') ||
	    (inputline[4] != '-') ||
	    (inputline[5] != '>') ||
	    (inputline[6] != ' ') ||
	    !isdigit(inputline[7]) ||
	    (inputline[8] != '.') ||
	    !isdigit(inputline[9])) {
		die(lineno, "Bad dependency: format is \"1.2 -> 3.4\"");
	}
	fromtask = inputline[0] - '0';
	fromstmt = inputline[2] - '0';
	totask = inputline[7] - '0';
	tostmt = inputline[9] - '0';

	if (!statement_exists(fromtask, fromstmt)) {
		die(lineno, "From-statement undefined");
	}
	if (!statement_exists(totask, tostmt)) {
		die(lineno, "To-statement undefined");
	}
	if (fromtask == totask && fromstmt > tostmt) {
		warn(lineno, "Dependency reverses program order");
	}
	if (fromtask == totask && fromstmt == tostmt) {
		warn(lineno, "Statement depends on itself");
	}
	if (statement_is_read(fromtask, fromstmt) &&
	    statement_is_read(totask, tostmt)) {
		warn(lineno, "Read-to-read dependency");
	}
	if (statement_does_write(fromtask, fromstmt) &&
	    statement_does_read(totask, tostmt) &&
	    (task_vars[fromtask][fromstmt] == task_vars[totask][tostmt]) &&
	    (task_wvals[fromtask][fromstmt] != task_rvals[totask][tostmt]) &&
	    (task_rvals[totask][tostmt] != -1)) {
		warn(lineno, "Value read not written by preceding write");
	}
	if (statement_does_write(fromtask, fromstmt) &&
	    statement_does_write(totask, tostmt)) {
		if (task_vars[fromtask][fromstmt] !=
		    task_vars[totask][tostmt]) {
		    	warn(lineno,
			     "Dependent write to different variable");
		}
		for_each_statement_does_write(task, stmt) {
			if ((task_vars[task][stmt] ==
			     task_vars[fromtask][fromstmt]) &&
			    (dep_map[fromtask][fromstmt][task][stmt])) {
				warn2(dep_map_lineno[fromtask][fromstmt][task][stmt],
				      lineno,
				      "Double downstream write dependency");
			}
			if ((task_vars[task][stmt] ==
			     task_vars[totask][tostmt]) &&
			    statement_does_write(task, stmt) &&
			    (dep_map[task][stmt][totask][tostmt])) {
				warn2(dep_map_lineno[task][stmt][totask][tostmt],
				      lineno,
				      "Double upstream write dependency");
			}
		}
	}

	dep_map[fromtask][fromstmt][totask][tostmt] = 1;
	dep_map_lineno[fromtask][fromstmt][totask][tostmt] = lineno;
}

/*
 * Parse memory barrier directive from inputline.  These can either
 * be applied to an existing statement or be standalone memory barriers.
 * It is not legal to apply more than one memory barrier to the same
 * statement.  Note that capitalized opcodes are shorthand for applying
 * an "mb" directive to the lowercase version of that same opcode.
 */
void parse_memory_barrier(char *inputline, int lineno)
{
	int i;
	int mb;
	char *mbcp;
	int taskid;
	int stmtid;

	if (!isdigit(inputline[0]) ||
	    (inputline[1] != '.') ||
	    !isdigit(inputline[2])) {
		die(lineno, "Bad memory barrier: format is \"t.s membar\"");
	}
	taskid = inputline[0] - '0';
	stmtid = inputline[2] - '0';
	mbcp = &inputline[4];
	if (stmtid < task_last_stmt[taskid]) {
		die(lineno, "Task statement out of order");
	}
	for (i = 0; mbdecode[i].name != NULL; i++) {
		if (strcmp(mbcp, mbdecode[i].name) == 0) {
			mb = mbdecode[i].properties;
			if (!statement_exists(taskid, stmtid)) {
				if ((mb & OP_SYNC) &&
				    !(mb & OP_DIVIDE) &&
				    ((mb & OP_SYNC_BEFORE == 0) ||
				     (mb & OP_SYNC_AFTER == 0))) {
					die(lineno,
					    "One-tailed memory barrier "
					    "must be attached to instruction");
				}
				task_ops[taskid][stmtid] = no_op;
				task_lineno[taskid][stmtid] = lineno;
				task_n_stmts[taskid]++;
			}
			if (task_mbs[taskid][stmtid] != 0) {
				die(lineno, "Duplicate memory barrier");
			}
			task_last_stmt[taskid] = stmtid;
			task_stmts[taskid][task_n_stmts[taskid]] = stmtid;
			task_stmt_map[taskid][stmtid] = task_n_stmts[taskid];
			task_mbs[taskid][stmtid] = mb;
			return;
		}
	}
	die(lineno, "Invalid memory barrier");
}

/*
 * Parse statement from inputline, using lineno in any necessary error
 * messages.  Update the various task_[] maps upon successful parse.
 */
void parse_statement(char *inputline, int lineno)
{
	int taskid;
	int stmtid;
	int op;
	char opcodelower;
	int var;
	int val;
	int rval;
	int wval;

	opcodelower = tolower(inputline[4]);
	if (((strlen(inputline) != 7) &&
	     (strlen(inputline) != 9) &&
	     (strlen(inputline) != 11)) ||
	    !isdigit(inputline[0]) ||
	    (inputline[1] != '.') ||
	    !isdigit(inputline[2]) ||
	    !isblank(inputline[3]) ||
	    (((opcodelower != 'a') ||
	      (strlen(inputline) != 11)) &&
	     (opcodelower != 'r') &&
	     ((opcodelower != 'w') ||
	      (strlen(inputline) != 9))) ||
	    !isblank(inputline[5]) ||
	    !islower(inputline[6]) ||
	    ((strlen(inputline) != 7) &&
	     (!isblank(inputline[7]) ||
	      !isdigit(inputline[8])))) {
		die(lineno, "Bad statement: format is \"t.s o v v\"");
	}

	taskid = inputline[0] - '0';
	stmtid = inputline[2] - '0';
	op = op_decode[inputline[4]];
	var = inputline[6] - 'a';
	rval = wval = -1;
	if (strlen(inputline) == 9) {
		if (op == read_op) {
			rval = inputline[8] - '0';
		} else {
			wval = inputline[8] - '0';
		}
	} else if (strlen(inputline) == 11) {
		rval = inputline[8] - '0';
		wval = inputline[10] - '0';
	}
	if (stmtid <= task_last_stmt[taskid]) {
		die(lineno, "Task statement out of order");
	}
	task_last_stmt[taskid] = stmtid;
	task_stmts[taskid][task_n_stmts[taskid]] = stmtid;
	task_stmt_map[taskid][stmtid] = task_n_stmts[taskid];
	task_ops[taskid][stmtid] = op;
	task_mbs[taskid][stmtid] = op_mb_decode[inputline[4]];
	task_vars[taskid][stmtid] = var;
	task_rvals[taskid][stmtid] = rval;
	task_wvals[taskid][stmtid] = wval;
	task_lineno[taskid][stmtid] = lineno;
	task_n_stmts[taskid]++;
}

/*
 * Read a program (statements and dependencies) from standard input.
 */
int read_program(void)
{
	int i;
	char *cp;
	char inputline[4096];
	int linelen;
	int lineno = 0;

	for (;;) {
		if (fgets(inputline, sizeof(inputline), stdin) == NULL) {
			break;
		}
		lineno++;

		/* Trim comments and trailing whitespace. */

		cp = strchr(inputline, '#');
		if (cp != NULL) {
			*cp = '\0';
		}
		cp = strchr(inputline, '\n');
		if (cp != NULL) {
			*cp = '\0';
		}
		for (i = strlen(inputline) - 1; i >= 0; i--) {
			if (!isblank(inputline[i])) {
				break;
			}
			inputline[i] = '\0';
		}

		/* Ignore empty/comment lines. */

		if (strlen(inputline) == 0) {
			continue;
		}
		if (strlen(inputline) < 6) {
			die(lineno, "Line too short");
		}

		/* Parse. */

		if (inputline[4] == '-') {
			parse_dependency(inputline, lineno);
		} else if (isalpha(inputline[5])) {
			parse_memory_barrier(inputline, lineno);
		} else {
			parse_statement(inputline, lineno);
		}
	}
	return lineno;
}
