package com.growth;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class GrowthApplicationTests {

    @Test
    void run() {
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            GrowthApplication.main(new String[0]);

            mocked.verify(() -> {
                SpringApplication.run(GrowthApplication.class, new String[0]);
            });
        }
    }
}
