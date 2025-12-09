# Hướng dẫn cấu hình Application Properties

## Cấu trúc file

- **`application.properties`**: File cấu hình chung (được commit lên git)
- **`application.properties.example`**: File template mẫu (được commit lên git)
- **`application-local.properties`**: File chứa thông tin bảo mật (KHÔNG được commit lên git)

## Thiết lập ban đầu

1. Copy file `application.properties.example` thành `application-local.properties`:
   ```bash
   cp application.properties.example application-local.properties
   ```

2. Mở file `application-local.properties` và điền các thông tin thực tế:
   - `spring.datasource.username`: Tên người dùng database
   - `spring.datasource.password`: Mật khẩu database
   - `jwt.secret`: Secret key cho JWT (khuyến nghị tạo chuỗi ngẫu nhiên 64+ ký tự)
   - `spring.security.oauth2.client.registration.google.client-id`: Google OAuth2 Client ID
   - `spring.security.oauth2.client.registration.google.client-secret`: Google OAuth2 Client Secret

## Thông tin bảo mật được lưu ở đâu?

Tất cả thông tin nhạy cảm được lưu trong file **`application-local.properties`**, file này:
- ✅ **KHÔNG được commit lên git** (đã được thêm vào `.gitignore`)
- ✅ Chỉ tồn tại trên máy local của bạn
- ✅ Được Spring Boot tự động load khi chạy ứng dụng (profile `local`)

## Spring Profiles

Ứng dụng sử dụng Spring Profile `local` để load các cấu hình từ `application-local.properties`.

File `application.properties` có cấu hình:
```properties
spring.profiles.active=local
```

Điều này đảm bảo Spring Boot sẽ tự động load và merge các properties từ `application-local.properties`.

## Bảo mật

- ❌ **KHÔNG BAO GIỜ** commit file `application-local.properties` lên git
- ✅ Luôn sử dụng `application.properties.example` làm template
- ✅ Mỗi developer cần tạo file `application-local.properties` riêng cho mình
- ✅ Trên môi trường production, sử dụng environment variables hoặc secret management tools

