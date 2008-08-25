package x10.lang;

public abstract value class Future[+T] implements ()=>T {

    public abstract def start(): void;
    public abstract def started(): boolean;
    public abstract def force(): T;
    public abstract def forced(): boolean;
    public def apply() = force();
}
