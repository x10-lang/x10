package x10.util;

public interface Builder[Element,+Collection] {
    public def add(Element): Builder[Element,Collection];
    public def result(): Collection;
}
