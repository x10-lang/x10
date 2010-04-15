#include <new>

#include <minisat_solver.h>
#include <minisat_util.h>

class PolySat__Solver : public x10::lang::Object, public SolverCallback {

    Solver solver;
    int solveStatus;
    x10aux::ref<x10::lang::VoidFun_0_1<x10aux::ref<x10::lang::ValRail<int> > > > closure;

    public:

    PolySat__Solver (void)
      : solver(this), solveStatus(0)
    {
        solver.verbosity = 1;
    }

    static x10aux::ref<PolySat__Solver> _make(x10aux::ref<x10::lang::String> s) {
        x10aux::ref<PolySat__Solver> self = new (x10aux::alloc<PolySat__Solver>()) PolySat__Solver();
   
        const char *filename = s->c_str();
        gzFile in = gzopen(filename, "rb");
        if (in == NULL)
            reportf("ERROR! Could not open file: %s\n", filename);
        parse_DIMACS(in, self->solver);
        gzclose(in);

        return self;
    }

    void handleNewClause (const vec<Lit> &clause) {
        x10aux::ref<Reference> cl = closure;
        x10aux::ref<x10::lang::ValRail<x10_int> > clause_ = x10::lang::ValRail<x10_int>::make(clause.size());
        for (x10_int i=0 ; i<clause.size() ; ++i) {
            (*clause_)[i] = toInt(clause[i]);
        }
        x10::lang::VoidFun_0_1<x10aux::ref<x10::lang::ValRail<x10_int> > >::apply(cl,clause_);
    }

    void step (void) {
        x10::lang::Runtime::probe();
    }

    x10_boolean solve (x10aux::ref<x10::lang::VoidFun_0_1<x10aux::ref<x10::lang::ValRail<int> > > > closure_) {
        closure = closure_;
        if (!solver.simplify()){
            solveStatus = -1;
            return true;
        }
        solveStatus = solver.solve() ? 1 : -1;
        return !solver.wasKilled();
    }

    void addClause (x10aux::ref<x10::lang::ValRail<x10_int> > clause_) {
        vec<Lit> clause;
        for (x10_int i=0 ; i<clause_->length() ; ++i) {
            clause.push(toLit((*clause_)[i]));
        }
        solver.learnClause(clause);
    }

    void kill (void) {
        solver.kill();
    }

    void dump (void) {

        if (solveStatus) printStats(solver);

        switch (solveStatus) {
            case -1:
            printf("UNSATISFIABLE\n");
            break;

            case 0:
            printf("UNKNOWN\n");
            break;

            case 1: {
                printf("SATISFIABLE\n");
                for (int i = 0; i < solver.nVars(); i++)
                    if (solver.model[i] != l_Undef)
                        printf("%s%s%d", (i==0)?"":" ", (solver.model[i]==l_True)?"":"-", i+1);
                printf(" 0\n");
            }
            break;

            default: abort();
        }
    }
    
};
