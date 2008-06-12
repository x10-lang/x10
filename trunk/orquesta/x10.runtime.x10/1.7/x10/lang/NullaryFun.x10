package x10.lang;

/**

  Should we make it so that an instance of a class implementing
  NullaryFun[R] can be coerced into type ()=>R? 

  @author vj 06/11/08
 */
public interface NullaryFun[R] {
    def apply():R;
}
