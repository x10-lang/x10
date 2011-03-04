package global.lb;
/**
   A Runner knows how to execute a task of type T within
   a collecting finish of type Z. It returns the result
   of the finish.
*/
public interface  Runner[T,Z] extends (T,Reducible[Z])=> Z {

    /**  Print statistics about execution.
     */
     def stats(time:Long, verbose:Boolean):void;
}