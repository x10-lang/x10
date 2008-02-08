package msat.core;
import msat.mtl.*;

class Solver {
	
//	Just like 'assert()' but expression will be evaluated in the release version as well.
	static  void check(boolean expr) { assert(expr); }
	// Problem specification:
	//
	// Add a new variable with parameters specifying variable mode.
	/**Creates a new SAT variable in the solver. If 'decision_var' is cleared, variable will not be
	 used as a decision variable (NOTE! This has effects on the meaning of a SATISFIABLE result).
		*/
		/*Var*/ int newVar(boolean sign, boolean dvar)
		{
		    int v = nVars();
		    watches   .push();          // (list for positive literal)
		    watches   .push();          // (list for negative literal)
		    reason    .push(null);
		    assigns   .push(toInt(Lit.VAR_UNDEF));
		    level     .push(-1);
		    activity  .push(0);
		    seen      .push(0);

		    polarity    .push((char)sign);
		    decision_var.push((char)dvar);

		    insertVarOrder(v);
		    return v;
		}
	int newVar() { return newVar(true, true);}
	int newVar(boolean polarity) { return newVar(polarity, true);}
     // Add a clause to the solver. NOTE! 'ps' may be shrunk by this method!

	boolean addClause(Vec<Lit> ps)
	{
	    assert(decisionLevel() == 0);

	    if (!ok)
	        return false;
	    else{
	        // Check if clause is satisfied and remove false/duplicate literals:
	        sort(ps);
	        Lit p; int i, j;
	        for (i = j = 0, p = Lit.LIT_UNDEF; i < ps.size(); i++)
	        	// TODO vj: Check .. should this be Lit.TRUE?
	            if (value(ps.get(i)) == Boolean.TRUE || ps[i] == ~p)
	                return true;
	            else if (value(ps.get(i)) != Boolean.FALSE && ps.get(i) != p)
	            {
	            	j++;
	            	p = ps.get(i);
	            	ps.set(j,p);
	            }
	        ps.shrink(i - j);
	    }

	    if (ps.size() == 0)
	        return ok = false;
	    else if (ps.size() == 1){
	        assert(value(ps.get(0)) == Boolean.UNDEF);
	        uncheckedEnqueue(ps.get(0));
	        return ok = (propagate() == null);
	    }else{
	        Clause* c = new Clause(ps, false);
	        clauses.push(c);
	        attachClause(*c);
	    }

	    return true;
	}

	// Solving:
	//
//	Removes already satisfied clauses.

	/*_________________________________________________________________________________________________
	|
	|  simplify : [void]  ->  [bool]
	|  
	|  Description:
	|    Simplify the clause database according to the current top-level assigment. Currently, the only
	|    thing done here is the removal of satisfied clauses, but more things can be put here.
	|________________________________________________________________________________________________@*/
	protected boolean simplify()
	{
	    assert(decisionLevel() == 0);

	    if (!ok || propagate() != null)
	        return ok = false;

	    if (nAssigns() == simpDB_assigns || (simpDB_props > 0))
	        return true;

	    // Remove satisfied clauses:
	    removeSatisfied(learnts);
	    if (remove_satisfied)        // Can be turned off.
	        removeSatisfied(clauses);

	    // Remove fixed variables from the variable heap:
	    order_heap.filter(VarFilter(*this));

	    simpDB_assigns = nAssigns();
	    simpDB_props   = clauses_literals + learnts_literals;   // (shouldn't depend on stats really, but it will do for now)

	    return true;
	}

//	Search for a model that respects a given set of assumptions.
	
	boolean solve( Vec<Lit> assumps)
	{
	    model.clear();
	    conflict.clear();

	    if (!ok) return false;

	    assumps.copyTo(assumptions);

	    double  nof_conflicts = restart_first;
	    double  nof_learnts   = nClauses() * learntsize_factor;
	    lbool   status        = Lit.LIT_UNDEF;

	    if (verbosity >= 1){
	        reportf("============================[ Search Statistics ]==============================\n");
	        reportf("| Conflicts |          ORIGINAL         |          LEARNT          | Progress |\n");
	        reportf("|           |    Vars  Clauses Literals |    Limit  Clauses Lit/Cl |          |\n");
	        reportf("===============================================================================\n");
	    }

	    // Search:
	    while (status == Lit.LIT_UNDEF){
	        if (verbosity >= 1)
	            reportf("| %9d | %7d %8d %8d | %8d %8d %6.0f | %6.3f %% |\n", (int)conflicts, order_heap.size(), nClauses(), (int)clauses_literals, (int)nof_learnts, nLearnts(), (double)learnts_literals/nLearnts(), progress_estimate*100), fflush(stdout);
	        status = search((int)nof_conflicts, (int)nof_learnts);
	        nof_conflicts *= restart_inc;
	        nof_learnts   *= learntsize_inc;
	    }

	    if (verbosity >= 1)
	        reportf("===============================================================================\n");


	    if (status == Boolean.TRUE){
	        // Extend & copy model:
	        model.growTo(nVars());
	        for (int i = 0; i < nVars(); i++) model[i] = value(i);
	#ifndef NDEBUG
	        verifyModel();
	#endif
	    }else{
	        assert(status == l_False);
	        if (conflict.size() == 0)
	            ok = false;
	    }

	    cancelUntil(0);
	    return status == Boolean.TRUE;
	}

//	=================================================================================================
//	 Debug methods:


	protected void verifyModel()
	{
	    bool failed = false;
	    for (int i = 0; i < clauses.size(); i++){
	        assert(clauses[i]->mark() == 0);
	        Clause& c = *clauses[i];
	        for (int j = 0; j < c.size(); j++)
	            if (modelValue(c[j]) == Boolean.TRUE)
	                goto next;

	        reportf("unsatisfied clause: ");
	        printClause(*clauses[i]);
	        reportf("\n");
	        failed = true;
	    next:;
	    }

	    assert(!failed);

	    reportf("Verified %d original clauses.\n", clauses.size());
	}

	// Extra results: (read-only member variable)
	//
	Vec<Boolean> model;             // If problem is satisfiable, this vector contains the model (if any).
	Vec<Lit>   conflict;          // If problem is unsatisfiable (possibly under assumptions),
	// this vector represent the final conflict clause expressed in the assumptions.

	// Mode of operation:
	//
	double    var_decay;          // Inverse of the variable activity decay factor.                                            (default 1 / 0.95)
	double    clause_decay;       // Inverse of the clause activity decay factor.                                              (1 / 0.999)
	double    random_var_freq;    // The frequency with which the decision heuristic tries to choose a random variable.        (default 0.02)
	int       restart_first;      // The initial restart limit.                                                                (default 100)
	double    restart_inc;        // The factor with which the restart limit is multiplied in each restart.                    (default 1.5)
	double    learntsize_factor;  // The intitial limit for learnt clauses is a factor of the original clauses.                (default 1 / 3)
	double    learntsize_inc;     // The limit for learnt clauses is multiplied with this factor each restart.                 (default 1.1)
	boolean      expensive_ccmin;    // Controls conflict clause minimization.                                                    (default TRUE)
	int       polarity_mode;      // Controls which polarity the decision heuristic chooses. See enum below for allowed modes. (default polarity_false)
	int       verbosity;          // Verbosity level. 0=silent, 1=some progress report                                         (default 0)

	enum polarity { polarity_true = 0, polarity_false = 1, polarity_user = 2, polarity_rnd = 3 };

	// Statistics: (read-only member variable)
	//
	/*uint64_t*/ long starts, decisions, rnd_decisions, propagations, conflicts;
	/*uint64_t*/ long clauses_literals, learnts_literals, max_literals, tot_literals;


	// Helper structures:
	//
	class VarOrderLt {
		final VecDouble activity;
		boolean apply(int/*Var*/ x, int/*Var*/ y)  { return activity.get(x) > activity.get(y); }
		VarOrderLt(VecDouble act) { activity=act; }
	};


	class VarFilter {
		boolean apply(int/*Var*/ v) { return toLbool(s.assigns[v]) == Lit.LIT_UNDEF && s.decision_var[v]; }
	};

	// Solver state:
	//
	protected boolean                ok;               // If FALSE, the constraints are already unsatisfiable. No part of the solver state may be used!
	protected vec<Clause*>        clauses;          // List of problem clauses.
	protected vec<Clause*>        learnts;          // List of learnt clauses.
	protected double              cla_inc;          // Amount to bump next clause with.
	protected VecDouble         activity;         // A heuristic measurement of the activity of a variable.
	protected double              var_inc;          // Amount to bump next variable with.
	protected vec<vec<Clause*> >  watches;          // 'watches[lit]' is a list of constraints watching 'lit' (will go there if literal becomes true).
	protected VecChar           assigns;          // The current assignments (Boolean:s stored as char:s).
	protected VecChar           polarity;         // The preferred polarity of each variable.
	protected VecChar           decision_var;     // Declares if a variable is eligible for selection in the decision heuristic.
	protected Vec<Lit>            trail;            // Assignment stack; stores all assigments made in the order they were made.
	protected VecInt            trail_lim;        // Separator indices for different decision levels in 'trail'.
	protected vec<Clause*>        reason;           // 'reason[var]' is the clause that implied the variables current value, or 'null' if none.
	protected VecInt           level;            // 'level[var]' contains the level at which the assignment was made.
	protected int                 qhead;            // Head of queue (as index into the trail -- no more explicit propagation queue in MiniSat).
	protected int                 simpDB_assigns;   // Number of top-level assignments since last execution of 'simplify()'.
	protected /*int64_t*/long             simpDB_props;     // Remaining number of propagations that must be made before next execution of 'simplify()'.
	protected Vec<Lit>            assumptions;      // Current set of assumptions provided to solve by the user.
	protected Heap<VarOrderLt>    order_heap;       // A priority queue of variables ordered with respect to the variable activity.
	protected double              random_seed;      // Used by the random variable selection.
	protected double              progress_estimate;// Set by 'search()'.
	protected boolean                remove_satisfied; // Indicates whether possibly inefficient linear scan for satisfied clauses should be performed in 'simplify'.

	// Temporaries (to reduce allocation overhead). Each variable is prefixed by the method in which it is
	// used, exept 'seen' wich is used in several places.
	//
	protected VecChar           seen;
	protected Vec<Lit>            analyze_stack;
	protected Vec<Lit>            analyze_toclear;
	protected Vec<Lit>            add_tmp;

	public Solver() {
	    // Parameters: (formerly in 'SearchParams')
	    var_decay=1 / 0.95;
	    clause_decay=1 / 0.999;
	    random_var_freq=0.02;
	  restart_first=100;
	  restart_inc=1.5; 
	  learntsize_factor=((double)1/(double)3);
	  learntsize_inc=(1.1);

	    // More parameters:
	    //
	  expensive_ccmin  =(true);
	  polarity_mode =   (polarity_false);
	   verbosity     =   (0);

	    // Statistics: (formerly in 'SolverStats')
	    //
	  starts=(0);
	  decisions=(0); 
	  rnd_decisions=(0);
	  propagations=(0);
	  conflicts=(0);
	   clauses_literals=(0);
	   learnts_literals=(0);
	   max_literals=(0); 
	   tot_literals=(0);

	  ok=              (true);
	  cla_inc          =(1);
	  var_inc          =(1);
	  qhead           = (0);
	  simpDB_assigns  = (-1);
	  simpDB_props     =(0);
	  order_heap       =(VarOrderLt(activity));
	  random_seed     = (91648253);
	  progress_estimate=(0);
	  remove_satisfied =(true);
	

}


	// Main internal methods:
	//

	protected Lit      pickBranchLit    (int polarity_mode, double random_var_freq){
		// Return the next decision variable.

		    /*Var*/int next = Lit.VAR_UNDEF;

		    // Random decision:
		    if (drand(random_seed) < random_var_freq && !order_heap.empty()){
		        next = order_heap[irand(random_seed,order_heap.size())];
		        if (toLbool(assigns[next]) == Lit.VAR_UNDEF && decision_var[next])
		            rnd_decisions++; }

		    // Activity based decision:
		    while (next == var_Undef || toLbool(assigns[next]) != Lit.VAR_UNDEF || !decision_var[next])
		        if (order_heap.empty()){
		            next = var_Undef;
		            break;
		        }else
		            next = order_heap.removeMin();

		    bool sign = false;
		    switch (polarity_mode){
		    case polarity_true:  sign = false; break;
		    case polarity_false: sign = true;  break;
		    case polarity_user:  sign = polarity[next]; break;
		    case polarity_rnd:   sign = irand(random_seed, 2); break;
		    default: assert(false); }

		    return next == var_Undef ? lit_Undef : Lit(next, sign);
		}

                                                 
//	Enqueue a literal. Assumes value of literal is undefined.
	protected void     uncheckedEnqueue (Lit p) { uncheckedEnqueue(p, null);}

	protected void uncheckedEnqueue(Lit p, Clause[] from)
	{
	    assert(value(p) == Lit.LIT_UNDEF);
	    assigns [var(p)] = toInt(lbool(!sign(p)));  // <<== abstract but not uttermost effecient
	    level   [var(p)] = decisionLevel();
	    reason  [var(p)] = from;
	    trail.push(p);
	}

	
	protected boolean     enqueue(Lit p, Clause[] from);                            // Test if fact 'p' contradicts current state, enqueue otherwise.
	
	/*_________________________________________________________________________________________________
	|
	|  propagate : [void]  ->  [Clause*]
	|  
	|  Description:
	|    Propagates all enqueued facts. If a conflict arises, the conflicting clause is returned,
	|    otherwise null.
	| Perform unit propagation. Returns possibly conflicting clause.
	|  
	|    Post-conditions:
	|      * the propagation queue is empty, even if there was a conflict.
	|________________________________________________________________________________________________@*/
	protected Clause[] propagate()
	{
	    Clause[] confl     = null;
	    int     num_props = 0;

	    while (qhead < trail.size()){
	        Lit            p   = trail[qhead++];     // 'p' is enqueued fact to propagate.
	        vec<Clause[]>&  ws  = watches[toInt(p)];
	        Clause         **i, **j, **end;
	        num_props++;

	        for (i = j = (Clause**)ws, end = i + ws.size();  i != end;){
	            Clause& c = **i++;

	            // Make sure the false literal is data[1]:
	            Lit false_lit = ~p;
	            if (c[0] == false_lit)
	                c[0] = c[1], c[1] = false_lit;

	            assert(c[1] == false_lit);

	            // If 0th watch is true, then clause is already satisfied.
	            Lit first = c[0];
	            if (value(first) == Boolean.TRUE){
	                *j++ = &c;
	            }else{
	                // Look for new watch:
	                for (int k = 2; k < c.size(); k++)
	                    if (value(c[k]) != l_False){
	                        c[1] = c[k]; c[k] = false_lit;
	                        watches[toInt(~c[1])].push(&c);
	                        goto FoundWatch; }

	                // Did not find watch -- clause is unit under assignment:
	                *j++ = &c;
	                if (value(first) == l_False){
	                    confl = &c;
	                    qhead = trail.size();
	                    // Copy the remaining watches:
	                    while (i < end)
	                        *j++ = *i++;
	                }else
	                    uncheckedEnqueue(first, &c);
	            }
	        FoundWatch:;
	        }
	        ws.shrink(i - j);
	    }
	    propagations += num_props;
	    simpDB_props -= num_props;

	    return confl;
	}

	
	// Backtrack until a certain level.
	//Revert to the state at given level (keeping all assignment at 'level' but not beyond).
	//
	protected void cancelUntil(int level) {
	    if (decisionLevel() > level){
	        for (int c = trail.size()-1; c >= trail_lim.get(level); c--){
	            /*Var*/int     x  = trail.get(c).var();
	            assigns.set(x, Lit.LIT_UNDEF.toInt());
	            insertVarOrder(x); }
	        qhead = trail_lim[level];
	        trail.shrink(trail.size() - trail_lim[level]);
	        trail_lim.shrink(trail_lim.size() - level);
	    } }

	/**_________________________________________________________________________________________________
	|
	|  analyze : (confl : Clause*) (out_learnt : vec<Lit>&) (out_btlevel : int&)  ->  [void]
	|  
	|  Description:
	|    Analyze conflict and produce a reason clause.
	|  
	|    Pre-conditions:
	|      * 'out_learnt' is assumed to be cleared.
	|      * Current decision level must be greater than root level.
	|  
	|    Post-conditions:
	|      * 'out_learnt[0]' is the asserting literal at level 'out_btlevel'.
	|  
	|  Effect:
	|    Will undo part of the trail, upto but not beyond the assumption of the current decision level.
	|________________________________________________________________________________________________@
	*/
	// (bt = backtrack)
	protected void     analyze          (Clause* confl, vec<Lit>& out_learnt, int& out_btlevel) {
		    int pathC = 0;
		    Lit p     = lit_Undef;

		    // Generate conflict clause:
		    //
		    out_learnt.push();      // (leave room for the asserting literal)
		    int index   = trail.size() - 1;
		    out_btlevel = 0;

		    do{
		        assert(confl != null);          // (otherwise should be UIP)
		        Clause& c = *confl;

		        if (c.learnt())
		            claBumpActivity(c);

		        for (int j = (p == lit_Undef) ? 0 : 1; j < c.size(); j++){
		            Lit q = c[j];

		            if (!seen[var(q)] && level[var(q)] > 0){
		                varBumpActivity(var(q));
		                seen[var(q)] = 1;
		                if (level[var(q)] >= decisionLevel())
		                    pathC++;
		                else{
		                    out_learnt.push(q);
		                    if (level[var(q)] > out_btlevel)
		                        out_btlevel = level[var(q)];
		                }
		            }
		        }

		        // Select next clause to look at:
		        while (!seen[var(trail[index--])]);
		        p     = trail[index+1];
		        confl = reason[var(p)];
		        seen[var(p)] = 0;
		        pathC--;

		    }while (pathC > 0);
		    out_learnt[0] = ~p;

		    // Simplify conflict clause:
		    //
		    int i, j;
		    if (expensive_ccmin){
		        uint32_t abstract_level = 0;
		        for (i = 1; i < out_learnt.size(); i++)
		            abstract_level |= abstractLevel(var(out_learnt[i])); // (maintain an abstraction of levels involved in conflict)

		        out_learnt.copyTo(analyze_toclear);
		        for (i = j = 1; i < out_learnt.size(); i++)
		            if (reason[var(out_learnt[i])] == null || !litRedundant(out_learnt[i], abstract_level))
		                out_learnt[j++] = out_learnt[i];
		    }else{
		        out_learnt.copyTo(analyze_toclear);
		        for (i = j = 1; i < out_learnt.size(); i++){
		            Clause& c = *reason[var(out_learnt[i])];
		            for (int k = 1; k < c.size(); k++)
		                if (!seen[var(c[k])] && level[var(c[k])] > 0){
		                    out_learnt[j++] = out_learnt[i];
		                    break; }
		        }
		    }
		    max_literals += out_learnt.size();
		    out_learnt.shrink(i - j);
		    tot_literals += out_learnt.size();

		    // Find correct backtrack level:
		    //
		    if (out_learnt.size() == 1)
		        out_btlevel = 0;
		    else{
		        int max_i = 1;
		        for (int i = 2; i < out_learnt.size(); i++)
		            if (level[var(out_learnt[i])] > level[var(out_learnt[max_i])])
		                max_i = i;
		        Lit p             = out_learnt[max_i];
		        out_learnt[max_i] = out_learnt[1];
		        out_learnt[1]     = p;
		        out_btlevel       = level[var(p)];
		    }


		    for (int j = 0; j < analyze_toclear.size(); j++) seen[var(analyze_toclear[j])] = 0;    // ('seen[]' is now cleared)
		}
	}

/**_________________________________________________________________________________________________
|
|  analyzeFinal : (p : Lit)  ->  [void]
|  
|  Description:
|    Specialized analysis procedure to express the final conflict in terms of assumptions.
|    Calculates the (possibly empty) set of assumptions that led to the assignment of 'p', and
|    stores the result in 'out_conflict'.
|________________________________________________________________________________________________@
*/
	protected void     analyzeFinal     (Lit p, vec<Lit>& out_conflict){
		// COULD THIS BE IMPLEMENTED BY THE ORDINARIY "analyze" BY SOME REASONABLE GENERALIZATION?
		    out_conflict.clear();
		    out_conflict.push(p);

		    if (decisionLevel() == 0)
		        return;

		    seen[var(p)] = 1;

		    for (int i = trail.size()-1; i >= trail_lim[0]; i--){
		        Var x = var(trail[i]);
		        if (seen[x]){
		            if (reason[x] == null){
		                assert(level[x] > 0);
		                out_conflict.push(~trail[i]);
		            }else{
		                Clause& c = *reason[x];
		                for (int j = 1; j < c.size(); j++)
		                    if (level[var(c[j])] > 0)
		                        seen[var(c[j])] = 1;
		            }
		            seen[x] = 0;
		        }
		    }

		    seen[var(p)] = 0;
		}
   
	/** (helper method for 'analyze()')
	 Check if 'p' can be removed. 'abstract_levels' is used to abort early if the algorithm is
	 visiting literals at levels that cannot be removed later.

 */
	protected boolean litRedundant(Lit p, int abstract_levels)
	{
	    analyze_stack.clear(); analyze_stack.push(p);
	    int top = analyze_toclear.size();
	    while (analyze_stack.size() > 0){
	        assert(reason[var(analyze_stack.last())] != null);
	        Clause& c = *reason[var(analyze_stack.last())]; analyze_stack.pop();

	        for (int i = 1; i < c.size(); i++){
	            Lit p  = c[i];
	            if (!seen[var(p)] && level[var(p)] > 0){
	                if (reason[var(p)] != null && (abstractLevel(var(p)) & abstract_levels) != 0){
	                    seen[var(p)] = 1;
	                    analyze_stack.push(p);
	                    analyze_toclear.push(p);
	                }else{
	                    for (int j = top; j < analyze_toclear.size(); j++)
	                        seen[var(analyze_toclear[j])] = 0;
	                    analyze_toclear.shrink(analyze_toclear.size() - top);
	                    return false;
	                }
	            }
	        }
	    }

	    return true;
	}


	 // Search for a given number of conflicts.                                                   
	/*_________________________________________________________________________________________________
	|
	|  search : (nof_conflicts : int) (nof_learnts : int) (params : const SearchParams&)  ->  [lbool]
	|  
	|  Description:
	|    Search for a model the specified number of conflicts, keeping the number of learnt clauses
	|    below the provided limit. NOTE! Use negative value for 'nof_conflicts' or 'nof_learnts' to
	|    indicate infinity.
	|  
	|  Output:
	|    'Boolean.TRUE' if a partial assigment that is consistent with respect to the clauseset is found. If
	|    all variables are decision variables, this means that the clause set is satisfiable. 'l_False'
	|    if the clause set is unsatisfiable. 'Lit.LIT_UNDEF' if the bound on number of conflicts is reached.
	|________________________________________________________________________________________________@*/
	protected Boolean search(int nof_conflicts, int nof_learnts)
	{
	    assert(ok);
	    int         backtrack_level;
	    int         conflictC = 0;
	    vec<Lit>    learnt_clause;

	    starts++;

	    bool first = true;

	    for (;;){
	        Clause* confl = propagate();
	        if (confl != null){
	            // CONFLICT
	            conflicts++; conflictC++;
	            if (decisionLevel() == 0) return l_False;

	            first = false;

	            learnt_clause.clear();
	            analyze(confl, learnt_clause, backtrack_level);
	            cancelUntil(backtrack_level);
	            assert(value(learnt_clause[0]) == Lit.LIT_UNDEF);

	            if (learnt_clause.size() == 1){
	                uncheckedEnqueue(learnt_clause[0]);
	            }else{
	                Clause* c = Clause_new(learnt_clause, true);
	                learnts.push(c);
	                attachClause(*c);
	                claBumpActivity(*c);
	                uncheckedEnqueue(learnt_clause[0], c);
	            }

	            varDecayActivity();
	            claDecayActivity();

	        }else{
	            // NO CONFLICT

	            if (nof_conflicts >= 0 && conflictC >= nof_conflicts){
	                // Reached bound on number of conflicts:
	                progress_estimate = progressEstimate();
	                cancelUntil(0);
	                return Lit.LIT_UNDEF; }

	            // Simplify the set of problem clauses:
	            if (decisionLevel() == 0 && !simplify())
	                return l_False;

	            if (nof_learnts >= 0 && learnts.size()-nAssigns() >= nof_learnts)
	                // Reduce the set of learnt clauses:
	                reduceDB();

	            Lit next = lit_Undef;
	            while (decisionLevel() < assumptions.size()){
	                // Perform user provided assumption:
	                Lit p = assumptions[decisionLevel()];
	                if (value(p) == Boolean.TRUE){
	                    // Dummy decision level:
	                    newDecisionLevel();
	                }else if (value(p) == l_False){
	                    analyzeFinal(~p, conflict);
	                    return l_False;
	                }else{
	                    next = p;
	                    break;
	                }
	            }

	            if (next == lit_Undef){
	                // New variable decision:
	                decisions++;
	                next = pickBranchLit(polarity_mode, random_var_freq);

	                if (next == lit_Undef)
	                    // Model found:
	                    return Boolean.TRUE;
	            }

	            // Increase decision level and enqueue 'next'
	            assert(value(next) == Lit.LIT_UNDEF);
	            newDecisionLevel();
	            uncheckedEnqueue(next);
	        }
	    }
	}

	// Reduce the set of learnt clauses.
	/*_________________________________________________________________________________________________
	|
	|  reduceDB : ()  ->  [void]
	|  
	|  Description:
	|    Remove half of the learnt clauses, minus the clauses locked by the current assignment. Locked
	|    clauses are clauses that are reason to some assignment. Binary clauses are never removed.
	|________________________________________________________________________________________________@*/
	class reduceDB_lt {
		boolean apply (Clause[]/* * */ x, Clause[] y) { 
			return x.size() > 2 && (y.size() == 2 || x->activity() < y->activity()); 
		}
	}
	protected void reduceDB()
	{
	    int     i, j;
	    double  extra_lim = cla_inc / learnts.size();    // Remove any clause below this activity

	    sort(learnts, reduceDB_lt());
	    for (i = j = 0; i < learnts.size() / 2; i++){
	        if (learnts[i]->size() > 2 && !locked(*learnts[i]))
	            removeClause(*learnts[i]);
	        else
	            learnts[j++] = learnts[i];
	    }
	    for (; i < learnts.size(); i++){
	        if (learnts[i]->size() > 2 && !locked(*learnts[i]) && learnts[i]->activity() < extra_lim)
	            removeClause(*learnts[i]);
	        else
	            learnts[j++] = learnts[i];
	    }
	    learnts.shrink(i - j);
	}
	
	// Shrink 'cs' to contain only non-satisfied clauses.
	protected void removeSatisfied(vec<Clause*>& cs)
	{
	    int i,j;
	    for (i = j = 0; i < cs.size(); i++){
	        if (satisfied(*cs[i]))
	            removeClause(*cs[i]);
	        else
	            cs[j++] = cs[i];
	    }
	    cs.shrink(i - j);
	}

	// Read state:
//	Gives the current decisionlevel.
	protected int      decisionLevel ()        { return trail_lim.size(); }
//	Used to represent an abstraction of sets of decision levels.
	protected int abstractLevel (int/*Var*/ x)    { return 1 << (level.get(x) & 31); }
//	The current value of a variable.
	protected Boolean    value         (int/*Var*/ x)    { return toLbool(assigns.get(x)); }
//	The current value of a literal.
	protected  Boolean    value         (Lit p)    { return toLbool(assigns.get(p.var())) ^ p.sign(); }
//	The value of a literal in the last model. The last call to solve must have been satisfiable.
	protected  Boolean    modelValue    (Lit p)    { return model.get(p.var()) ^ sign(p); }
	// The current number of assigned literals.
	protected  int      nAssigns      ()         { return trail.size(); }
	// The current number of original clauses.
	protected  int      nClauses      ()         { return clauses.size(); }
//	The current number of learnt clauses.
	protected  int      nLearnts      ()         { return learnts.size(); }
//	The current number of variables.
	protected  int      nVars         ()         { return assigns.size(); }

	// Variable mode:
//	Declare which polarity the decision heuristic should use for a variable. Requires mode 'polarity_user'.
	protected  void     setPolarity   (int/*Var*/ v, boolean b) { 
		polarity    [v] = (char)b; 
		}
//	Declare if a variable should be eligible for selection in the decision heuristic.
	protected  void     setDecisionVar(int/*Var*/ v, boolean b) { 
		decision_var[v] = (char)b; if (b) { insertVarOrder(v); } 
		}
//	Search without assumptions.
	protected boolean     solve         ()              { 
		Vec<Lit> tmp; 
		return solve(tmp); 
		}
	// FALSE means solver is in a conflicting state
	protected boolean     okay          ()         { return ok; }



	// Operations on clauses:
	//
//	 Attach a clause to watcher lists.
	protected 	void attachClause(Clause c) {
	    assert(c.size() > 1);
	    watches[toInt(~c[0])].push(c);
	    watches[toInt(~c[1])].push(c);
	    if (c.learnt()) learnts_literals += c.size();
	    else            clauses_literals += c.size(); }

//	 Detach a clause to watcher lists.
	protected void     detachClause     (Clause c) {
	    assert(c.size() > 1);
	    assert(find(watches[toInt(~c[0])], c));
	    assert(find(watches[toInt(~c[1])], c));
	    remove(watches[toInt(~c[0])], c);
	    remove(watches[toInt(~c[1])], c);
	    if (c.learnt()) learnts_literals -= c.size();
	    else            clauses_literals -= c.size(); }
	
//	 Detach and free a clause.
	protected void removeClause(Clause c) {
	    detachClause(c);
	    //free(&c); 
	    }
//	 Returns TRUE if a clause is a reason for some implication in the current state.
	protected boolean     locked          (Clause c) { 
		return reason[var(c[0])] == c && value(c[0]) == Boolean.TRUE; 
	}
	 // Returns TRUE if a clause is satisfied in the current state.
	protected boolean     satisfied        (final Clause c){
		    for (int i = 0; i < c.size(); i++)
		        if (value(c.get(i)) == Boolean.TRUE)
		            return true;
		    return false; }

	}

	// Misc:
	//

	// DELETE THIS ?? IT'S NOT VERY USEFUL ...

	protected double progressEstimate() const
	{
	    double  progress = 0;
	    double  F = 1.0 / nVars();

	    for (int i = 0; i <= decisionLevel(); i++){
	        int beg = i == 0 ? 0 : trail_lim[i - 1];
	        int end = i == decisionLevel() ? trail.size() : trail_lim[i];
	        progress += pow(F, i) * (end - beg);
	    }

	    return progress / nVars();
	}


	// Debug:
	protected void printLit(Lit l)
	{
		reportf("%s%d:%c", l.sign() ? "-" : "", l.var()+1, l.value() == Boolean.TRUE ? '1' : (value(l) == l_False ? '0' : 'X'));
	}


	//template<class C>
	protected void printClause(const C& c)
	{
		for (int i = 0; i < c.size(); i++){
			printLit(c[i]);
			fprintf(stderr, " ");
		}
	}
	
	protected void     checkLiteralCount() {
	    // Check that sizes are calculated correctly:
	    int cnt = 0;
	    for (int i = 0; i < clauses.size(); i++)
	        if (clauses[i]->mark() == 0)
	            cnt += clauses[i]->size();

	    if ((int)clauses_literals != cnt){
	        fprintf(stderr, "literal count: %d, real value = %d\n", (int)clauses_literals, cnt);
	        assert((int)clauses_literals == cnt);
	    }

	// Static helpers:
	//

	// Returns a random float 0 <= x < 1. Seed must never be 0.
	static  double drand(double seed) {
		seed *= 1389796;
		int q = (int)(seed / 2147483647);
		seed -= (double)q * 2147483647;
		return seed / 2147483647; }

	// Returns a random integer 0 <= x < size. Seed must never be 0.
	static  int irand(double seed, int size) {
		return (int)(drand(seed) * size); }



//	=================================================================================================
//	Implementation of inline methods:

//	Maintaining Variable/Clause activity:
	/** Insert a variable in the decision order priority queue.*/
	void insertVarOrder(int/*Var*/ x) {
		if (!order_heap.inHeap(x) && decision_var[x]) order_heap.insert(x); }

	/** 
	 * Decay all variables with the specified factor. Implemented by increasing the 'bump' value instead.
	 */
	void varDecayActivity() { var_inc *= var_decay; }

	/** Increase a variable with the current 'bump' value.*/
	void varBumpActivity(int/*Var*/ v) {
		if ( (activity.set(v, activity.get(v) + var_inc) > 1e100 ) {
			// Rescale:
			for (int i = 0; i < nVars(); i++)
				activity.set(i, activity.get(i)* 1e-100);
			var_inc *= 1e-100; }

		// Update order_heap with respect to new activity:
		if (order_heap.inHeap(v))
			order_heap.decrease(v); }

	/**
	 *    Decay all clauses with the specified factor. Implemented by increasing the 'bump' value instead.
	 *
	 */
	void claDecayActivity() { cla_inc *= clause_decay; }

	/**
	 *  Increase a clause with the current 'bump' value.
	 *
	 */
	void claBumpActivity (Clause& c) {
		if ( (c.activity() += cla_inc) > 1e20 ) {
			// Rescale:
			for (int i = 0; i < learnts.size(); i++)
				learnts[i]->activity() *= 1e-20;
				cla_inc *= 1e-20; } }

	boolean     enqueue         (Lit p, Clause* from)   { return value(p) != Lit.VAR_UNDEF ? value(p) != l_False : (uncheckedEnqueue(p, from), true); }
	

	// Begins a new decision level.
	protected void     newDecisionLevel()                      { trail_lim.push(trail.size()); }



//	=================================================================================================
//	Debug + etc:


//	#define reportf(format, args...) ( fflush(stdout), fprintf(stderr, format, ## args), fflush(stderr) )

	static  void logLit(FILE* f, Lit l)
	{
		fprintf(f, "%sx%d", sign(l) ? "~" : "", var(l)+1);
	}

	static  void logLits(FILE* f, const vec<Lit>& ls)
	{
		fprintf(f, "[ ");
		if (ls.size() > 0){
			logLit(f, ls[0]);
			for (int i = 1; i < ls.size(); i++){
				fprintf(f, ", ");
				logLit(f, ls[i]);
			}
		}
		fprintf(f, "] ");
	}

	static String showBool(boolean b) { return b ? "true" : "false"; }


}

