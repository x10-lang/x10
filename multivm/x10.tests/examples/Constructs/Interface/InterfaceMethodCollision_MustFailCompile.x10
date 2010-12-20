
import x10.compiler.*; // @Uncounted @NonEscaping @NoThisAccess
import x10.compiler.tests.*; // err markers

interface IDoom {
  def doom():void;
}

interface IBloom {
  def doom():Int;
}

@ERR interface IGloom extends IDoom, IBloom {} 
