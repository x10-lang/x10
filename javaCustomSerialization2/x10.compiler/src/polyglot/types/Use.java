package polyglot.types;

public interface Use<T extends TypeObject> extends TypeObject {
    T def();
}
