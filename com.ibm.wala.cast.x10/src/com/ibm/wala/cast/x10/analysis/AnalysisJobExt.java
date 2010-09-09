/*
 * Created on Feb 23, 2006
 */
package com.ibm.wala.cast.x10.analysis;

import java.util.HashMap;

import polyglot.frontend.JobExt;

public class AnalysisJobExt extends HashMap implements JobExt {
    public static final String CAST_JOBEXT_KEY= "CAst";
    /**
     * 
     */
    private static final long serialVersionUID= -8188399933862023205L;

    public AnalysisJobExt() {
	super();
    }
}
