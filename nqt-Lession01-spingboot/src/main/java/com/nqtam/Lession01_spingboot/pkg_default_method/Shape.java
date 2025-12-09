package com.nqtam.Lession01_spingboot.pkg_default_method;

public interface Shape {
    void draw();
    default String setColor(String color){
        return "Đã set màu " + color;
    }
}
