package br.com.fourteca;

import br.com.fourteca.config.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@WithMockCustomUser
class FourtecaApplicationTests {

	@Test
	void contextLoads() {
	}

}
