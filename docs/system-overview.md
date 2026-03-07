# 01. System Overview — Airline Booking System

## Mục lục

1. [Mô tả dự án](#1-mô-tả-dự-án)
2. [Mục tiêu & Phạm vi hệ thống](#2-mục-tiêu--phạm-vi-hệ-thống)
3. [Actors & Roles](#3-actors--roles)
4. [High-level Use Cases](#4-high-level-use-cases)
5. [Tech Stack](#5-tech-stack)
6. [System Constraints & Assumptions](#6-system-constraints--assumptions)

---

## 1. Mô tả dự án

**Airline Booking System** là một hệ thống đặt vé máy bay trực tuyến được xây dựng dưới dạng **RESTful API Backend**. Hệ thống cho phép hành khách tìm kiếm chuyến bay, đặt vé, thanh toán và quản lý hành trình của mình. Phía quản trị viên có thể quản lý toàn bộ dữ liệu vận hành bao gồm chuyến bay, máy bay, sân bay và các thông tin liên quan.

Hệ thống được thiết kế theo kiến trúc **Layered Architecture** (Controller → Service → Repository → Database), tuân thủ các nguyên tắc RESTful và được bảo mật bằng **JWT Authentication** kết hợp **Role-Based Access Control (RBAC)**. Hệ thống sử dụng **Redis** như một in-memory store để quản lý vòng đời token (Refresh Token, Access Token Blacklist) và lưu OTP cho các luồng xác minh danh tính.

---

## 2. Mục tiêu & Phạm vi hệ thống

### 2.1 Mục tiêu

| # | Mục tiêu |
|---|----------|
| 1 | Cung cấp API cho phép tìm kiếm và đặt vé máy bay trực tuyến |
| 2 | Hỗ trợ thanh toán trực tuyến qua cổng VNPay (sandbox) |
| 3 | Xác thực và phân quyền người dùng bằng JWT + Refresh Token; quản lý token an toàn qua **Redis** |
| 4 | Quản lý toàn bộ vòng đời của booking (tạo → thanh toán → xác nhận → hủy) |
| 5 | Ghi nhận audit log cho các hoạt động quan trọng trong hệ thống |
| 6 | Cho phép hành khách đánh giá chuyến bay sau khi hoàn thành |
| 7 | Gửi thông báo tới người dùng về trạng thái đặt vé và chuyến bay |
| 8 | Hỗ trợ luồng quên mật khẩu / xác minh tài khoản qua OTP (lưu tạm trong Redis) |

### 2.2 Phạm vi hệ thống (Scope)

**In-scope (Trong phạm vi):**
- Đăng ký, đăng nhập, quản lý tài khoản người dùng
- Tìm kiếm chuyến bay theo điều kiện (điểm đi, điểm đến, ngày bay, hạng ghế, ...)
- Đặt vé, chọn ghế, chọn dịch vụ bổ sung (add-ons)
- Thanh toán qua VNPay sandbox
- Quản lý thông tin hành khách (passenger profiles)
- Quản lý chuyến bay, máy bay, sân bay (Admin)
- Thông báo hệ thống (notifications)
- Ghi audit log
- Đánh giá và nhận xét chuyến bay (reviews)

**Out-of-scope (Ngoài phạm vi):**
- Giao diện người dùng (Frontend / Mobile App)
- Tích hợp thanh toán thực tế (chỉ dùng VNPay sandbox)
- Hệ thống check-in tại sân bay
- Kết nối với GDS (Global Distribution System) bên ngoài
- Tích hợp email/SMS thực tế (notification chỉ lưu DB)

---

## 3. Actors & Roles

Hệ thống có **4 actors** chính:

| Actor | Mô tả | Yêu cầu xác thực |
|-------|-------|:-----------------:|
| **Guest** | Người dùng chưa đăng nhập, chỉ có thể xem thông tin công khai | Không |
| **Passenger** | Hành khách đã đăng ký tài khoản, có thể đặt vé và quản lý booking | Có |
| **Admin** | Quản trị viên hệ thống, có toàn quyền quản lý dữ liệu vận hành | Có |
| **System** | Các tiến trình tự động chạy nội bộ (scheduler, event handler, ...) | Internal |

### Chi tiết quyền theo role:

```
ROLE_GUEST       → Chỉ đọc thông tin công khai (flights, airports)
ROLE_PASSENGER   → Tất cả quyền của Guest + đặt vé, thanh toán, quản lý booking cá nhân
ROLE_ADMIN       → Toàn quyền hệ thống (CRUD tất cả tài nguyên, xem audit logs)
ROLE_SYSTEM      → Tự động xử lý: cập nhật trạng thái booking, gửi notification
```

---

## 4. High-level Use Cases

### 4.1 Guest

| Use Case | Mô tả |
|----------|-------|
| Xem thông tin sân bay | Tra cứu danh sách sân bay |
| Tìm kiếm chuyến bay | Tìm chuyến bay theo điều kiện |
| Xem thông tin chuyến bay | Xem chi tiết chuyến bay, giá vé, ghế trống |
| Đăng ký tài khoản | Tạo tài khoản Passenger mới |
| Đăng nhập | Lấy Access Token & Refresh Token |

### 4.2 Passenger

| Use Case | Mô tả |
|----------|-------|
| Quản lý hồ sơ cá nhân | Cập nhật thông tin cá nhân, đổi mật khẩu |
| Đặt vé máy bay | Tạo booking (1 chiều / khứ hồi), chọn ghế |
| Thêm dịch vụ bổ sung | Chọn add-ons (hành lý, suất ăn, ...) |
| Thanh toán booking | Thanh toán qua VNPay, xem lịch sử thanh toán |
| Quản lý booking | Xem, hủy booking của bản thân |
| Quản lý hành khách | Thêm/sửa thông tin hành khách đi kèm |
| Đánh giá chuyến bay | Viết review sau khi chuyến bay hoàn thành |
| Xem thông báo | Xem notifications liên quan đến booking |
| Làm mới Token | Dùng Refresh Token (lưu trong Redis) để lấy Access Token mới |
| Đăng xuất | Vô hiệu hóa token hiện tại (Access Token bị blacklist trong Redis) |
| Quên mật khẩu | Yêu cầu OTP, xác minh OTP (lưu tạm trong Redis với TTL 5 phút), đặt lại mật khẩu |

### 4.3 Admin

| Use Case | Mô tả |
|----------|-------|
| Quản lý tài khoản | CRUD tài khoản người dùng |
| Quản lý sân bay | CRUD thông tin sân bay |
| Quản lý máy bay | CRUD máy bay, cấu hình ghế ngồi |
| Quản lý chuyến bay | CRUD chuyến bay, lịch trình |
| Quản lý chậm/hủy chuyến | Ghi nhận thông tin delay/cancellation |
| Quản lý booking | Xem và xử lý tất cả bookings trong hệ thống |
| Quản lý dịch vụ | CRUD add-ons |
| Xem audit logs | Theo dõi lịch sử hoạt động hệ thống |
| Xem báo cáo | Thống kê doanh thu, booking, ... |

### 4.4 System (Tự động)

| Use Case | Mô tả |
|----------|-------|
| Cập nhật trạng thái booking | Tự động hủy booking chưa thanh toán sau thời gian quy định |
| Gửi thông báo | Tạo notification khi có sự kiện (booking confirmed, flight delayed, ...) |
| Ghi audit log | Tự động ghi lại các hành động quan trọng |

---

## 5. Tech Stack

### 5.1 Backend

| Thành phần | Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|-----------|----------|
| Ngôn ngữ | Java | 21 (LTS) | — |
| Framework | Spring Boot | 4.0.3 | — |
| Web Layer | Spring MVC (REST) | — | Xử lý HTTP request/response |
| ORM | Spring Data JPA / Hibernate | — | Tương tác với MySQL |
| Security | Spring Security + JWT | — | Authentication & Authorization |
| Build Tool | Maven (Maven Wrapper) | — | — |
| Database | MySQL | 8.x | Lưu trữ dữ liệu chính |
| Cache / In-memory Store | **Redis** | 7.x | Refresh Token, Access Token Blacklist, OTP Cache |
| Payment Gateway | VNPay | Sandbox | Xử lý thanh toán |

### 5.2 Cấu trúc Package

```
com.airlinebooking/
├── controllers/     # REST API Controllers — xử lý HTTP request/response
├── services/        # Business Logic Layer (Interface + Impl)
├── repositories/    # Data Access Layer (Spring Data JPA)
├── models/          # Entity classes — mapping với DB tables
├── dtos/            # Data Transfer Objects (Request & Response)
├── exceptions/      # Custom Exception & Global Error Handler
├── security/        # JWT, Spring Security Config, Auth Filter
└── config/          # CORS, Bean configurations, ...
```

### 5.3 Database — MySQL (17 bảng)

```
accounts          users             airports
aircraft          seats             flights
flight_seats      flight_delays     bookings
booking_flights   passengers        booking_seats
addons            payments          notifications
audit_logs        reviews
```

### 5.4 Redis — In-memory Store (3 key patterns)

| Key Pattern | Giá trị | TTL | Mục đích |
|---|---|---|---|
| `refresh_token:{userId}` | token string | 7 ngày | Lưu Refresh Token, thay thế DB table |
| `blacklist:token:{jti}` | `"revoked"` | Còn lại của Access Token | Vô hiệu hóa Access Token khi logout |
| `otp:{email}` | 6-digit code | 5 phút | OTP cho quên mật khẩu / xác minh tài khoản |

---

## 6. System Constraints & Assumptions

### 6.1 Constraints (Ràng buộc)

| # | Ràng buộc |
|---|-----------|
| 1 | API chỉ trả về định dạng **JSON** |
| 2 | Mọi endpoint (trừ public) đều yêu cầu **JWT Access Token** hợp lệ trong Header |
| 3 | Access Token có thời hạn ngắn; hết hạn phải dùng **Refresh Token** (lưu trong Redis) để cấp mới |
| 4 | Thanh toán chỉ hỗ trợ **VNPay sandbox** — không xử lý tiền thật |
| 5 | Hệ thống không tự cung cấp giao diện người dùng (chỉ là Backend API) |
| 6 | Mỗi ghế trên chuyến bay chỉ có thể được đặt bởi **một booking tại một thời điểm** |
| 7 | Booking chưa thanh toán sẽ tự động bị **hủy sau thời gian quy định** |
| 8 | Sau khi logout, Access Token bị **vô hiệu hóa ngay lập tức** thông qua Redis blacklist |
| 9 | OTP xác minh chỉ có hiệu lực trong **5 phút** và tự động xóa khỏi Redis sau đó |

### 6.2 Assumptions (Giả định)

| # | Giả định |
|---|----------|
| 1 | Dữ liệu chuyến bay (lịch trình, giá vé) được Admin nhập thủ công vào hệ thống |
| 2 | Hệ thống không tích hợp GDS hoặc nguồn dữ liệu chuyến bay bên ngoài |
| 3 | Tất cả giá vé tính theo đơn vị **VNĐ (Vietnamese Dong)** |
| 4 | Một booking có thể bao gồm nhiều hành khách và nhiều chặng bay |
| 5 | Notification lưu vào database, không gửi email/SMS thực tế |
| 6 | Môi trường phát triển sử dụng MySQL local hoặc Docker |
