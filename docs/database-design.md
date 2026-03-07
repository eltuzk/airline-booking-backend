# 02. Database Design — Airline Booking System

## Mục lục

1. [Mô tả từng bảng](#1-mô-tả-từng-bảng)
2. [Relationships](#2-relationships)
3. [Redis Key Design](#3-redis-key-design)

---

## 1. Mô tả từng bảng

> **Quy ước:**
> - `PK` = Primary Key | `FK` = Foreign Key | `UQ` = Unique | `NN` = Not Null
> - Tất cả bảng đều có `id BIGINT PK AUTO_INCREMENT`
> - `created_at` và `updated_at` mặc định là `DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`

---

### 1.1 `accounts` — Tài khoản hệ thống

> Lưu thông tin xác thực (authentication). Tách biệt với `users` để theo nguyên tắc _Separation of Concerns_.

| Column | Type | Constraints | Mô tả |
|--------|------|-------------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | ID tài khoản |
| `email` | VARCHAR(100) | UQ, NN | Email đăng nhập |
| `password_hash` | VARCHAR(255) | NN | Mật khẩu đã hash (BCrypt) |
| `role` | ENUM | NN, DEFAULT 'PASSENGER' | `PASSENGER` \| `ADMIN` |
| `status` | ENUM | NN, DEFAULT 'ACTIVE' | `ACTIVE` \| `INACTIVE` \| `BANNED` |
| `created_at` | DATETIME | NN | Ngày tạo tài khoản |
| `updated_at` | DATETIME | NN | Ngày cập nhật gần nhất |

---

### 1.2 `users` — Hồ sơ cá nhân

> Lưu thông tin profile của người dùng (1-1 với `accounts`).

| Column | Type | Constraints | Mô tả |
|--------|------|-------------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | ID hồ sơ |
| `account_id` | BIGINT | FK → accounts, UQ, NN | Liên kết tài khoản |
| `first_name` | VARCHAR(50) | NN | Tên |
| `last_name` | VARCHAR(50) | NN | Họ |
| `date_of_birth` | DATE | | Ngày sinh |
| `gender` | ENUM | | `MALE` \| `FEMALE` \| `OTHER` |
| `phone` | VARCHAR(20) | | Số điện thoại |
| `nationality` | VARCHAR(50) | | Quốc tịch |
| `passport_number` | VARCHAR(50) | | Số hộ chiếu |
| `avatar_url` | VARCHAR(255) | | Ảnh đại diện |
| `created_at` | DATETIME | NN | — |
| `updated_at` | DATETIME | NN | — |

---

### 1.3 `airports` — Sân bay

| Column | Type | Constraints | Mô tả |
|--------|------|-------------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — |
| `iata_code` | VARCHAR(3) | UQ, NN | Mã IATA (VD: `HAN`, `SGN`) |
| `icao_code` | VARCHAR(4) | UQ | Mã ICAO (VD: `VVNB`) |
| `name` | VARCHAR(100) | NN | Tên sân bay |
| `city` | VARCHAR(100) | NN | Thành phố |
| `country` | VARCHAR(100) | NN | Quốc gia |
| `timezone` | VARCHAR(50) | NN | VD: `Asia/Ho_Chi_Minh` |
| `created_at` | DATETIME | NN | — |

---

### 1.4 `aircraft` — Máy bay

| Column | Type | Constraints | Mô tả |
|--------|------|-------------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — |
| `registration_number` | VARCHAR(20) | UQ, NN | Số hiệu đăng ký (VD: `VN-A123`) |
| `model` | VARCHAR(50) | NN | VD: `Boeing 787`, `Airbus A320` |
| `manufacturer` | VARCHAR(50) | | Nhà sản xuất |
| `total_seats` | INT | NN | Tổng số ghế |
| `status` | ENUM | NN, DEFAULT 'ACTIVE' | `ACTIVE` \| `MAINTENANCE` \| `RETIRED` |
| `created_at` | DATETIME | NN | — |
| `updated_at` | DATETIME | NN | — |

---

### 1.5 `seats` — Cấu hình ghế ngồi

> Lưu sơ đồ ghế cố định của từng máy bay. Quan hệ N-N với `flights` thông qua `flight_seats`.

| Column | Type | Constraints | Mô tả |
|--------|------|-------------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — |
| `aircraft_id` | BIGINT | FK → aircraft, NN | Thuộc máy bay nào |
| `seat_number` | VARCHAR(5) | NN | VD: `12A`, `1B` |
| `class` | ENUM | NN | `ECONOMY` \| `BUSINESS` \| `FIRST` |
| `type` | ENUM | NN | `WINDOW` \| `MIDDLE` \| `AISLE` |

> **Constraint:** `UNIQUE(aircraft_id, seat_number)` — Mỗi ghế là duy nhất trên 1 máy bay.

---

### 1.6 `flights` — Chuyến bay

| Column | Type | Constraints | Mô tả |
|--------|------|-------------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — |
| `flight_number` | VARCHAR(10) | NN | VD: `VN123` |
| `aircraft_id` | BIGINT | FK → aircraft, NN | Máy bay vận hành |
| `departure_airport_id` | BIGINT | FK → airports, NN | Sân bay khởi hành |
| `arrival_airport_id` | BIGINT | FK → airports, NN | Sân bay đến |
| `departure_time` | DATETIME | NN | Giờ khởi hành |
| `arrival_time` | DATETIME | NN | Giờ đến dự kiến |
| `status` | ENUM | NN, DEFAULT 'SCHEDULED' | `SCHEDULED` \| `DELAYED` \| `BOARDING` \| `DEPARTED` \| `ARRIVED` \| `CANCELLED` |
| `base_price` | DECIMAL(15,2) | NN | Giá vé cơ sở (Economy) |
| `created_at` | DATETIME | NN | — |
| `updated_at` | DATETIME | NN | — |

> **Constraint:** `UNIQUE(flight_number, departure_time)` — Không có 2 chuyến bay trùng số hiệu và giờ bay.

---

### 1.7 `flight_seats` — Ghế trên chuyến bay cụ thể

> Bảng junction giữa `flights` và `seats`. Lưu giá và trạng thái của từng ghế cho mỗi chuyến bay.

| Column | Type | Constraints | Mô tả |
|--------|------|-------------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — |
| `flight_id` | BIGINT | FK → flights, NN | Chuyến bay |
| `seat_id` | BIGINT | FK → seats, NN | Ghế ngồi |
| `price` | DECIMAL(15,2) | NN | Giá ghế (có thể khác base_price) |
| `status` | ENUM | NN, DEFAULT 'AVAILABLE' | `AVAILABLE` \| `BOOKED` \| `BLOCKED` |

> **Constraint:** `UNIQUE(flight_id, seat_id)` — Một ghế chỉ xuất hiện một lần trên mỗi chuyến bay.

---

### 1.8 `flight_delays` — Thông tin delay/hủy chuyến

| Column | Type | Constraints | Mô tả |
|--------|------|-------------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — |
| `flight_id` | BIGINT | FK → flights, NN | Chuyến bay bị ảnh hưởng |
| `delay_minutes` | INT | NN | Số phút bị trễ |
| `reason` | VARCHAR(255) | | Lý do |
| `new_departure_time` | DATETIME | | Giờ khởi hành mới (nếu có) |
| `announced_at` | DATETIME | NN | Thời điểm thông báo |
| `created_by` | BIGINT | FK → accounts | Admin ghi nhận |
| `created_at` | DATETIME | NN | — |

---

### 1.9 `bookings` — Đặt vé

> Bảng trung tâm của hệ thống. Một booking có thể chứa nhiều chặng bay và nhiều hành khách.

| Column | Type | Constraints | Mô tả |
|--------|------|-------------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — |
| `booking_code` | VARCHAR(20) | UQ, NN | Mã đặt vé (VD: `ABS-20260307-001`) |
| `account_id` | BIGINT | FK → accounts, NN | Người đặt vé |
| `total_amount` | DECIMAL(15,2) | NN | Tổng tiền (vé + addons) |
| `status` | ENUM | NN, DEFAULT 'PENDING' | `PENDING` \| `CONFIRMED` \| `CANCELLED` \| `COMPLETED` |
| `payment_deadline` | DATETIME | NN | Hạn thanh toán (sau đó tự hủy) |
| `created_at` | DATETIME | NN | — |
| `updated_at` | DATETIME | NN | — |

---

### 1.10 `booking_flights` — Chặng bay trong booking

> Cho phép một booking có nhiều chặng (khứ hồi, quá cảnh).

| Column | Type | Constraints | Mô tả |
|--------|------|-------------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — |
| `booking_id` | BIGINT | FK → bookings, NN | Booking |
| `flight_id` | BIGINT | FK → flights, NN | Chuyến bay |
| `trip_type` | ENUM | NN | `OUTBOUND` \| `RETURN` |
| `sequence` | INT | NN, DEFAULT 1 | Thứ tự chặng (cho multi-leg) |

> **Constraint:** `UNIQUE(booking_id, flight_id)`

---

### 1.11 `passengers` — Hành khách

> Lưu thông tin từng hành khách trong một booking (không nhất thiết là chủ tài khoản).

| Column | Type | Constraints | Mô tả |
|--------|------|-------------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — |
| `booking_id` | BIGINT | FK → bookings, NN | Thuộc booking nào |
| `first_name` | VARCHAR(50) | NN | Tên |
| `last_name` | VARCHAR(50) | NN | Họ |
| `date_of_birth` | DATE | NN | Ngày sinh |
| `gender` | ENUM | NN | `MALE` \| `FEMALE` \| `OTHER` |
| `nationality` | VARCHAR(50) | NN | Quốc tịch |
| `passport_number` | VARCHAR(50) | | Số hộ chiếu (quốc tế) |
| `id_number` | VARCHAR(20) | | Số CCCD (nội địa) |
| `passenger_type` | ENUM | NN, DEFAULT 'ADULT' | `ADULT` \| `CHILD` \| `INFANT` |
| `created_at` | DATETIME | NN | — |

---

### 1.12 `booking_seats` — Phân công ghế cho hành khách

> Bảng junction 3 chiều: Booking ↔ FlightSeat ↔ Passenger.

| Column | Type | Constraints | Mô tả |
|--------|------|-------------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — |
| `booking_id` | BIGINT | FK → bookings, NN | Booking |
| `flight_seat_id` | BIGINT | FK → flight_seats, UQ, NN | Ghế cụ thể trên chuyến bay (1 ghế chỉ thuộc 1 booking) |
| `passenger_id` | BIGINT | FK → passengers, NN | Hành khách ngồi ghế đó |

---

### 1.13 `addons` — Dịch vụ bổ sung

| Column | Type | Constraints | Mô tả |
|--------|------|-------------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — |
| `booking_id` | BIGINT | FK → bookings, NN | Thuộc booking nào |
| `passenger_id` | BIGINT | FK → passengers | Hành khách áp dụng (nullable = áp dụng cả booking) |
| `flight_id` | BIGINT | FK → flights | Chặng bay áp dụng (nullable = áp dụng tất cả chặng) |
| `type` | ENUM | NN | `BAGGAGE` \| `MEAL` \| `INSURANCE` \| `PRIORITY_BOARDING` \| `LOUNGE_ACCESS` |
| `description` | VARCHAR(255) | | Mô tả chi tiết (VD: 23kg hành lý ký gửi) |
| `price` | DECIMAL(15,2) | NN | Giá addon |
| `created_at` | DATETIME | NN | — |

---

### 1.14 `payments` — Thanh toán

| Column | Type | Constraints | Mô tả |
|--------|------|-------------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — |
| `booking_id` | BIGINT | FK → bookings, NN | Booking thanh toán |
| `amount` | DECIMAL(15,2) | NN | Số tiền |
| `method` | ENUM | NN, DEFAULT 'VNPAY' | `VNPAY` \| `BANK_TRANSFER` \| `CASH` |
| `status` | ENUM | NN, DEFAULT 'PENDING' | `PENDING` \| `SUCCESS` \| `FAILED` \| `REFUNDED` |
| `vnpay_transaction_id` | VARCHAR(100) | | Mã giao dịch VNPay |
| `vnpay_response_code` | VARCHAR(10) | | Response code từ VNPay (`00` = thành công) |
| `paid_at` | DATETIME | | Thời điểm thanh toán thành công |
| `created_at` | DATETIME | NN | — |

---

### 1.15 `notifications` — Thông báo

| Column | Type | Constraints | Mô tả |
|--------|------|-------------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — |
| `account_id` | BIGINT | FK → accounts, NN | Người nhận |
| `title` | VARCHAR(255) | NN | Tiêu đề thông báo |
| `message` | TEXT | NN | Nội dung |
| `type` | ENUM | NN | `BOOKING_CONFIRMED` \| `BOOKING_CANCELLED` \| `FLIGHT_DELAYED` \| `PAYMENT_SUCCESS` \| `PAYMENT_FAILED` \| `SYSTEM` |
| `is_read` | BOOLEAN | NN, DEFAULT FALSE | Đã đọc chưa |
| `created_at` | DATETIME | NN | — |

---

### 1.16 `audit_logs` — Nhật ký hệ thống

| Column | Type | Constraints | Mô tả |
|--------|------|-------------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — |
| `actor_id` | BIGINT | FK → accounts (nullable) | Người thực hiện (NULL = System) |
| `actor_role` | ENUM | NN | `PASSENGER` \| `ADMIN` \| `SYSTEM` |
| `action` | VARCHAR(100) | NN | VD: `CREATE_BOOKING`, `CANCEL_FLIGHT` |
| `entity_type` | VARCHAR(50) | NN | VD: `BOOKING`, `FLIGHT`, `ACCOUNT` |
| `entity_id` | BIGINT | | ID của bản ghi bị tác động |
| `old_value` | JSON | | Trạng thái trước khi thay đổi |
| `new_value` | JSON | | Trạng thái sau khi thay đổi |
| `ip_address` | VARCHAR(45) | | IP của người thực hiện |
| `created_at` | DATETIME | NN | Thời điểm xảy ra |

---

### 1.17 `reviews` — Đánh giá chuyến bay

| Column | Type | Constraints | Mô tả |
|--------|------|-------------|-------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — |
| `account_id` | BIGINT | FK → accounts, NN | Người viết review |
| `flight_id` | BIGINT | FK → flights, NN | Chuyến bay được đánh giá |
| `booking_id` | BIGINT | FK → bookings, NN | Booking tham chiếu (đảm bảo đã bay thật) |
| `rating` | TINYINT | NN, CHECK(1-5) | Điểm đánh giá 1→5 sao |
| `title` | VARCHAR(255) | | Tiêu đề review |
| `comment` | TEXT | | Nội dung chi tiết |
| `status` | ENUM | NN, DEFAULT 'PENDING' | `PENDING` \| `APPROVED` \| `REJECTED` |
| `created_at` | DATETIME | NN | — |

> **Constraint:** `UNIQUE(account_id, flight_id, booking_id)` — Mỗi người chỉ được review 1 chuyến bay trong 1 booking.

---

## 2. Relationships

### 2.1 Quan hệ 1-1 (One-to-One)

| Bảng A | Bảng B | Mô tả |
|--------|--------|-------|
| `accounts` | `users` | Mỗi tài khoản có đúng một hồ sơ cá nhân |
| `flight_seats` | `booking_seats` | Một ghế trên chuyến bay chỉ thuộc tối đa một `booking_seat` |

### 2.2 Quan hệ 1-N (One-to-Many)

| Bảng "1" | Bảng "N" | FK | Mô tả |
|----------|----------|----|-------|
| `accounts` | `bookings` | `bookings.account_id` | Một tài khoản có nhiều đặt vé |
| `accounts` | `notifications` | `notifications.account_id` | Một tài khoản nhận nhiều thông báo |
| `accounts` | `reviews` | `reviews.account_id` | Một tài khoản viết nhiều review |
| `accounts` | `audit_logs` | `audit_logs.actor_id` | Một tài khoản tạo ra nhiều audit log |
| `airports` | `flights` | `flights.departure_airport_id` | Một sân bay là điểm khởi hành của nhiều chuyến |
| `airports` | `flights` | `flights.arrival_airport_id` | Một sân bay là điểm đến của nhiều chuyến |
| `aircraft` | `seats` | `seats.aircraft_id` | Một máy bay có nhiều ghế |
| `aircraft` | `flights` | `flights.aircraft_id` | Một máy bay vận hành nhiều chuyến bay |
| `flights` | `flight_seats` | `flight_seats.flight_id` | Một chuyến bay có nhiều ghế |
| `flights` | `flight_delays` | `flight_delays.flight_id` | Một chuyến có thể bị delay nhiều lần |
| `flights` | `booking_flights` | `booking_flights.flight_id` | Một chuyến bay có trong nhiều booking |
| `flights` | `reviews` | `reviews.flight_id` | Một chuyến bay nhận nhiều review |
| `seats` | `flight_seats` | `flight_seats.seat_id` | Một ghế vật lý xuất hiện trên nhiều chuyến bay |
| `bookings` | `booking_flights` | `booking_flights.booking_id` | Một booking gồm nhiều chặng bay |
| `bookings` | `passengers` | `passengers.booking_id` | Một booking chứa nhiều hành khách |
| `bookings` | `booking_seats` | `booking_seats.booking_id` | Một booking phân công nhiều ghế |
| `bookings` | `addons` | `addons.booking_id` | Một booking có nhiều dịch vụ bổ sung |
| `bookings` | `payments` | `payments.booking_id` | Một booking có thể có nhiều lần thanh toán |
| `passengers` | `booking_seats` | `booking_seats.passenger_id` | Một hành khách được gán nhiều ghế (nhiều chặng) |
| `passengers` | `addons` | `addons.passenger_id` | Một hành khách có nhiều addon |

### 2.3 Quan hệ N-N (Many-to-Many) — thông qua bảng junction

| Bảng A | Bảng B | Junction Table | Mô tả |
|--------|--------|----------------|-------|
| `flights` | `seats` | `flight_seats` | Mỗi chuyến bay × mỗi ghế → một bản ghi flight_seat |
| `bookings` | `flights` | `booking_flights` | Một booking nhiều chuyến, một chuyến nhiều booking |
| `bookings` | `passengers` × `flight_seats` | `booking_seats` | Phân công ghế cho từng hành khách trên từng chặng |

---

## 3. Redis Key Design

> Redis đóng vai trò **In-memory Store** cho 3 use case liên quan đến xác thực và bảo mật.  
> Tất cả key đều có **TTL** để tự động hết hạn — không cần cleanup thủ công.

---

### 3.1 Refresh Token Storage

```
Key:    refresh_token:{userId}
Value:  <refresh_token_string>
TTL:    604800 giây (7 ngày)
```

**Luồng hoạt động:**
1. Đăng nhập thành công → lưu Refresh Token vào Redis với key = `refresh_token:{userId}`
2. Client gọi `/refresh` → Backend đọc từ Redis, so sánh với token trong request
3. Nếu khớp → cấp Access Token mới, **rotate** Refresh Token (ghi đè key cũ)
4. Đăng xuất → xóa key khỏi Redis: `DEL refresh_token:{userId}`

> ⚠️ Bảng `refresh_tokens` MySQL **không còn được sử dụng** — toàn bộ logic chuyển sang Redis.

---

### 3.2 Access Token Blacklist

```
Key:    blacklist:token:{jti}
Value:  "revoked"
TTL:    <thời gian còn lại của Access Token (giây)>
```

> `jti` (JWT ID) là claim duy nhất được gắn vào mỗi Access Token khi phát hành.

**Luồng hoạt động:**
1. Client gọi `/logout` → Backend lấy `jti` từ Access Token hiện tại
2. Tính TTL còn lại của token (`exp - now`)
3. SET key vào Redis với TTL = TTL còn lại
4. Mọi request sau đó → Security Filter kiểm tra `EXISTS blacklist:token:{jti}` → nếu tồn tại → từ chối `401 Unauthorized`

**Tại sao cần `jti`?**  
Access Token JWT là stateless — nếu không có blacklist, sau khi logout token vẫn có thể dùng đến khi hết hạn tự nhiên.

---

### 3.3 OTP Cache

```
Key:    otp:{email}
Value:  <6-digit OTP code>
TTL:    300 giây (5 phút)
```

**Luồng hoạt động (Quên mật khẩu):**
```
1. POST /forgot-password { email }
   → Sinh OTP 6 số ngẫu nhiên
   → SET otp:{email} <otp> EX 300
   → Ghi log hệ thống

2. POST /verify-otp { email, otp }
   → GET otp:{email}
   → So sánh OTP, nếu đúng: DEL otp:{email} → cấp reset token tạm thời
   → Nếu sai hoặc key không tồn tại (hết hạn): trả về 400 Bad Request

3. POST /reset-password { reset_token, new_password }
   → Xác minh reset token → cập nhật password_hash trong MySQL
```

---

### 3.4 Tổng hợp Redis Key Patterns

| Key Pattern | Kiểu dữ liệu | TTL | Mục đích |
|-------------|-------------|-----|----------|
| `refresh_token:{userId}` | String | 7 ngày | Lưu Refresh Token |
| `blacklist:token:{jti}` | String | Động (exp còn lại) | Invalidate Access Token sau logout |
| `otp:{email}` | String | 5 phút | OTP xác minh quên mật khẩu |
