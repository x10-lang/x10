package futuresched.core;



public interface Notifier {
   public def addIfNotSet(task: FTask, obj: Any): Boolean;
}
