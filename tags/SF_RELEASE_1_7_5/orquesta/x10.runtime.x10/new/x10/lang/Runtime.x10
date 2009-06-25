package x10.lang;

public class Runtime {
    public incomplete static def sleep(millis: long): boolean;
    public incomplete static def exit(code: int): void;
    public incomplete static def setExitCode(code: int): void;
    
    public static def location(o: Object): Place = {
        if (o instanceof Ref) {
            return (o as Ref).location;
        }
        return x10.lang.Runtime.here();
    }
    
    public incomplete static def here(): Place;
    
    public static def main(args: Rail[String]): void = {
    }
}
