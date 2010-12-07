interface IDoom {
  def doom():void;
}

interface IBloom {
  def doom():Int;
}

interface IGloom extends IDoom, IBloom {} 
