package com.Ngay02.nqt_Ngay02.dependency_injection;

// Interface (Dependency)
public interface Shape {
    void draw();
}

// Concrete Class 1
class CircleShape implements Shape {
    @Override
    public void draw() {
        System.out.println("CircleShape draw");
    }
}

// Concrete Class 2
class RectangleShape implements Shape {
    @Override
    public void draw() {
        System.out.println("RectangleShape draw");
    }
}