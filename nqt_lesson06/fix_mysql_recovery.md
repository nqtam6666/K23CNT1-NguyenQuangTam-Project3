# Hướng dẫn sửa lỗi MySQL Recovery Mode

## Lỗi: "Operation not allowed when innodb_force_recovery > 0"

Lỗi này xảy ra khi MySQL đang ở chế độ recovery, chỉ cho phép SELECT, không cho phép INSERT/UPDATE/DELETE.

## Cách sửa:

### Bước 1: Kiểm tra trạng thái recovery
Mở MySQL và chạy:
```sql
SHOW VARIABLES LIKE 'innodb_force_recovery';
```

### Bước 2: Tắt chế độ recovery
1. Mở file cấu hình MySQL (thường là `my.ini` hoặc `my.cnf`)
   - Windows: `C:\ProgramData\MySQL\MySQL Server 8.0\my.ini`
   - Hoặc tìm trong MySQL installation directory

2. Tìm dòng có `innodb_force_recovery` và:
   - Xóa dòng đó, HOẶC
   - Đổi thành `innodb_force_recovery = 0`

3. Khởi động lại MySQL service:
   - Windows: Services → MySQL → Restart
   - Hoặc dùng Command Prompt (Admin):
     ```
     net stop MySQL80
     net start MySQL80
     ```

### Bước 3: Nếu vẫn không được, thử cách này:
1. Dừng MySQL service
2. Xóa file `ib_logfile0` và `ib_logfile1` trong thư mục data của MySQL
3. Khởi động lại MySQL

### Lưu ý:
- Nếu MySQL tự động vào recovery mode, có thể do database bị corrupt
- Nên backup dữ liệu trước khi thực hiện các thao tác trên

