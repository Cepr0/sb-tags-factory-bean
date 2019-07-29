package io.github.cepr0.demo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Supplier;

@SpringBootApplication
public class Application {

	public Application(@Qualifier("greetings") Map<String, Supplier<String>> beans) {
		System.out.println(beans.keySet());
		Supplier<String> hi = beans.get("hi");
		System.out.println(hi.get());
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Configuration
	static class Config {

		@Bean
		public TagsFactoryBean greetings() {
			return TagsFactoryBean.<Supplier>builder()
					.tags("greeting")
					.type(Supplier.class)
					.generics(String.class)
					.build();
		}

		@Tags({"greeting", "2letters"})
		@Bean
		public Supplier<String> hi() {
			return () -> "hi";
		}

		@Tags("greeting")
		@Bean
		public Supplier<Integer> qu() {
			return () -> 0;
		}

		@Tags({"parting", "2letters"})
		@Bean
		public Supplier<String> by() {
			return () -> "by";
		}

		@Tags("greeting")
		@Bean
		public Supplier<String> hello() {
			return () -> "hello";
		}

		@Tags("parting")
		@Bean
		public Supplier<String> goodbye() {
			return () -> "goodbye";
		}
	}
}
