				/*
				 * CCCC analysis toy.
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
				
				#include <stdio.h>
				#include <ctype.h>
				
				#include "cccc-data.h"
				#include "cccc-traverse.h"
				#include "cccc-output.h"
				#include "cccc-input.h"
				#include "cccc-cycle.h"
				
				/*
				 * Build all the program-order dependencies into po_map.
				 * Later need to pay attention only to memory barriers @@@ .
				 */
				void build_program_order(void)
				{
					int t;
					int fs;
					int ts;
				
					for_each_task(t) {
						for_each_statement_pair_in_task(t, fs, ts) {
							po_map[t][fs][t][ts] = 1;
						}
					}
				}
				
				/*
				 * Do the CCCC "propagation" dependencies into prop_map.
				 * This function currently implicitly assumes each task's statements
				 * run in program order, which will need to be reworked when explicit
				 * memory barriers are added.  @@@
				 */
				void propagate_dependencies(void)
				{
					int s1;
					int s2a;
					int s2b;
					int t1;
					int t2;
					int change=1;
				    while (change) {
				    	change=0;
						for_each_task(t2) {
							for_each_statement_pair_in_task(t2, s2a, s2b) {
								for_each_other_task(t2, t1) {
									for_each_statement_in_task(t1, s1) {
										if ((dep_map[t1][s1][t2][s2a]||prop_map[t1][s1][t2][s2a])&&
											prop_map[t1][s1][t2][s2b]!=1 ) {
											prop_map[t1][s1][t2][s2b]=1;
											change=1;
										}
										if ((dep_map[t2][s2b][t1][s1]||prop_map[t2][s2b][t1][s1])&&
											prop_map[t2][s2a][t1][s1]!=1) {
											prop_map[t2][s2a][t1][s1]=1;
											change=1;
										}
									}
								}
							}
						}
				    }
				}
				
				/*
				 * Build the CCCC local-consistency dependencies in array hb, based on the
				 * specified taskid and dependency map.  To combine the effects of multiple
				 * dependency maps, invoke this function on each in sequence.  This function
				 * currently assumes tasks are program-ordered, which will need to change
				 * when explicit memory barriers are added.
				 */
				void build_local_consistency_dep(int taskid, dependency_map_t dep)
				{
					int ft;
					int fs;
					int tt;
					int ts;
				
					for_each_dependency(dep, ft, fs, tt, ts) {
						if ((statement_is_write(ft, fs) ||
						     (ft == taskid)) &&
						    (statement_is_write(tt, ts) ||
						     (tt == taskid))) {
							hb[ft][fs][tt][ts] = 1;
						}
					}
				}
				
				/*
				 * Build the CCCC local-consistency dependencies in array hb for the
				 * specified taskid.  Use the dep_map, po_map, and prop_map dependencies.
				 */
				void build_local_consistency(int taskid)
				{
				
					clear_dependency_map(hb);
					build_local_consistency_dep(taskid, dep_map);
					build_local_consistency_dep(taskid, po_map);
					build_local_consistency_dep(taskid, prop_map);
				}
				void build_global_consistency_dep(int taskid, dependency_map_t dep)
				{
					int ft;
					int fs;
					int tt;
					int ts;
				
					for_each_dependency(dep, ft, fs, tt, ts) {
							hb[ft][fs][tt][ts] = 1;
					}
				}
				
				void build_global_hb(void) {
					clear_dependency_map(hb);
					int ft;
					int fs;
					int tt;
					int ts;
					for_each_dependency(dep_map, ft, fs, tt, ts) {
							hb[ft][fs][tt][ts] = 1;
					}
					for_each_dependency(po_map, ft, fs, tt, ts) {
							hb[ft][fs][tt][ts] = 1;
					}
					for_each_dependency(prop_map, ft, fs, tt, ts) {
							hb[ft][fs][tt][ts] = 1;
					}
				}
				
				/*
				 * Check the CCCC local-consistency criterion for each task in turn.
				 */
				void check_local_consistency(void)
				{
					int foundcycle = 0;
					int taskid;
				
					for_each_task(taskid) {
						build_local_consistency(taskid);
						printf("Happens-before dependencies for task %d\n", taskid);
						dump_these_dependencies(hb);
						if (has_cycle(hb)) {
							printf("Task %d sees cycle\n", taskid);
							foundcycle = 1;
						}
					}
					if (foundcycle) {
						printf("Cycle found: illegal CCCC execution\n");
					} else {
						printf("No cycles found.\n");
					}
				}
				/*
				 * 
				 * Check to make sure that write dependencies are full linear chains.
				 * Also make sure that all reads depend on some write, but infer
				 * read-on-write dependencies from values where possible.
				 * @@@ Later on the read stuff... @@@
				 * Check the CCCC write-consistency criterion for each task in turn.
				 */
				void check_write_consistency(void)
				{
					int var;
					int r;
					int ft;
					int fs;
					int tt;
					int ts;
					int nwrite[MAX_VARS] = { 0 };
					int maxRank[MAX_VARS] = { 0 };
								
							
					clear_var_dependency();
					build_global_hb();
					//printf("Global dependencies\n");
					//dump_these_dependencies(hb);
					
					/* Assume that the user has cleared up other write warnings. */
					for_each_dependency(hb, ft, fs, tt, ts) {
						if (statement_is_write(ft, fs) &&
							statement_is_write(tt, ts) &&
							(task_vars[ft][fs] == task_vars[tt][ts])) {
						  vb[tt][ts]++;
						  printf("vb[%i][%i] set to %i.\n", tt, ts, vb[tt][ts]);
						  var = task_vars[tt][ts];
						  if (maxRank[var] < vb[tt][ts]) {
							maxRank[var]=vb[tt][ts];
						  }
						}
					}
					for_each_statement(ft, fs) {
						if (statement_is_write(ft, fs)) {
							nwrite[task_vars[ft][fs]]++;
						}
					}
								
					int max=0;
					int i;
					int j;
					for_each_variable(i) {
						if (nwrite[i] > 0) {
						if (maxRank[i]+1 != nwrite[i]) {
							// max Rank for variable i must be equal to #writes - 1
								printf("Writes on variable %c are not totally ordered (maxRank is %i, instead of %i).\n",
								i+'a',
								maxRank[i],
								nwrite[i]-1);
								return;
						}
						if (maxRank[i] > max) {
							max = maxRank[i];
						}
					}
					}		
					//printf("Max=%i.\n", max);
					int rank[MAX_VARS][max+1]; // set to 1 and checked if equal to 1.
					for_each_variable(i) {
							for (j=0; j < max+1; j++) {
								rank[i][j]=-1;
							}
					}
					for_each_statement(ft, fs) {
						var=task_vars[ft][fs];
						r=vb[ft][fs];
						if (statement_is_write(ft, fs)) {
							if (rank[var][r]>=0) {
								// Cannot have two writes for the same var with same rank.
								printf("Writes on variable %c are not totally ordered (%i.%i and %i.(%i/10) have rank %i).\n",
								var+'a',ft,fs, rank[var][r]%10, rank[var][r], r);
								return;
							}
							rank[var][r]=ft*10+fs;
							//printf("rank[%c][%i] set, since vb[%i][%i] is %i.\n", var+'a', r, ft,fs, r);
						}
					}
					for_each_variable(i) {
						if (nwrite[i]>0) {
						for (j=0; j<nwrite[i]; j++) {
							if (rank[i][j]==-1) {
								// Each rank must have a corresponding statement.
								printf("Writes on variable %c are not totally ordered (no stm with rank %i).\n",
										i+'a',
										j);
								return;
							}
						}
					}	
					}
					printf("All writes are ordered.\n");
				}
				
				/*
				 * Take the program and explicit dependencies on standard input,
				 * analyze, and output analysis on standard output.
				 *
				 * Input lines, statements:
				 *	1 2 w a 1 # task 1 statement 2 write variable "a" value 1.
				 *	1 3 r a 1 # task 1 statement 3 read variable "a" value 1.
				 *	2 0 r a   # task 1 statement 0 read variable "a" value unspecified.
				 * Input lines, explicit dependencies:
				 *	1.2 -> 2.0 # task 2 statement 0 depends on task 1 statement 2.
				 *
				 * Fixed format.  Tasks range from 0-9.  Statements within tasks also
				 * range from 0-9.  Variables range from a-z.  Values range from 0-9.
				 * Operations are "r" (read) and "w" (write).  Memory barriers TBD. @@@
				 * Trailing blanks and empty lines are ignored, as are any characters
				 * following a pound sign ("#").
				 *
				 * Statements must be defined before a dependency may reference them.
				 * Statements within a given task must appear in numerical order.
				 */
				int main(int argc, char *argv[])
				{
					initialize();
					read_program();
				
					build_program_order();
					propagate_dependencies();
				
					dump_statements();
					dump_dependencies();
					dump_po_dependencies();
					dump_prop_dependencies();
				
					check_local_consistency();
					check_write_consistency();
				
					check_warnings();
				
					exit(0);
				}
