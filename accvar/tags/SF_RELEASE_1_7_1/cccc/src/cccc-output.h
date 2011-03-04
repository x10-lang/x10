/*
 * Output functions for CCCC analysis toy.
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
 * Dump a memory-barrier statement.
 */
void dump_barrier(int taskid, int stmtid, int mb)
{
	int i;

	for (i = 0; mbdecode[i].name != NULL; i++) {
		if (mbdecode[i].properties == mb) {
			printf("%d.%d %s\n",
			       taskid, stmtid,
			       mbdecode[i].name);
			break;
		}
	}
	if (mbdecode[i].name == NULL) {
		printf("%d.%d ?%#x?\n", taskid, stmtid, mb);
	}
}

/*
 * Dump the statements in the same form that they are input.
 */
void dump_statements(void)
{
	int i;
	int mb;
	char opcode;
	int taskid;
	int stmtid;
	char val1;
	char val2;

	printf("# Statements\n");
	for_each_statement(taskid, stmtid) {
		val1 = task_rvals[taskid][stmtid];
		val2 = task_wvals[taskid][stmtid];
		if (val1 == -1) {
			val1 = val2;
			val2 = -1;
		}
		mb = task_mbs[taskid][stmtid];
		if (task_ops[taskid][stmtid] == no_op) {
			if (mb != 0) {

				/* Explicit memory barrier. */

				dump_barrier(taskid, stmtid, mb);
			}
		} else {
			opcode = op_code[task_ops[taskid][stmtid]];
			if (mb == OP_SYNC) {
				
				/* Implicit memory barrier. */

				opcode = toupper(opcode);
			}
			printf("%d.%d %c %c %c %c\n",
			       taskid, stmtid, opcode,
			       task_vars[taskid][stmtid] + 'a',
			       val1 < 0 ? ' ' : val1 + '0',
			       val2 < 0 ? ' ' : val2 + '0');
			if ((mb != 0) &&
			    (mb != OP_SYNC)) {

				/* Explicit barrier on statement. */

				dump_barrier(taskid, stmtid, mb);
			}
		}
	}
}

/*
 * Dump the dependencies recorded in the specified dependency map.
 */
void
dump_these_dependencies(dependency_map_t dep)
{
	int ft;
	int fs;
	int tt;
	int ts;

	for_each_dependency(dep, ft, fs, tt, ts) {
		printf("%d.%d -> %d.%d\n", ft, fs, tt, ts);
	}
}

/*
 * Dump the manually specified dependencies.
 */
void dump_dependencies(void)
{
	printf("# Manually specified dependencies\n");
	dump_these_dependencies(dep_map);
}

/*
 * Dump the manually specified dependencies.
 */
void dump_read_dependencies(void)
{
	printf("# Inferred write-to-read dependencies\n");
	dump_these_dependencies(read_map);
}

/*
 * Dump the program-order dependencies.
 */
void dump_po_dependencies(void)
{
	printf("# Program-order dependencies\n");
	dump_these_dependencies(po_map);
}

/*
 * Dump the CCCC-propagated dependencies.
 */
void dump_prop_dependencies(void)
{
	printf("# Propagated dependencies\n");
	dump_these_dependencies(prop_map);
}

/*
 * Print the specified cycle from the specified statement path array.
 */
void print_cycle(statement_t stmtpath[], int firststmt, int laststmt)
{
	int i;
	int ft;
	int fs;
	int tt;
	int ts;

	for (i = firststmt; i < laststmt; i++) {
		ft = stmtpath[i].taskid;
		fs = stmtpath[i].stmtid;
		tt = stmtpath[i + 1].taskid;
		ts = stmtpath[i + 1].stmtid;
		printf("%d.%d -> %d.%d ", ft, fs, tt, ts);
		if (dep_map[ft][fs][tt][ts]) {
			printf(" (manual)");
		}
		if (read_map[ft][fs][tt][ts]) {
			printf(" (inferred write-to-read)");
		}
		if (po_map[ft][fs][tt][ts]) {
			printf(" (program order)");
		}
		if (prop_map[ft][fs][tt][ts]) {
			printf(" (propagation)");
		}
		printf("\n");
	}
}

void die(int lineno, char *msg)
{
	fprintf(stderr, "Fatal: line: %d: %s\n", lineno, msg);
	exit(-1);
}

int numwarnings = 0;

void warn(int lineno, char *msg)
{
	fprintf(stderr, "Warning: line: %d: %s\n", lineno, msg);
	numwarnings++;
}

void warn2(int lineno1, int lineno2, char *msg)
{
	fprintf(stderr, "Warning: lines: %d/%d: %s\n", lineno1, lineno2, msg);
	numwarnings++;
}

void check_warnings(void)
{
	if (numwarnings != 0) {
		printf("%d warnings issued, please fix and rerun\n",
		       numwarnings);
	}
}
