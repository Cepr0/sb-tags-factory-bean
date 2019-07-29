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

	public Application(@Qualifier("greetings") Map<String, Supplier<String>> suppliers) {
		System.out.println(suppliers.keySet());
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Configuration
	static class Config {

		@Bean
		public TagsFactoryBean greetings() {
			return new TagsFactoryBean("greeting");
		}

		@Tags({"greeting", "2letters"})
		@Bean
		public Supplier<String> hi() {
			return new Supplier<String>() {
				@Override
				public String get() {
					return "hi";
				}
			};
		}

		@Tags({"parting", "2letters"})
		@Bean
		public Supplier<String> by() {
			return new Supplier<String>() {
				@Override
				public String get() {
					return "by";
				}
			};
		}

		@Tags("greeting")
		@Bean
		public Supplier<String> hello() {
			return new Supplier<String>() {
				@Override
				public String get() {
					return "hello";
				}
			};
		}

		@Tags("parting")
		@Bean
		public Supplier<String> goodbye() {
			return new Supplier<String>() {
				@Override
				public String get() {
					return "goodbye";
				}
			};
		}
	}
}
