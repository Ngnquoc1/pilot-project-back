# Pilot Project Back - Nhật ký lỗi và Cách khắc phục (Issue Log & Solutions)

Tài liệu này ghi lại các lỗi gặp phải trong quá trình thiết lập và phát triển dự án `pilot-project-back` kèm theo nguyên nhân và cách khắc phục chi tiết.

---

## 1. Lỗi không tìm thấy class `QProject` (QueryDSL Compilation Error)

### Mô tả lỗi
Khi build dự án hoặc mở các file có sử dụng QueryDSL (như `ProjectRepositoryTest`, `TaskRepositoryCustomImpl`), trình biên dịch báo lỗi:
```text
java: cannot find symbol
  symbol:   class QProject
  location: package vn.elca.training.model.entity
```

### Nguyên nhân
- `QProject`, `QTask`, `QUser`... là các **Q-classes (Metamodel)** được thư viện **QueryDSL** tự động sinh ra từ các JPA Entities (như `Project`, `Task`, `User`) thông qua annotation processor `apt-maven-plugin`.
- Khi mới clone dự án về hoặc sau khi chạy `mvn clean`, thư mục mã nguồn tự sinh (`target/generated-sources`) chưa được tạo ra, hoặc IDE chưa nhận diện thư mục này là **Generated Sources Root**.

### Cách khắc phục
1. **Chạy Maven Compile**:
   Chạy lệnh sau tại thư mục gốc của dự án để sinh lại toàn bộ Q-classes:
   ```bash
   mvn clean compile
   ```
2. **Cấu hình trên IDE (IntelliJ IDEA)**:
   - Mở tab **Maven** $\rightarrow$ `Lifecycle` $\rightarrow$ double-click vào **`compile`**.
   - Chuột phải vào thư mục `target/generated-sources` trong Project view $\rightarrow$ chọn **Mark Directory as** $\rightarrow$ **Generated Sources Root**.
   - Bật Annotation Processors: Vào **Settings/Preferences** $\rightarrow$ **Build, Execution, Deployment** $\rightarrow$ **Compiler** $\rightarrow$ **Annotation Processors** $\rightarrow$ tích chọn **Enable annotation processing**.

---

## 2. Lỗi cấu hình Custom Repository với class `RenameThisClass` (Spring Data JPA Wiring Error)

### Mô tả lỗi
- Class `vn.elca.training.repository.custom.RenameThisClass` thực thi interface `TaskRepositoryCustom`.
- Interface chính `TaskRepository` kế thừa cả `JpaRepository` và `TaskRepositoryCustom`.
- Khi ứng dụng Spring Boot khởi động, Spring Data JPA không thể liên kết (wire) phần code custom này vào `TaskRepository`, dẫn đến lỗi `BeanCreationException` / `PropertyReferenceException` khi cố phân tích các hàm custom thành derived queries.

### Nguyên nhân
- **Quy ước đặt tên (Naming Convention) của Spring Data JPA**: Khi một Repository interface kế thừa một Custom interface (ví dụ `TaskRepository` kế thừa `TaskRepositoryCustom`), Spring Data JPA sẽ tự động tìm kiếm class thực thi dựa trên quy ước đặt tên:
  - `<CustomInterfaceName>Impl` $\rightarrow$ `TaskRepositoryCustomImpl`
  - Hoặc `<RepositoryName>Impl` $\rightarrow$ `TaskRepositoryImpl`
- Do class đang được đặt tên tùy ý là `RenameThisClass`, Spring Data JPA không nhận ra đây là implementation của `TaskRepositoryCustom`, đồng thời class cũng không có annotation `@Component`/`@Repository` để đăng ký làm bean độc lập.

### Cách khắc phục
1. **Đổi tên class và file**:
   - Đổi tên file từ `RenameThisClass.java` thành `TaskRepositoryCustomImpl.java` (hoặc `TaskRepositoryImpl.java`).
   - Đổi tên khai báo class trong code:
     ```java
     package vn.elca.training.repository.custom;

     // Đổi từ public class RenameThisClass sang:
     public class TaskRepositoryCustomImpl implements TaskRepositoryCustom {
         // ... nội dung giữ nguyên
     }
     ```
2. Sau khi đổi tên đúng chuẩn, Spring Data JPA sẽ tự động nhận diện và ghép các method `findProjectsByTaskName` và `listRecentTasks` vào `TaskRepository` mà không cần cấu hình gì thêm.

---

## 3. Lỗi `NullPointerException` tại `ProjectServiceImpl.count()` (Missing Dependency Injection)

### Mô tả lỗi
Khi truy cập endpoint `/main`, hệ thống gặp lỗi ngoại lệ:
```text
java.lang.NullPointerException: null
	at vn.elca.training.service.impl.ProjectServiceImpl.count(ProjectServiceImpl.java:28)
	at vn.elca.training.web.MainController.main(MainController.java:32)
```

### Nguyên nhân
- Trong class `ProjectServiceImpl`, thuộc tính `projectRepository` chỉ được khai báo dạng `private ProjectRepository projectRepository;` nhưng **chưa được tiêm (inject) Bean vào** (thiếu `@Autowired` hoặc Constructor Injection).
- Khi gọi phương thức `projectService.count()`, biến `this.projectRepository` đang mang giá trị `null`, dẫn đến việc gọi `null.count()` gây ra `NullPointerException`.
- Tương tự trong `MainController`, thuộc tính `private String title;` cũng bị thiếu annotation `@Value("${application.title}")` để đọc giá trị từ file `messages.properties`.

### Cách khắc phục
1. **Trong `ProjectServiceImpl.java`**: Thêm `@Autowired` (Field injection hoặc Constructor injection):
   ```java
   @Service
   @Profile("!dummy | dev")
   public class ProjectServiceImpl implements ProjectService {

       @Autowired
       private ProjectRepository projectRepository;

       // ...
   }
   ```
2. **Trong `MainController.java`**: Thêm `@Value("${application.title}")` cho biến `title`:
   ```java
   @Value("${application.title}")
   private String title;
   ```
