package com.Ngay02.nqt_Ngay02.dependency_injection;
// Không cần import com.Ngay02.nqt_Ngay02.dependency_injection.Shape; vì chúng cùng package

public class DrawShape {
    private Shape shape; // Phụ thuộc vào Interface

    // Constructor Injection: Tiêm đối tượng Shape vào đây
    public DrawShape(Shape shape) {
        this.shape = shape;
    }

    public void Draw() {
        shape.draw();
    }

    public static void main(String[] args) {

        // --- Dependency Injection Thủ công (Manual DI) ---

        // 1. Inject CircleShape
        // Lớp bên ngoài (hàm main) chịu trách nhiệm tạo đối tượng
        DrawShape drawShape1 = new DrawShape(new CircleShape());
        drawShape1.Draw(); // Output: CircleShape draw

        // 2. Inject RectangleShape
        DrawShape drawShape2 = new DrawShape(new RectangleShape());
        drawShape2.Draw(); // Output: RectangleShape draw

        // *** Nếu bạn dùng Spring Framework ***
        // Spring sẽ đảm nhận việc tạo và inject đối tượng thay cho hàm main.
    }
}