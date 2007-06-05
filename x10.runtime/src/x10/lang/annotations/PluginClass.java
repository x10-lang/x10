package x10.lang.annotations;

public interface PluginClass extends ClassAnnotation {
    String className();
    String propertyNames$ = "className ";
}
