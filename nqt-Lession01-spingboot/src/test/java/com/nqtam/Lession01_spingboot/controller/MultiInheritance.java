package com.nqtam.Lession01_spingboot.controller;

import com.nqtam.Lession01_spingboot.pkg_default_method.Interface1;
import com.nqtam.Lession01_spingboot.pkg_default_method.Interface2;
import com.nqtam.Lession01_spingboot.pkg_default_method.Shape;

import java.util.ArrayList;
import java.util.List;

    public class MultiInheritance  implements Interface1, Interface2, Shape {

    List<String> messages = new ArrayList<>();

    @Override
    public void draw() {
        messages.add("Vẽ hình trong MultiInheritance");
    }

    @Override
    public void method1() {
        messages.add("Interface1.method1()");
        messages.add("Gọi thêm trong MultiInheritance.method1()");
    }

    @Override
    public void method2() {
        messages.add("Interface2.method2()");
    }

    public List<String> runAll() {
        draw();
        messages.add(setColor("Đỏ"));
        method1();
        method2();

        return messages;
    }
}
