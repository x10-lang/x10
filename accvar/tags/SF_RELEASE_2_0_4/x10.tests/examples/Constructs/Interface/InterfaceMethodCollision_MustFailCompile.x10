interface IDoom {
  def doom():Void;
}

interface IBloom {
  def doom():Int;
}

interface IGloom extends IDoom, IBloom {} 
