package com.hamy.devflow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.mail.test-connection=false"
})
@Import(MySqlTestConfig.class)
class DevFlowApplicationTests {

    @Test
    void contextLoads() {
    }
}