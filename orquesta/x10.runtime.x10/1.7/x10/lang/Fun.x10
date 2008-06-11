package x10.lang;

/**

  Should we make it so that an instance of a class implementing
  Fun[D,R] can be coerced into type D=>R? 

  Note: We may have the same class implementing multiple fun
  interfaces, e.g. Fun[D1,R1], Fun[D2,R2] etc. So then we would have
  to use the usual casting mechanism to choose which fun type this
  object is intended to be at.

  @author vj 06/11/08
 */
public interface Fun[D,R] {
    def apply(x:D):R;
}
