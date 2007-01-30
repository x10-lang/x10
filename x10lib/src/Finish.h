/* copyright (c) 2006 IBM
 * Pre-processor directive to support x10 finish.
 *
 * #include "Finish.h" <BR>
 *
 * Pre-processor directive that implements the finish functionality in
 * x10. It takes the same argumemts as Activity::spawn(), viz., an
 * instance of a sub-class of x10lib::ActivityMaker and the Place at
 * which the activity is to be spawned.
 * This is a pre-processor directive so that we can implementing the
 * blocking functionality of finish in a non-blocking fashion. It
 * translates the code into a switch statement to jump into and out
 * of it. FINISH can only be invoked from within the Activity::run()
 * method. Other directives defined here provide support for the
 * FINISH directive. Whenever FINISH is used that method should have
 * an ACTIVITY_START at the start of the method, and ACTIVITY_END at
 * the end of the method. Calling FINISH basically sets a label and
 * returns. When the method is again invoked the execution continues
 * from that label. 
 * 
 * FINISH itself may not be invoked from within a switch
 * statement. Local variables in a method using FINISH, are not
 * guaranteed to be valid after a call to FINISH. Thus, any method
 * using this functionality cannot have any local variables. All of
 * these must be translated into class member variables. 
 *
 * Another restriction is that the FINISH (and the accompanying
 * directives) can occur only at the "top-level" in the run()
 * method. In other words, they should be lexically enclosed in the
 * run() method. Invoking FINISH is a method called by run() would
 * result in undefined behaviour. 
 *
 * The implementation is based on the idea of protothreads.
 *
 * @see http://www.sics.se/~adam/pt/ 
 * @see A. Dunkels, O. Schmidt, T. Voigt, and M. Ali, Protothreads:
 * Simplifying Event-Driven Programming of Memory-Constrained Embedded
 * Systems, Proc. ACM SenSys, Boulder, CO, USA, Nov 2006.
 * @author Sriram Krishnamoorthy
 */
#ifndef x10lib_Finish_h
#define x10lib_Finish_h

#include "FinishScope.h"
#include "FinishScopeManager.h"
#include "DsFinishRecord.h"
#include "FinishRecordManager.h"
#include "SltType.h"

#if defined(FINISH)
#  error "Symbol conflict! FINISH already defined. Please change it."
#endif

/**This directive should be placed at the start of the run() method
 *  (which is assumed to have no local variables) that uses FINISH. By
 *  construction, it has access to public/protected methods in the
 *  Activity class. 
 */
#define ACTIVITY_START switch(Slt().Label()) {                     \
case 0: ;

/**This directive should be the last statement in the method
 * containing ACTIVITY_START. Note that it checks for an invalid
 * label by adding a default label. 
 */
#define ACTIVITY_END break;                                        \
default: assert(0);  }

/**
 */
#define FINISH(maker,place)                                        \
Slt().Scope(x10lib::FinishScopeManager::                           \
	      rInstance().CreateFinishScope());                    \
Slt().Record(x10lib::FinishRecordManager::                         \
              rInstance().Insert(Slt().Scope(),                    \
                new x10lib::DsFinishRecord(Slt().Scope(),          \
                                           x10lib::Here())));      \
(maker).Scope(Slt().Scope());                                      \
spawn((maker),(place));                                            \
Slt().Label(__LINE__); case __LINE__:                              \
if(!Slt().pRecord()->HasTerminated())                              \
   return x10lib::ACTIVITY_FINISH_WAITING;                         \
delete Slt().pRecord();


#endif

