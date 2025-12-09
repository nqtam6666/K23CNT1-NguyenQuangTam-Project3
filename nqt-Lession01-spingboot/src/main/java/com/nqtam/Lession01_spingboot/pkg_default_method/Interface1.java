package com.nqtam.Lession01_spingboot.pkg_default_method;

public interface Interface1 {
    default void method1() {
        System.out.println("Interface1.method1()");
    }
}
