package com.nqtam.Lession01_spingboot.controller; // Nên đặt trong gói gốc hoặc gói test

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath; // Import quan trọng để kiểm tra JSON
import static org.hamcrest.Matchers.is; // Import cho các giá trị so sánh

@SpringBootTest
@AutoConfigureMockMvc
public class DemoControllerTest { // Nên đổi tên lớp thành DemoControllerTest

    @Autowired
    private MockMvc mvc;

    @Test
    public void demoApi_ShouldReturnJsonWithMessage() throws Exception {
        // Giả định rằng obj.runAll() trả về một chuỗi nào đó, ví dụ: "Test success"
        // Bạn cần thay thế "Expected output from runAll()" bằng kết quả THỰC TẾ của obj.runAll()
        String expectedMessage = "Expected output from runAll()";
        System.out.println("test api");
        mvc.perform(MockMvcRequestBuilders.get("/demo")
                        .accept(MediaType.APPLICATION_JSON)) // Yêu cầu trả về JSON

                // 1. Kiểm tra HTTP Status: Phải là 200 OK
                .andExpect(status().isOk())

                // 2. Kiểm tra Content Type: Phải là application/json
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // 3. Kiểm tra Nội dung JSON: Dùng jsonPath để duyệt qua JSON
                // $.message là đường dẫn đến key 'message' trong JSON trả về.
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }
}