# 🎭 MOCKITO MASTER CHEATSHEET — CẨM NANG TOÀN DIỆN & CHUYÊN SÂU

> **Tài liệu hướng dẫn chi tiết, giải thích bản chất từ cơ bản đến nâng cao kèm ví dụ thực tế đầy đủ nhất về Mockito Framework dành cho Java Developers.**

---

## 📌 MỤC LỤC
1. [Bản chất của Mocking & Các loại Test Doubles](#1-bản-chất-của-mocking--các-loại-test-doubles)
2. [Cài đặt & Cơ chế khởi tạo Mockito](#2-cài-đặt--cơ-chế-khởi-tạo-mockito)
3. [Phân tích chuyên sâu 4 Annotations cốt lõi (@Mock, @Spy, @InjectMocks, @Captor)](#3-phân-tích-chuyên-sâu-4-annotations-cốt-lõi)
4. [So sánh chi tiết: @Mock vs @Spy vs @MockBean (Spring)](#4-so-sánh-chi-tiết-mock-vs-spy-vs-mockbean)
5. [Stubbing từ cơ bản đến nâng cao (thenReturn, thenThrow, thenAnswer, thenCallRealMethod)](#5-stubbing-từ-cơ-bản-đến-nâng-cao)
6. [Sự khác biệt kỹ thuật sống còn: `when(...).thenReturn(...)` vs `doReturn(...).when(...)`](#6-sự-khác-biệt-kỹ-thuật-when-vs-doreturn)
7. [Stubbing cho hàm void (doNothing, doThrow, doAnswer)](#7-stubbing-cho-hàm-void)
8. [BDDMockito (Phong cách Given - When - Then)](#8-bddmockito-phong-cách-given---when---then)
9. [Argument Matchers toàn tập (any, eq, argThat, Regex)](#9-argument-matchers-toàn-tập)
10. [Verification toàn tập (Số lần gọi, Thứ tự InOrder, Timeout, NoInteractions)](#10-verification-toàn-tập)
11. [ArgumentCaptor chuyên sâu (Bắt và kiểm tra dữ liệu ngầm)](#11-argumentcaptor-chuyên-sâu)
12. [Mock Static Methods & Mock Constructor (`mockStatic`, `mockConstruction`)](#12-mock-static-methods--mock-constructor)
13. [Kịch bản thực tế trọn vẹn (Full Service Test Case)](#13-kịch-bản-thực-tế-trọn-vẹn)
14. [Sổ tay bắt bệnh: Các lỗi thường gặp & Cách xử lý tận gốc](#14-sổ-tay-bắt-bệnh-các-lỗi-thường-gặp)

---

## 1. Bản chất của Mocking & Các loại Test Doubles

### 1.1. Tại sao phải Mock?
Khi viết **Unit Test** cho một class (ví dụ `ProjectServiceImpl`), class đó thường phụ thuộc vào các thành phần khác: `ProjectRepository` (kết nối DB), `EmailService` (gửi mail ra ngoài), `PaymentGateway` (gọi API ngân hàng).
* Nếu gọi thật: Test sẽ chạy chậm, phụ thuộc mạng/DB, và khi DB lỗi thì test fail dù code logic Service vẫn đúng.
* **Giải pháp:** Thay thế các phụ thuộc đó bằng các **đối tượng giả lập (Mocks)**. Ta kiểm soát 100% dữ liệu đầu vào và kết quả trả về của Mock để tập trung kiểm tra logic nội tại của Service.

### 1.2. Phân loại Test Doubles (Thuật ngữ chuẩn)
* **Dummy:** Đối tượng truyền vào chỉ để "cho đủ tham số", không bao giờ được dùng đến.
* **Stub:** Đối tượng giả trả về dữ liệu cố định đã được cấu hình trước (VD: `when(repo.findById(1L)).thenReturn(project)`).
* **Spy (Partial Mock):** Đối tượng thật bọc ngoài, theo dõi hành vi gọi hàm và cho phép ghi đè một vài hàm nhất định.
* **Mock:** Đối tượng giả được lập trình sẵn các kỳ vọng (expectations) và có thể xác minh (verify) xem nó có được gọi đúng số lần, đúng tham số hay không.

---

## 2. Cài đặt & Cơ chế khởi tạo Mockito

### 2.1. Cấu hình Maven
```xml
<!-- Nếu dùng Spring Boot, starter-test đã tích hợp sẵn Mockito -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Nếu dùng Java thuần không có Spring Boot -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

### 2.2. Cơ chế kích hoạt Mockito trong Test Class

#### Cách 1: JUnit 5 (Khuyên dùng)
```java
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class) // Tự động khởi tạo @Mock và @InjectMocks
class ProjectServiceTest {
    // ...
}
```

#### Cách 2: JUnit 4
```java
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {
    // ...
}
```

#### Cách 3: Thủ công với `openMocks` (Dành cho TestNG hoặc Base Test Class)
```java
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

class ManualInitTest {
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        // Khởi tạo tất cả @Mock, @Spy, @InjectMocks trong class này
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close(); // Giải phóng tài nguyên
    }
}
```

---

## 3. Phân tích chuyên sâu 4 Annotations cốt lõi

### 3.1. `@Mock`
* **Cơ chế:** Mockito dùng thư viện Byte Buddy sinh ra một class con (subclass) runtime kế thừa từ class/interface được chỉ định.
* **Hành vi mặc định:** Mọi phương thức khi được gọi sẽ **không chạy code thật**, mà trả về giá trị mặc định của kiểu dữ liệu:
  * `boolean` $\rightarrow$ `false`
  * Số (`int`, `long`, `double`...) $\rightarrow$ `0`
  * Đối tượng (`Object`, `String`...) $\rightarrow$ `null`
  * `Optional` $\rightarrow$ `Optional.empty()`
  * Collection (`List`, `Set`, `Map`) $\rightarrow$ Collection rỗng (không phải null).

```java
@Mock
private ProjectRepository projectRepository; // Tạo mock instance
```

---

### 3.2. `@Spy` (Partial Mock)
* **Cơ chế:** Tạo ra một đối tượng **bọc quanh instance thật**.
* **Hành vi mặc định:** Gọi code thật 100%. Trừ khi bạn chủ động stub phương thức nào đó thì phương thức đó mới trả về giá trị giả.

```java
@Spy
private ApplicationMapper applicationMapper = new ApplicationMapperImpl(); // Instance thật
```

---

### 3.3. `@InjectMocks` (Cơ chế tiêm phụ thuộc)
Mockito sẽ tự động tìm các `@Mock` hoặc `@Spy` có trong test class và tiêm vào đối tượng được đánh dấu `@InjectMocks` theo thứ tự ưu tiên sau:

1. **Constructor Injection (Ưu tiên số 1 - Khuyên dùng):** Tìm constructor có tham số khớp nhất với các `@Mock`.
2. **Property Setter Injection (Ưu tiên số 2):** Tìm các hàm setter (`setProjectRepository(...)`).
3. **Field Injection (Ưu tiên số 3):** Dùng Java Reflection set trực tiếp vào private field.

```java
@Mock
private ProjectRepository projectRepository;

@InjectMocks
private ProjectServiceImpl projectService; 
// Mockito sẽ gọi: new ProjectServiceImpl(projectRepository);
```

> [!NOTE]
> `@InjectMocks` chỉ khởi tạo class cụ thể (Concrete Class), **không thể dùng `@InjectMocks` trên một `Interface`**!

---

### 3.4. `@Captor`
Dùng để khai báo `ArgumentCaptor` dùng cho việc bắt tham số khi verify (chi tiết ở [Mục 11](#11-argumentcaptor-chuyên-sâu)).

```java
@Captor
private ArgumentCaptor<Project> projectCaptor;
```

---

## 4. So sánh chi tiết: `@Mock` vs `@Spy` vs `@MockBean`

| Tiêu chí | `@Mock` | `@Spy` | `@MockBean` (Spring Boot) |
| :--- | :--- | :--- | :--- |
| **Bản chất** | Đối tượng giả lập 100%. | Đối tượng thật bị can thiệp 1 phần. | Bean giả lập đăng ký vào Spring IoC Context. |
| **Chạy code thật?** | **Không** (trả về null/default). | **Có** (mặc định chạy code thật). | **Không** (giống `@Mock`). |
| **Nạp Spring Context?** | **Không** (cực nhanh, mili-giây). | **Không** (cực nhanh, mili-giây). | **Có** (chậm hơn vì phải nạp Context). |
| **Phạm vi sử dụng** | **Unit Test thuần túy** cho Service, Util, Validator. | Test class có logic phức tạp cần giữ lại hầu hết method thật. | **Integration Test / Controller Test** (`@WebMvcTest`, `@SpringBootTest`). |

---

## 5. Stubbing từ cơ bản đến nâng cao

### 5.1. `thenReturn(...)` — Trả về kết quả cố định & Chained Invocations

```java
// 1. Trả về một đối tượng cụ thể
Project mockProject = new Project("EFV", LocalDate.now());
when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));

// 2. Chained Invocations: Mỗi lần gọi liên tiếp trả về một kết quả khác nhau
when(projectRepository.count())
    .thenReturn(10L)  // Lần gọi 1 -> trả về 10
    .thenReturn(11L)  // Lần gọi 2 -> trả về 11
    .thenReturn(12L); // Lần gọi 3 trở đi -> trả về 12
```

---

### 5.2. `thenThrow(...)` — Giả lập ném Ngoại lệ (Exception)

```java
// Giả lập khi tìm ID 999L thì ném ngoại lệ Runtime
when(projectRepository.findById(999L))
    .thenThrow(new ProjectNotFoundException(999L));

// Giả lập ném Exception theo Class type (Mockito sẽ tự instantiate)
when(projectRepository.findAll())
    .thenThrow(IllegalStateException.class);
```

---

### 5.3. `thenAnswer(...)` — Tính toán kết quả động dựa trên Input
Dùng khi bạn muốn kết quả trả về phụ thuộc vào tham số truyền vào hàm (rất phổ biến khi mock hàm `repository.save()`).

```java
when(projectRepository.save(any(Project.class)))
    .thenAnswer(invocation -> {
        // 1. Lấy tham số truyền vào method save(Project p)
        Project projectArg = invocation.getArgument(0);

        // 2. Giả lập logic DB: tự động sinh ID nếu chưa có
        if (projectArg.getId() == null) {
            projectArg.setId(999L);
        }

        // 3. Trả về chính entity đã được gắn ID
        return projectArg;
    });

// Kiểm chứng:
Project newProject = new Project("TEST", LocalDate.now());
Project saved = projectRepository.save(newProject);
assertEquals(999L, saved.getId()); // ID đã được thenAnswer sinh động
```

---

### 5.4. `thenCallRealMethod()` — Gọi method thật
Chỉ áp dụng cho `@Spy` hoặc abstract class/interface có `default method`.

```java
when(spyService.calculateDiscount(anyDouble())).thenCallRealMethod();
```

---

## 6. Sự khác biệt kỹ thuật: `when(...).thenReturn(...)` vs `doReturn(...).when(...)`

Đây là lỗi kinh điển khiến rất nhiều lập trình viên bối rối.

### Bản chất:
* `when(mock.method())`: Phương thức `method()` **thực sự được gọi trước** khi Mockito kịp gắn stub vào!
  * Với `@Mock`: Do method là giả nên gọi trước không sao.
  * Với `@Spy`: Do method là thật, nên code thật sẽ **chạy ngay tại dòng `when(...)`** $\rightarrow$ Dễ gây lỗi `IndexOutOfBoundsException` hoặc `NullPointerException`.
* `doReturn(val).when(mock).method()`: Mockito gắn stub vào **trước**, phương thức thật **hoàn toàn không bị kích hoạt**.

### Ví dụ minh họa sự cố:

```java
List<String> realList = new ArrayList<>();
List<String> spyList = Mockito.spy(realList);

// ❌ SAI LẦM: Gây ra IndexOutOfBoundsException ngay tại dòng này!
// Vì spyList.get(0) cố đọc phần tử 0 của một list rỗng trước khi stub kịp có hiệu lực.
when(spyList.get(0)).thenReturn("First Element");

// ✅ ĐÚNG CHUẨN KHI DÙNG VỚI @SPY:
doReturn("First Element").when(spyList).get(0);
```

> [!IMPORTANT]
> **Quy tắc bỏ túi:**
> * Đối với `@Mock`: Dùng `when(...).thenReturn(...)` cho thuận mắt.
> * Đối với `@Spy` hoặc hàm `void`: **BẮT BUỘC dùng `doReturn / doThrow / doNothing`**.

---

## 7. Stubbing cho hàm void

Hàm `void` không trả về giá trị nên không thể viết `when(repo.delete(x)).thenReturn(...)`. Ta dùng cú pháp `do...when`:

```java
// 1. Giả lập hàm void ném Exception khi xóa
doThrow(new DataIntegrityViolationException("Khóa ngoại bị ràng buộc"))
    .when(projectRepository).deleteById(1L);

// 2. Giả lập hàm void không làm gì cả (Hữu ích khi muốn bỏ qua hàm gửi mail/log)
doNothing().when(emailService).sendNotification(anyString(), anyString());

// 3. Giả lập hàm void can thiệp vào tham số (doAnswer)
doAnswer(invocation -> {
    String message = invocation.getArgument(0);
    System.out.println("Audit log nhận được: " + message);
    return null; // Hàm void luôn return null trong Answer
}).when(auditService).log(anyString());
```

---

## 8. BDDMockito (Phong cách Given - When - Then)

Nếu team của bạn theo phong cách BDD, `BDDMockito` là wrapper của Mockito giúp code test đọc như một câu văn tiếng Anh:

```java
import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@Test
void testFindById_BddStyle() {
    // 1. [Given] Tiền đề
    Project project = new Project("KSTA", LocalDate.now());
    given(projectRepository.findById(1L)).willReturn(Optional.of(project));

    // 2. [When] Hành động
    Project result = projectService.findById(1L);

    // 3. [Then] Kỳ vọng
    then(projectRepository).should(times(1)).findById(1L);
    then(projectRepository).shouldHaveNoMoreInteractions();
    assertEquals("KSTA", result.getName());
}
```

### Bảng chuyển đổi cú pháp BDD:
* `when(mock.foo()).thenReturn(bar)` $\longrightarrow$ `given(mock.foo()).willReturn(bar)`
* `when(mock.foo()).thenThrow(ex)` $\longrightarrow$ `given(mock.foo()).willThrow(ex)`
* `verify(mock).foo()` $\longrightarrow$ `then(mock).should().foo()`

---

## 9. Argument Matchers toàn tập

Matchers cho phép viết stub hoặc verify linh hoạt mà không cần cố định giá trị truyền vào.

### 9.1. Danh mục Matchers thông dụng
```java
import static org.mockito.ArgumentMatchers.*;

any()                 // Bất kỳ kiểu gì (kể cả null)
any(Project.class)    // Bất kỳ đối tượng nào thuộc kiểu Project (hoặc class con)
anyString()           // Bất kỳ chuỗi String nào (không null)
anyInt(), anyLong()   // Bất kỳ số nguyên / số long
anyList(), anySet()   // Bất kỳ List / Set nào
isNull(), isNotNull() // Khớp null hoặc không null
contains("ABC")       // Khớp chuỗi chứa "ABC"
matches("^[0-9]+$")   // Khớp chuỗi theo Regex
```

---

### 9.2. Custom Matcher với `argThat(...)`
Cho phép kiểm tra logic phức tạp trên tham số truyền vào:

```java
// Chỉ stub khi Project truyền vào có tên bắt đầu bằng "PRJ_" và finishingDate trong tương lai
when(projectRepository.save(argThat(project -> 
    project.getName() != null 
    && project.getName().startsWith("PRJ_") 
    && project.getFinishingDate().isAfter(LocalDate.now())
))).thenReturn(new Project("SAVED_OK", LocalDate.now()));
```

---

### 9.3. ⚠️ QUY TẮC BẤT DI BẤT DỊCH CỦA MATCHERS
> Nếu trong một method có **nhiều tham số**, một khi đã dùng Matcher cho 1 tham số thì **TẤT CẢ các tham số còn lại bắt buộc phải là Matcher**.

```java
// ❌ SAI: Ném InvalidUseOfMatchersException (Vì tham số thứ 2 là giá trị raw "ACTIVE")
when(taskRepository.findByProjectIdAndStatus(anyLong(), "ACTIVE")).thenReturn(list);

// ✅ ĐÚNG: Bọc giá trị raw bằng hàm eq()
when(taskRepository.findByProjectIdAndStatus(anyLong(), eq("ACTIVE"))).thenReturn(list);
```

---

## 10. Verification toàn tập

Verification dùng để trả lời câu hỏi: *"Method này có thực sự được gọi không? Gọi bao nhiêu lần? Thứ tự trước sau ra sao?"*

### 10.1. Kiểm tra tần suất gọi hàm
```java
// Gọi đúng 1 lần (mặc định verify(mock) tương đương verify(mock, times(1)))
verify(projectRepository, times(1)).findById(1L);

// KHÔNG BAO GIỜ được gọi
verify(projectRepository, never()).deleteById(anyLong());

// Gọi ít nhất 1 lần / ít nhất N lần
verify(auditService, atLeastOnce()).log(anyString());
verify(auditService, atLeast(2)).log(anyString());

// Gọi tối đa N lần
verify(auditService, atMost(3)).log(anyString());

// CHỈ DUY NHẤT method này được gọi trên mock và không còn method nào khác
verify(projectRepository, only()).findById(1L);
```

---

### 10.2. Kiểm tra không có tương tác thừa (`verifyNoInteractions` & `verifyNoMoreInteractions`)
```java
// 1. Đảm bảo mock KHÔNG HỀ bị đụng tới trong suốt test case
verifyNoInteractions(emailService);

// 2. Đảm bảo sau khi đã verify findById, repo không bị gọi thêm bất kỳ hàm nào khác
verify(projectRepository).findById(1L);
verifyNoMoreInteractions(projectRepository);
```

---

### 10.3. Kiểm tra Thứ tự thực thi (`InOrder`)
Đảm bảo các bước nghiệp vụ diễn ra đúng trình tự (Ví dụ: Phải Lưu Project xong rồi mới được Ghi Audit Log).

```java
// 1. Tạo đối tượng InOrder truyền vào các mock cần giám sát thứ tự
InOrder inOrder = inOrder(projectRepository, auditService);

// 2. Thực thi hàm service
projectService.createProject(dto);

// 3. Xác minh thứ tự: save() PHẢI chạy trước log()
inOrder.verify(projectRepository).save(any(Project.class));
inOrder.verify(auditService).log(eq("CREATE_PROJECT"));
```

---

### 10.4. Kiểm tra trong môi trường Bất đồng bộ / Đa luồng (`timeout`)
Nếu method được gọi bên trong một background thread (`CompletableFuture`, `@Async`):

```java
// Chờ tối đa 1000ms để method sendEmail được gọi đúng 1 lần
verify(emailService, timeout(1000).times(1)).sendEmail(any());
```

---

## 11. ArgumentCaptor chuyên sâu

`ArgumentCaptor` cho phép bạn "chặn bắt" đối tượng thực tế được truyền vào Mock để soi chi tiết từng field bên trong.

### 11.1. Bắt 1 đối số (Single Invocation)

```java
@Test
void testCreateProject_ShouldSetDefaultFinishingDate() {
    // Given
    ProjectDto dto = new ProjectDto();
    dto.setName("NEW_APP");

    // When
    projectService.create(dto);

    // Then: Bắt tham số truyền vào hàm save
    ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
    verify(projectRepository).save(projectCaptor.capture());

    // Lấy đối tượng bị bắt ra kiểm tra
    Project capturedProject = projectCaptor.getValue();
    assertEquals("NEW_APP", capturedProject.getName());
    assertNotNull(capturedProject.getFinishingDate(), "Ngày kết thúc phải tự động được set default");
}
```

---

### 11.2. Bắt nhiều đối số qua nhiều lần gọi (Multiple Invocations)

```java
@Test
void testBatchProcessing_CaptureAllCalls() {
    // Giả sử service gọi save 3 lần với 3 Project khác nhau
    projectService.importProjects(List.of("PRJ_1", "PRJ_2", "PRJ_3"));

    ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
    verify(projectRepository, times(3)).save(captor.capture());

    // Lấy toàn bộ danh sách các đối tượng qua 3 lần gọi
    List<Project> allCaptured = captor.getAllValues();
    assertEquals(3, allCaptured.size());
    assertEquals("PRJ_1", allCaptured.get(0).getName());
    assertEquals("PRJ_2", allCaptured.get(1).getName());
    assertEquals("PRJ_3", allCaptured.get(2).getName());
}
```

---

## 12. Mock Static Methods & Mock Constructor

Từ phiên bản **Mockito 3.4.0+**, bạn có thể mock method tĩnh hoặc mock việc khởi tạo `new Object()` mà không cần bất kỳ plugin ngoài nào.

### 12.1. Mock Static Method (`mockStatic`)
Cực kỳ hữu dụng khi test các hàm liên quan đến thời gian hệ thống (`LocalDate.now()`, `System.currentTimeMillis()`) hoặc UUID.

```java
import org.mockito.MockedStatic;
import static org.mockito.Mockito.*;

@Test
void testFixedSystemDate() {
    LocalDate fixedDate = LocalDate.of(2026, 9, 4);

    // Sử dụng try-with-resources để tự động đóng mock static khi ra khỏi block
    try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
        // Cố định LocalDate.now() luôn trả về ngày 04/09/2026
        mockedLocalDate.when(LocalDate::now).thenReturn(fixedDate);

        // Gọi code nghiệp vụ
        LocalDate actual = projectService.getCurrentDate();

        assertEquals(LocalDate.of(2026, 9, 4), actual);
    }

    // Ra ngoài block try, LocalDate.now() tự động trở lại bình thường
}
```

---

### 12.2. Mock Constructor (`mockConstruction`)
Dùng khi một method bên trong service tự tạo đối tượng bằng từ khóa `new` mà không qua Dependency Injection:

```java
@Test
void testMockNewObjectCreation() {
    // Mock mọi instance của RestTemplate khi được khởi tạo bằng `new RestTemplate()`
    try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
            (mock, context) -> {
                when(mock.getForObject(anyString(), eq(String.class))).thenReturn("MOCKED_RESPONSE");
            })) {

        // Service bên trong tự gọi: RestTemplate restTemplate = new RestTemplate();
        String result = externalApiService.callApi();

        assertEquals("MOCKED_RESPONSE", result);
        assertEquals(1, mocked.constructed().size()); // Đã tạo 1 instance giả
    }
}
```

---

## 13. Kịch bản thực tế trọn vẹn (Full Service Test Case)

Dưới đây là một class Test hoàn chỉnh chuẩn Enterprise áp dụng toàn bộ kiến thức trên để test [`ProjectServiceImpl`](file:///C:/Users/nnnq/01_Trainee/newcomers-java-master@d35fff52243/pilot-project-back/src/main/java/vn/elca/training/service/impl/ProjectServiceImpl.java):

```java
package vn.elca.training.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.elca.training.model.dto.ProjectDto;
import vn.elca.training.model.entity.Project;
import vn.elca.training.model.exception.ProjectNotFoundException;
import vn.elca.training.repository.ProjectRepository;
import vn.elca.training.service.impl.ProjectServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử chuyên sâu cho ProjectServiceImpl")
class ProjectServiceImplDeepTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Captor
    private ArgumentCaptor<Project> projectCaptor;

    // ==========================================
    // 1. NHÓM TEST: TÌM KIẾM THEO ID (findById)
    // ==========================================
    @Nested
    @DisplayName("Hàm findById")
    class FindByIdTests {

        @Test
        @DisplayName("Thành công: Trả về Entity khi tìm thấy ID hợp lệ")
        void findById_WhenIdExists_ShouldReturnProject() {
            // [Given]
            Project mockProject = new Project("EFV_PROJECT", LocalDate.of(2026, 12, 31));
            mockProject.setId(1L);
            when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));

            // [When]
            Project actual = projectService.findById(1L);

            // [Then]
            assertNotNull(actual);
            assertEquals(1L, actual.getId());
            assertEquals("EFV_PROJECT", actual.getName());
            verify(projectRepository, times(1)).findById(1L);
            verifyNoMoreInteractions(projectRepository);
        }

        @Test
        @DisplayName("Thất bại: Ném ProjectNotFoundException khi ID không tồn tại")
        void findById_WhenIdNotFound_ShouldThrowException() {
            // [Given]
            when(projectRepository.findById(999L)).thenReturn(Optional.empty());

            // [When & Then]
            ProjectNotFoundException exception = assertThrows(
                ProjectNotFoundException.class,
                () -> projectService.findById(999L)
            );

            assertTrue(exception.getMessage().contains("999"));
            verify(projectRepository, times(1)).findById(999L);
        }
    }

    // ==========================================
    // 2. NHÓM TEST: CẬP NHẬT DỰ ÁN (update)
    // ==========================================
    @Nested
    @DisplayName("Hàm update")
    class UpdateTests {

        @Test
        @DisplayName("Thành công: Cập nhật đúng các trường và lưu vào DB")
        void update_WhenValidInput_ShouldUpdateAndSave() {
            // [Given]
            Project existingProject = new Project("OLD_NAME", LocalDate.of(2025, 1, 1));
            existingProject.setId(10L);
            existingProject.setCustomer("OLD_CUSTOMER");

            ProjectDto updateDto = new ProjectDto();
            updateDto.setName("NEW_NAME");
            updateDto.setCustomer("NEW_CUSTOMER");
            LocalDate newDate = LocalDate.of(2026, 6, 30);
            updateDto.setFinishingDate(newDate);

            when(projectRepository.findById(10L)).thenReturn(Optional.of(existingProject));
            when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

            // [When]
            Project result = projectService.update(updateDto, 10L);

            // [Then]
            verify(projectRepository).save(projectCaptor.capture());
            Project savedProject = projectCaptor.getValue();

            assertEquals("NEW_NAME", savedProject.getName());
            assertEquals("NEW_CUSTOMER", savedProject.getCustomer());
            assertEquals(newDate, savedProject.getFinishingDate());
            assertEquals(savedProject, result);
        }

        @Test
        @DisplayName("Thành công: Chỉ cập nhật các trường không null trong DTO")
        void update_WhenPartialDto_ShouldOnlyUpdateNonNullFields() {
            // [Given]
            Project existing = new Project("KEEP_NAME", LocalDate.of(2025, 1, 1));
            existing.setCustomer("KEEP_CUSTOMER");

            ProjectDto partialDto = new ProjectDto();
            partialDto.setName("ONLY_NEW_NAME"); // finishingDate và customer là null

            when(projectRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

            // [When]
            projectService.update(partialDto, 1L);

            // [Then]
            verify(projectRepository).save(projectCaptor.capture());
            Project saved = projectCaptor.getValue();

            assertEquals("ONLY_NEW_NAME", saved.getName());
            assertEquals("KEEP_CUSTOMER", saved.getCustomer(), "Customer không được bị ghi đè null");
            assertEquals(LocalDate.of(2025, 1, 1), saved.getFinishingDate(), "Date không được bị ghi đè null");
        }
    }
}
```

---

## 14. Sổ tay bắt bệnh: Các lỗi thường gặp & Cách xử lý tận gốc

### 🔴 Lỗi 1: `NullPointerException` khi gọi phương thức của Mock
* **Nguyên nhân:** Mock chưa được khởi tạo (biến Mock bị null).
* **Khắc phục:** Đảm bảo class có `@ExtendWith(MockitoExtension.class)` hoặc đã gọi `MockitoAnnotations.openMocks(this)`.

---

### 🔴 Lỗi 2: `InvalidUseOfMatchersException`
* **Nguyên nhân:** Đặt matcher `any()` bên ngoài hàm stub hoặc trộn lẫn matcher với giá trị raw (xem [Mục 9.3](#93-quy-tắc-bất-di-bất-dịch-của-matchers)).
* **Khắc phục:** Bọc các giá trị cụ thể bằng `eq("value")`.

---

### 🔴 Lỗi 3: `UnnecessaryStubbingException`
* **Nguyên nhân:** Từ Mockito 3+, cơ chế Strict Stubbing mặc định bật. Nếu bạn viết `when(repo.count()).thenReturn(5L)` nhưng trong test case không hề gọi `count()`, Mockito sẽ fail test để cảnh báo code thừa.
* **Khắc phục:**
  1. Xóa dòng stub không dùng đến.
  2. Hoặc nếu method đó dùng chung cho nhiều test qua `@BeforeEach`, dùng `lenient()`:
     ```java
     lenient().when(projectRepository.count()).thenReturn(5L);
     ```

---

### 🔴 Lỗi 4: `Cannot mock final class / method`
* **Nguyên nhân:** Mockito phiên bản cũ (< 2.x) không mock được class/method `final`.
* **Khắc phục:** Mockito 5.x mặc định đã hỗ trợ mock final classes thông qua `mock-maker-inline`. Đảm bảo dùng Mockito bản mới.

---
*Created for Training & Onboarding Reference.*
