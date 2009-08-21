package x10.constraint;

public interface ThisVar {

	/**
     * We distinguish the variable "this" since the compiler needs to perform
     * special actions for this. So each constraint keeps track of its this
     * variable. This is typically set using setThisVar when a substitution
     * is being applied. 
     * @return
     */
    XVar thisVar();
	
}
