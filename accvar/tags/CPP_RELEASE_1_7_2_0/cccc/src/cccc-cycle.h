/*
 * Cycle-detection functions for CCCC analysis toy.
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
 * Recursive cycle-detection helping function.  Does it the stupid
 * way, maintaining an explicit list of statements traversed thus far,
 * and scanning the matrix form of the dependency graph.
 */
int has_cycle_help(dependency_map_t dep, statement_t stmtpath[], int pathlen)
{
	int ft = stmtpath[pathlen - 1].taskid;
	int fs = stmtpath[pathlen - 1].stmtid;
	int i;
	int tt;
	int ts;

	for_each_statement(tt, ts) {
		if (dep[ft][fs][tt][ts]) {
			stmtpath[pathlen].taskid = tt;
			stmtpath[pathlen].stmtid = ts;
			for (i = 0; i < pathlen; i++) {
				if ((tt == stmtpath[i].taskid) &&
				    (ts == stmtpath[i].stmtid)) {
					break;	/* found a cycle. */
				}
			}
			if (i != pathlen) {
				printf("Cycle located:\n");
				print_cycle(stmtpath, i, pathlen);
				return 1;
			}
			if (has_cycle_help(dep, stmtpath, pathlen + 1)) {
				return 1;
			}
		}
	}
	return 0;
}

/*
 * Checks for cycles in the specified dependency map starting with
 * each dependency in turn.  Quits once the first cycle is located.
 * Also stupid, as it doesn't check for dependencies having already
 * been traversed.  Amazing what you can get away with for sufficiently
 * small problem size!  ;-)
 */
int has_cycle(dependency_map_t dep)
{
	int ft;
	int fs;
	int tt;
	int ts;
	statement_t stmtpath[MAX_TASKS * MAX_STMTS];

	for_each_dependency(dep, ft, fs, tt, ts) {
		stmtpath[0].taskid = ft;
		stmtpath[0].stmtid = fs;
		stmtpath[1].taskid = tt;
		stmtpath[1].stmtid = ts;
		if (has_cycle_help(dep, stmtpath, 2)) {
			return 1;
		}
	}
	return 0;
}
