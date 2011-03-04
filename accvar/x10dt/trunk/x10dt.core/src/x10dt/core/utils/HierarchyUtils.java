package x10dt.core.utils;

import java.util.HashSet;
import java.util.Set;

import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.Type;
import x10.types.MethodInstance;

public class HierarchyUtils {
	public static Set<ClassType> getSuperClasses(ClassType startingClass)
	{
		Set<ClassType> superTypes = new HashSet<ClassType>();
		ClassType superType = (ClassType)startingClass.superClass();
		
		while(superType != null)
		{
			superTypes.add(superType);
			superType = (ClassType)superType.superClass();
		}
		
		return superTypes;
	}
	
	private static void addInterfaces(ClassType startingClass, Set<ClassType> interfaces)
	{
		for(Type type : startingClass.interfaces())
		{
			interfaces.add((ClassType)type);
			addInterfaces((ClassType)type, interfaces);
		}
	}
	
	public static Set<ClassType> getInterfaces(Set<ClassType> classes)
	{
		Set<ClassType> interfaces = new HashSet<ClassType>();
		for(ClassType ct : classes)
		{
			addInterfaces(ct, interfaces);
		}
		
		return interfaces;
	}
	
	public static Set<MethodInstance> getMethods(Set<ClassType> classes)
	{
		return getMethods(classes, Flags.NONE);
	}
	
	public static Set<MethodInstance> getMethods(Set<ClassType> classes, Flags flags)
	{
		Set<MethodInstance> methods = new HashSet<MethodInstance>();
		for(ClassType ct : classes)
		{
			for(MethodInstance mi : ct.methods())
			{
				if(mi.flags().contains(flags))
				{
					methods.add(mi);
				}
			}
		}
		
		return methods;
	}
	
	public static Set<MethodInstance> getImplementedMethods(Set<ClassType> classes)
	{
		Set<MethodInstance> methods = new HashSet<MethodInstance>();
		for(ClassType ct : classes)
		{
			for(MethodInstance mi : ct.methods())
			{
				if(!mi.flags().isAbstract())
				{
					methods.add(mi);
				}
			}
		}
		
		return methods;
	}
}
