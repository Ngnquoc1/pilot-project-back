# 🚀 JUNIT 5 (JUPITER) CHEATSHEET — CẨM NANG TOÀN DIỆN

> **Tài liệu tham khảo nhanh & đầy đủ nhất về JUnit 5 dành cho Java & Spring Boot Developers.**

---

## 📌 MỤC LỤC
1. [Tổng quan Kiến trúc & Cài đặt (Maven / Gradle)](#1-tổng-quan-kiến-trúc--cài-đặt)
2. [Bảng tra cứu Annotations cốt lõi](#2-bảng-tra-cứu-annotations-cốt-lõi)
3. [Vòng đời kiểm thử (Test Lifecycle)](#3-vòng-đời-kiểm-thử-test-lifecycle)
4. [Tất cả Assertions (Khẳng định kết quả)](#4-tất-cả-assertions)
5. [Assumptions (Kiểm tra điều kiện tiền đề)](#5-assumptions)
6. [Kiểm thử Ngoại lệ (Exception) & Thời gian chạy (Timeout)](#6-kiểm-thử-ngoại-lệ--timeout)
7. [Parameterized Tests (Kiểm thử hướng dữ liệu - Data-Driven)](#7-parameterized-tests-data-driven)
8. [Nested Tests (@Nested) & Repeated Tests (@RepeatedTest)](#8-nested-tests--repeated-tests)
9. [Tagging, Phân nhóm & Thứ tự chạy (@Order)](#9-tagging-phân-nhóm--thứ-tự-chạy)
10. [Tích hợp Mockito (Unit Testing)](#10-tích-hợp-mockito-unit-testing)
11. [Tích hợp Spring Boot (Integration Testing)](#11-tích-hợp-spring-boot-integration-testing)
12. [Bảng so sánh chuyển đổi nhanh JUnit 4 ➔ JUnit 5](#12-bảng-chuyển-đổi-nhanh-junit-4--junit-5)

---

## 1. Tổng quan Kiến trúc & Cài đặt

### Kiến trúc 3 tầng của JUnit 5
$$\text{JUnit 5} = \text{JUnit Platform (Nền tảng chạy test)} + \text{JUnit Jupiter (API viết test mới)} + \text{JUnit Vintage (Chạy test JUnit 3/4 cũ)}$$

### Cấu hình Maven (`pom.xml`)

```xml
<!-- 1. Dành cho dự án Spring Boot (Đã tích hợp sẵn JUnit 5, Mockito, AssertJ) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- 2. Dành cho dự án Java thuần -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
</dependency>
```

---

## 2. Bảng tra cứu Annotations cốt lõi

| Annotation | Mô tả |
| :--- | :--- |
| `@Test` | Đánh dấu một method là test case. |
| `@BeforeEach` | Chạy trước **mỗi** `@Test` trong class (thay thế `@Before` của JUnit 4). |
| `@AfterEach` | Chạy sau **mỗi** `@Test` trong class (thay thế `@After` của JUnit 4). |
| `@BeforeAll` | Chạy 1 lần duy nhất trước toàn bộ test trong class (phải là `static`, thay thế `@BeforeClass`). |
| `@AfterAll` | Chạy 1 lần duy nhất sau khi toàn bộ test kết thúc (phải là `static`, thay thế `@AfterClass`). |
| `@DisplayName("...")` | Đặt tên hiển thị dễ đọc cho test method/class trên giao diện IDE / Báo cáo. |
| `@Disabled("Lý do")` | Bỏ qua (skip) không chạy test case này (thay thế `@Ignore`). |
| `@Nested` | Tạo class test lồng nhau để phân chia ngữ cảnh (BDD style). |
| `@Tag("smoke")` | Gán tag phân loại test (lọc chạy test theo tag trong CI/CD). |
| `@Timeout(5)` | Đặt giới hạn thời gian tối đa chạy test (tính theo giây hoặc đơn vị tùy chọn). |
| `@RepeatedTest(10)` | Tự động lặp lại test case $N$ lần. |
| `@ParameterizedTest` | Đánh dấu method là kiểm thử tham số hóa (Data-Driven Test). |
| `@ExtendWith(...)` | Đăng ký extension tùy chỉnh (VD: `MockitoExtension.class`, `SpringExtension.class`). |
| `@TempDir` | Tự động tạo thư mục tạm thời và tự xóa sạch sau khi test xong. |

---

## 3. Vòng đời kiểm thử (Test Lifecycle)

```java
import org.junit.jupiter.api.*;

@DisplayName("Vòng đời Test trong JUnit 5")
class LifecycleDemoTest {

    @BeforeAll
    static void beforeAll() {
        System.out.println(">> @BeforeAll: Khởi tạo resource tĩnh (chạy 1 lần duy nhất)");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println(" > @BeforeEach: Reset mock/state trước mỗi test");
    }

    @Test
    @DisplayName("Test case số 1")
    void testMethod1() {
        System.out.println("   [RUN] Test 1");
    }

    @Test
    @DisplayName("Test case số 2")
    void testMethod2() {
        System.out.println("   [RUN] Test 2");
    }

    @AfterEach
    void afterEach() {
        System.out.println(" > @AfterEach: Dọn dẹp sau mỗi test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println(">> @AfterAll: Đóng resource toàn cục (chạy 1 lần)");
    }
}
```

---

## 4. Tất cả Assertions

> ⚠️ **Quy tắc thứ tự tham số trong JUnit 5:**  
> `assertEquals(expected, actual, "Thông báo lỗi khi fail")` *(Giá trị kỳ vọng trước, Giá trị thực tế sau, Message lỗi ở cuối cùng).*

```java
import org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;

class AssertionsDemoTest {

    @Test
    void testAllStandardAssertions() {
        // 1. So sánh bằng / không bằng
        assertEquals(10, 5 + 5, "5 + 5 phải bằng 10");
        assertNotEquals(0, 10);

        // 2. Boolean
        assertTrue(10 > 5, "10 phải lớn hơn 5");
        assertFalse(10 < 5);

        // 3. Null / Not Null
        assertNotNull(new Object());
        assertNull(null);

        // 4. So sánh cùng địa chỉ ô nhớ (Same reference)
        String str = "JUnit";
        assertSame(str, str);
        assertNotSame(new String("JUnit"), new String("JUnit"));

        // 5. So sánh Mảng (Array)
        int[] expectedArr = { 1, 2, 3 };
        int[] actualArr = { 1, 2, 3 };
        assertArrayEquals(expectedArr, actualArr);

        // 6. So sánh Danh sách (Iterable / Collection)
        List<String> list1 = List.of("A", "B");
        List<String> list2 = List.of("A", "B");
        assertIterableEquals(list1, list2);

        // 7. Chủ động đánh dấu fail
        if (false) {
            fail("Điều kiện không hợp lệ!");
        }
    }

    @Test
    @DisplayName("Grouped Assertions với assertAll (Tương tự Soft Assert)")
    void testGroupedAssertions() {
        // Chạy TẤT CẢ assertions bên trong dù có 1 cái fail
        assertAll("Kiểm tra thông tin User",
            () -> assertEquals("john", "john"),
            () -> assertTrue(25 >= 18, "Tuổi phải >= 18"),
            () -> assertNotNull("john@example.com")
        );
    }
}
```

---

## 5. Assumptions

Dùng để kiểm tra điều kiện môi trường. Nếu **Assumption không thỏa mãn**, test sẽ tự động bị **SKIP (Bỏ qua)** chứ không bị tính là FAIL.

```java
import static org.junit.jupiter.api.Assumptions.*;
import org.junit.jupiter.api.Test;

class AssumptionsDemoTest {

    @Test
    void testOnlyOnDevEnvironment() {
        String env = System.getenv("ENV");
        // Chỉ chạy test này nếu đang ở môi trường DEV
        assumeTrue("DEV".equals(env), "Bỏ qua vì không phải môi trường DEV");

        // Code test bên dưới chỉ chạy khi assumeTrue pass
        assertEquals(2, 1 + 1);
    }

    @Test
    void testWithAssumingThat() {
        String os = System.getProperty("os.name");
        // Chỉ chạy đoạn code bên trong nếu là Windows, các đoạn khác vẫn chạy
        assumingThat(os.startsWith("Windows"), () -> {
            System.out.println("Đang chạy trên Windows");
        });
    }
}
```

---

## 6. Kiểm thử Ngoại lệ & Timeout

### 6.1. Kiểm tra Exception (`assertThrows`)
```java
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ExceptionDemoTest {

    @Test
    void testExceptionThrownAndMessage() {
        // 1. Kiểm tra đúng kiểu Exception được ném ra
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> {
                throw new IllegalArgumentException("ID không được âm: -1");
            },
            "Phải ném ra IllegalArgumentException"
        );

        // 2. Kiểm tra chi tiết message của Exception
        assertTrue(exception.getMessage().contains("-1"));
    }

    @Test
    void testDoesNotThrow() {
        // Đảm bảo khối code chạy mượt mà không ném ra bất kỳ exception nào
        assertDoesNotThrow(() -> {
            int result = 100 / 2;
        });
    }
}
```

### 6.2. Kiểm tra Timeout
```java
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

class TimeoutDemoTest {

    // Cách 1: Dùng assertTimeout (chờ code chạy xong rồi mới check thời gian)
    @Test
    void testTimeout() {
        assertTimeout(Duration.ofMillis(500), () -> {
            Thread.sleep(100); // Pass vì < 500ms
        });
    }

    // Cách 2: Dùng assertTimeoutPreemptively (ngắt thread ngay lập tức khi hết giờ)
    @Test
    void testTimeoutPreemptively() {
        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
            Thread.sleep(200);
        });
    }

    // Cách 3: Annotation @Timeout cho toàn bộ method
    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testWithAnnotationTimeout() throws InterruptedException {
        Thread.sleep(500);
    }
}
```

---

## 7. Parameterized Tests (Data-Driven)

Yêu cầu dependency: `junit-jupiter-params`.

### 7.1. `@ValueSource` (Tham số đơn)
```java
@ParameterizedTest
@ValueSource(ints = { 2, 4, 6, 8 })
void testIsEven(int number) {
    assertEquals(0, number % 2);
}
```

### 7.2. `@NullSource`, `@EmptySource`, `@NullAndEmptySource`
```java
@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = { "  ", "\t", "\n" })
void testIsBlank(String input) {
    assertTrue(input == null || input.trim().isEmpty());
}
```

### 7.3. `@EnumSource`
```java
enum Role { ADMIN, USER, GUEST }

@ParameterizedTest
@EnumSource(value = Role.class, names = { "ADMIN", "USER" }) // Lọc riêng 2 role
void testAuthorizedRoles(Role role) {
    assertNotNull(role);
}
```

### 7.4. `@CsvSource` & `@CsvFileSource` (Dữ liệu bảng nhiều cột)
```java
@ParameterizedTest(name = "[{index}] {0} + {1} phải bằng {2}")
@CsvSource({
    "1,   2,   3",
    "10, -5,   5",
    "0,   0,   0"
})
void testAdditionFromCsv(int a, int b, int expected) {
    assertEquals(expected, a + b);
}

// Đọc từ file src/test/resources/data.csv
@ParameterizedTest
@CsvFileSource(resources = "/data.csv", numLinesToSkip = 1)
void testWithCsvFile(String name, int age, boolean isAdult) {
    assertEquals(isAdult, age >= 18);
}
```

### 7.5. `@MethodSource` (Cung cấp Object phức tạp)
```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

class MethodSourceDemoTest {

    static Stream<Arguments> provideUsers() {
        return Stream.of(
            Arguments.of("admin", true),
            Arguments.of("guest", false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideUsers")
    void testUserPermissions(String username, boolean isAdmin) {
        assertEquals(isAdmin, "admin".equals(username));
    }
}
```

---

## 8. Nested Tests & Repeated Tests

### 8.1. `@Nested` (Phân cấp ngữ cảnh rõ ràng)
```java
import org.junit.jupiter.api.*;

@DisplayName("Kiểm thử ProjectService")
class ProjectServiceNestedTest {

    @Nested
    @DisplayName("Khi Project tồn tại trong Database")
    class WhenProjectExists {
        @Test
        @DisplayName("Phải lấy được thông tin chi tiết")
        void shouldReturnProject() { ... }

        @Test
        @DisplayName("Phải cập nhật thành công")
        void shouldUpdateSuccessfully() { ... }
    }

    @Nested
    @DisplayName("Khi Project KHÔNG tồn tại")
    class WhenProjectDoesNotExist {
        @Test
        @DisplayName("Phải ném ra ngoại lệ ProjectNotFoundException")
        void shouldThrowException() { ... }
    }
}
```

### 8.2. `@RepeatedTest` (Lặp lại test case)
```java
@RepeatedTest(value = 3, name = "Lần chạy {currentRepetition} / {totalRepetitions}")
void testFlakyOperation(RepetitionInfo info) {
    System.out.println("Đang chạy lần: " + info.getCurrentRepetition());
    assertTrue(Math.random() > 0.001);
}
```

---

## 9. Tagging, Phân nhóm & Thứ tự chạy

### 9.1. `@Tag` (Lọc test khi build)
```java
@Tag("smoke")
@Test
void criticalLoginTest() { ... }

@Tag("slow")
@Tag("integration")
@Test
void generateBigReportTest() { ... }
```

### 9.2. `@TestMethodOrder` & `@Order` (Quy định thứ tự thực thi)
```java
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.*;

@TestMethodOrder(OrderAnnotation.class)
class OrderedTest {

    @Test
    @Order(1)
    void step1_CreateAccount() { System.out.println("1. Tạo tài khoản"); }

    @Test
    @Order(2)
    void step2_Login() { System.out.println("2. Đăng nhập"); }

    @Test
    @Order(3)
    void step3_Checkout() { System.out.println("3. Thanh toán"); }
}
```

---

## 10. Tích hợp Mockito (Unit Testing)

Mô hình chuẩn viết **Unit Test thuần túy** cho tầng Service:

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Kích hoạt Mockito cho JUnit 5
class ProjectServiceUnitTest {

    @Mock
    private ProjectRepository projectRepository; // Tạo mock object

    @InjectMocks
    private ProjectServiceImpl projectService; // Tự động inject mock vào service

    @Test
    void testFindById_Success() {
        // 1. Given (Chuẩn bị hành vi giả lập)
        Project mockProject = new Project("EFV", LocalDate.now());
        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));

        // 2. When (Thực thi)
        Project result = projectService.findById(1L);

        // 3. Then (Kiểm tra kết quả & xác thực số lần gọi hàm)
        assertNotNull(result);
        assertEquals("EFV", result.getName());
        verify(projectRepository, times(1)).findById(1L);
    }
}
```

---

## 11. Tích hợp Spring Boot (Integration Testing)

Mô hình chuẩn viết **Integration Test** với Database (H2) thật:

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Tự động load full Spring Context (đã gồm SpringExtension)
class ProjectRepositoryIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void testCountAllProjects() {
        long count = projectRepository.count();
        assertTrue(count >= 5, "Database phải có ít nhất 5 bản ghi seed ban đầu");
    }
}
```

---

## 12. Bảng chuyển đổi nhanh JUnit 4 ➔ JUnit 5

| Tính năng | JUnit 4 | JUnit 5 |
| :--- | :--- | :--- |
| **Package** | `org.junit.*` | `org.junit.jupiter.api.*` |
| **Test Method** | `@Test` | `@Test` |
| **Setup trước mỗi test** | `@Before` | `@BeforeEach` |
| **Teardown sau mỗi test** | `@After` | `@AfterEach` |
| **Setup trước cả class** | `@BeforeClass` | `@BeforeAll` |
| **Teardown sau cả class** | `@AfterClass` | `@AfterAll` |
| **Bỏ qua test** | `@Ignore` | `@Disabled` |
| **Tên hiển thị** | *Không hỗ trợ* | `@DisplayName("...")` |
| **Bắt Exception** | `@Test(expected = ...)` | `assertThrows(Exception.class, () -> ...)` |
| **Kiểm tra Timeout** | `@Test(timeout = 1000)` | `assertTimeout(Duration.ofMillis(1000), ...)` |
| **Tích hợp Mockito** | `@RunWith(MockitoJUnitRunner.class)` | `@ExtendWith(MockitoExtension.class)` |
| **Tích hợp Spring Boot** | `@RunWith(SpringRunner.class)` | `@SpringBootTest` *(không cần @RunWith)* |
| **Gom nhóm assertions** | *Không hỗ trợ* | `assertAll(...)` |
| **Data-Driven test** | `@RunWith(Parameterized.class)` | `@ParameterizedTest` + `@ValueSource` / `@CsvSource` |

---
*Created for Training & Onboarding Reference.*
