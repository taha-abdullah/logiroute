package logiroute.logiroute_delivery;

import org.springframework.boot.SpringApplication;

public class TestLogirouteDeliveryApplication {

	public static void main(String[] args) {
		SpringApplication.from(LogirouteDeliveryApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
