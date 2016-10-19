package x10.util.resilient.localstore;

import x10.util.HashMap;
public class SimplePlaceHammer {
    val map:HashMap[Long,Long] = new HashMap[Long,Long]();
    public def this(steps:String, places:String) {
        if (steps != null && places != null) {
            val sRail = steps.split(",");
            val pRail = places.split(",");
        
            if (sRail.size == pRail.size) {
                for (var i:Long = 0; i < sRail.size ; i++) {
                    val step = Long.parseLong(sRail(i));
                    val place = Long.parseLong(pRail(i));
                    map.put(step, place);
                    Console.OUT.println("Hammer  step="+step+" place="+place);
                }
            }
        }
    }
    
    public def sayGoodBye(curStep:Long):Boolean {
        val placeToKill = map.getOrElse(curStep,-1);
        if (placeToKill == here.id) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public def getVictimPlaceId(curStep:Long):Long {
    	return map.getOrElse(curStep,-1);
    }
}