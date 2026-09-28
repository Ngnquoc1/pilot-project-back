# Hướng Dẫn Sử Dụng Scripts Mở Rộng Cơ Sở Dữ Liệu (Database Scaling Scripts)

## 📌 Tổng Quan
Hệ thống script mở rộng cơ sở dữ liệu PIM Tool được thiết kế nhằm **tăng gấp đôi (2x)** quy mô dữ liệu, phục vụ mục đích kiểm thử toàn diện các tính năng nâng cao (Advanced Features):
- **US01 - Visa Suggestion Box (Autocomplete)**: Kiểm thử giới hạn tối đa `limit = 10` gợi ý khi số lượng nhân viên vượt quá 10 (`18 nhân viên`).
- **US02 - Table Sorting Headers**: Kiểm thử sắp xếp 2 chiều ASC / DESC trên tập dữ liệu đa dạng (chuỗi, số, ngày tháng, trạng thái).
- **US02 - Server-side / Client-side Pagination**: Kiểm thử phân trang đa tầng:
  - Trang 10 items: Đủ 3 trang hoàn chỉnh (`30 items / 10 = 3 trang`).
  - Trang 5 items: Đủ 6 trang (`30 items / 5 = 6 trang`).
  - Trang 20 items: Đủ 2 trang (`Trang 1: 20 items, Trang 2: 10 items`).
- **US02 - Advanced Filter & QueryDSL**: Kiểm thử tìm kiếm kết hợp đa tiêu chí với 4 Groups và 30 Projects.

---

## 📊 Bảng Thống Kê Quy Mô Dữ Liệu (Trước & Sau Mở Rộng)

| Bảng CSDL | Dữ Liệu Ban Đầu | Sau Mở Rộng (x2) | Chi Tiết Dữ Liệu Mới |
| :--- | :---: | :---: | :--- |
| **`EMPLOYEE`** | 9 nhân viên | **18 nhân viên** | +9 nhân viên mới (Visa: `PL3`, `PL4`, `KMA`, `SME`, `PDU`, `CBE`, `TMO`, `LRO`, `EFO`) |
| **`"group"`** | 2 nhóm | **4 nhóm** | +2 nhóm mới (`Group 3` do PL3 dẫn dắt, `Group 4` do PL4 dẫn dắt) |
| **`PROJECT`** | 15 dự án | **30 dự án** | +15 dự án mới (Mã số: `1016` đến `1030`), đầy đủ các trạng thái (`NEW`, `PLA`, `INP`, `FIN`) |
| **`PROJECT_EMPLOYEE`** | 28 liên kết | **63 liên kết** | +35 liên kết thành viên dự án mới, phân bổ đều giữa nhân viên cũ và mới |

---

## 📁 Danh Sách File Script

1. **`src/main/resources/data.sql`** *(Tự động chạy)*:
   - Đã được cập nhật trực tiếp vào mã nguồn Spring Boot.
   - Khi chạy ứng dụng bằng `mvn spring-boot:run` hoặc khởi động class `ApplicationLauncher`, CSDL H2 in-memory sẽ tự động khởi tạo toàn bộ 30 dự án và 18 nhân viên.

2. **`scripts/expand_database.sql`** *(Script chèn nối tiếp)*:
   - Dành cho môi trường CSDL đã có sẵn dữ liệu gốc (15 dự án đầu).
   - Sử dụng các câu truy vấn con (`SELECT ID FROM ... WHERE VISA = ...`) để tự động map khóa ngoại an toàn, không lo lệch ID auto-increment.

3. **`scripts/seed_database_complete.sql`** *(Script khởi tạo trọn gói)*:
   - Toàn bộ câu lệnh SQL thuần túy từ đầu cho 18 nhân viên, 4 nhóm, 30 dự án và 63 liên kết.
   - Phù hợp chạy trong H2 Console, DBeaver, MySQL, Oracle hoặc database migration tool.

---

## 🚀 Cách Chạy Script

### Cách 1: Tự động khởi động cùng Backend (Khuyên dùng)
Không cần thao tác gì thêm! Khởi động ứng dụng bằng lệnh:
```bash
mvn spring-boot:run
```
Hệ thống sẽ tự nạp file `data.sql` mới với 30 dự án.

### Cách 2: Chạy qua H2 Console
1. Khởi động Backend và truy cập trình duyệt tại: `http://localhost:8080/h2console/`
2. Cấu hình kết nối:
   - **JDBC URL**: `jdbc:h2:mem:onboardingexercise;`
   - **User Name**: `sa`
   - **Password**: *(để trống)*
3. Mở file [`scripts/expand_database.sql`](expand_database.sql) (nếu muốn bổ sung dữ liệu) hoặc [`scripts/seed_database_complete.sql`](seed_database_complete.sql) (nếu muốn tạo mới hoàn toàn), copy nội dung vào cửa sổ SQL và nhấn **Run**.
