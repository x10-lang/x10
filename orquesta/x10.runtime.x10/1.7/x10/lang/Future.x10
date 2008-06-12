public x10.lang;

public value class Future[T] {
    public abstract start():void;
    public abstract started():boolean;
    public abstract force():T;
    public abstract forced(): boolean;
}
