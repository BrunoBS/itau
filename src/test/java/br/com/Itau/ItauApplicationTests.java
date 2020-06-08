package br.com.Itau;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.itau.ItauApplication;

@SpringBootTest
class ItauApplicationTests {

	@Test
	public void mainTest() {
		ItauApplication.main(new String[] {});
	}

}
