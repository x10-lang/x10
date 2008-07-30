package x10.array;

import x10.lang.*;

public class ConstantDist extends BaseDist {

    ConstantDist(Region r, place p) {
        super(r, false, true, p);
        places = new place [] {p};
        regions = new Region [] {r};
        initRegionMap();
    }
}    
