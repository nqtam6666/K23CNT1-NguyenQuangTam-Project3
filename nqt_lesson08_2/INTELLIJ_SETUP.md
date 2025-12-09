# Hướng dẫn cấu hình IntelliJ IDEA để fix lỗi FileCountLimitExceededException

## Cách 1: Thêm VM Options trong Run Configuration (Khuyến nghị)

1. **Mở Run Configuration:**
   - Click vào dropdown "Run" ở thanh toolbar (hoặc nhấn `Alt + Shift + F10`)
   - Chọn **"Edit Configurations..."**

2. **Thêm VM Options:**
   - Trong cửa sổ "Run/Debug Configurations"
   - Tìm và chọn configuration của bạn (thường là "Application")
   - Tìm phần **"VM options"** (nếu không thấy, click vào **"Modify options"** → chọn **"Add VM options"**)
   - Thêm dòng sau vào VM options:
     ```
     -Dorg.apache.tomcat.util.http.fileupload.FileCountLimit=10000 -Dorg.apache.tomcat.util.http.fileupload.FileSizeThreshold=0
     ```

3. **Apply và chạy:**
   - Click **"Apply"** → **"OK"**
   - Restart ứng dụng

## Cách 2: Sử dụng Environment Variables

1. Mở Run Configuration như trên
2. Tìm phần **"Environment variables"**
3. Thêm biến:
   - Name: `MAVEN_OPTS`
   - Value: `-Dorg.apache.tomcat.util.http.fileupload.FileCountLimit=10000 -Dorg.apache.tomcat.util.http.fileupload.FileSizeThreshold=0`

## Cách 3: Sử dụng script đã tạo

Chạy file `run-maven.bat` hoặc `run-maven.ps1` từ terminal.

## Lưu ý

- Sau khi thêm VM options, **phải restart ứng dụng** mới có hiệu lực
- Giới hạn 10000 parts đủ cho form có nhiều tác giả
- Nếu vẫn lỗi, có thể tăng giá trị lên 20000 hoặc cao hơn

