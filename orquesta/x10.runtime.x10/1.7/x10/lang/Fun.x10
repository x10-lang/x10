package x10.lang;

/**

  Should we make it so that an instance of a class implementing
  Fun[D,R] can be coerced into type D=>R? 

  Why is Fun[D,R] needed? Why dont we simply say that D=>R is in fact
  a "builtin" interface? So we can have classes implemented D=>R etc.?

  Should we make the types (D1, D2) => R be identical to (D1) => (D2)
  => R?

  Note: We may have the same class implementing multiple fun
  interfaces, e.g. Fun[D1,R1], Fun[D2,R2] etc. So then we would have
  to use the usual casting mechanism to choose which fun type this
  object is intended to be at.

  Note: prolly want to allow subtyping of functions so that an D=>R
  can always be passed in for a D' => R' provided that D' is a
  subtype of D and R is a subtype of R'.

  @author vj 06/11/08
 */
public interface fun[D,R] {
    def apply(x:D):R;
}

