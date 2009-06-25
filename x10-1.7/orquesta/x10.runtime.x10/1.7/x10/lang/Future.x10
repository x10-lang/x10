public x10.lang;

public abstract value class Future[FutureT] implements () => FutureT {
    public abstract def start(): void;
    public abstract def started(): boolean;
    public abstract def force(): FutureT;
    public abstract def forced(): boolean;
    public def apply() = force();
}
