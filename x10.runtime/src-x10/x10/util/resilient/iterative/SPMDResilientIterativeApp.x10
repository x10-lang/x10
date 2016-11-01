/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 *  (C) Copyright Sara Salem Hamouda 2014-2016.
 */

package x10.util.resilient.iterative;

import x10.util.HashMap;
import x10.util.Team;
import x10.util.resilient.localstore.Cloneable;
import x10.util.resilient.PlaceManager.ChangeDescription;

public interface SPMDResilientIterativeApp {
    public def isFinished_local():Boolean;
    
    public def step_local():void;
    
    public def getCheckpointData_local():HashMap[String,Cloneable];
    
    public def remake(changes:ChangeDescription, newTeam:Team):void;
    
    public def restore_local(restoreDataMap:HashMap[String,Cloneable], lastCheckpointIter:Long):void;   
}
