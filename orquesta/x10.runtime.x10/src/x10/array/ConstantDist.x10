package x10.array;

import x10.lang.*;

import java.util.Map;

public class ConstantDist extends BaseDist {

    ConstantDist(Region r, place p) {
        places = new place [] {p};
        regions = new Region [] {r};
        region = r;
        initRegionMap();
    }
}    
