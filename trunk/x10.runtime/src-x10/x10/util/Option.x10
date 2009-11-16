package x10.util;

// This class exists as a work-around for XTENLANG-624
public struct Option {
    short_:String; // underscore is workaround for XTENLANG-623
    long_:String;
    description:String;
    public def this(s:String, l:String, d:String) {
        short_ = "-"+s; long_="--"+l; description=d;
    }
}
