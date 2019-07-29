package io.github.cepr0.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@RunWith(SpringRunner.class)
@ContextConfiguration
public class TagsFactoryBeanTest {

	@Autowired
	@Qualifier("greetingWords")
	private Map<String, Supplier<String>> greetingWords;

	@Autowired
	@Qualifier("evenNums")
	private Map<String, Supplier<Integer>> evenNums;

	@Autowired
	@Qualifier("evens")
	private Map<String, Supplier<?>> evens;

	@Test
	public void greetingWords() {
		assertThat(greetingWords).hasSize(2).containsOnlyKeys("hi", "hello");
		assertThat(greetingWords.get("hi").get()).isEqualTo("hi");
		assertThat(greetingWords.get("hello").get()).isEqualTo("hello");
	}

	@Test
	public void evenNums() {
		assertThat(evenNums).hasSize(2).containsOnlyKeys("two", "ten");
		assertThat(evenNums.get("two").get()).isEqualTo(2);
		assertThat(evenNums.get("ten").get()).isEqualTo(10);
	}

	@Test
	public void evens() {
		assertThat(evens).hasSize(4).containsOnlyKeys("hi", "by", "two", "ten");
	}

	@Test
	public void whenTagsOrTypeAreMissingThenException() {

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() ->
						TagsFactoryBean.<Supplier>builder()
								.type(Supplier.class)
								.build()
				)
				.withMessage("Tags must not be null!");

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() ->
						TagsFactoryBean.<Supplier>builder()
								.tags("even")
								.build()
				)
				.withMessage("Type must not be null!");
	}

	@Configuration
	static class Config {

		@Bean
		public TagsFactoryBean greetingWords() {
			return TagsFactoryBean.<Supplier>builder()
					.tags("greeting")
					.type(Supplier.class)
					.generics(String.class)
					.build();
		}

		@Bean
		public TagsFactoryBean evenNums() {
			return TagsFactoryBean.<Supplier>builder()
					.tags("even")
					.type(Supplier.class)
					.generics(Integer.class)
					.build();
		}

		@Bean
		public TagsFactoryBean evens() {
			return TagsFactoryBean.<Supplier>builder()
					.tags("even")
					.type(Supplier.class)
					.build();
		}

		@Tags({"greeting", "2letters", "even"})
		@Bean
		public Supplier<String> hi() {
			return () -> "hi";
		}

		@Tags({"parting", "2letters", "even"})
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

		@Tags({"greeting", "odd"})
		@Bean
		public Supplier<Integer> one() {
			return () -> 1;
		}

		@Tags({"greeting", "even"})
		@Bean
		public Supplier<Integer> two() {
			return () -> 2;
		}

		@Tags({"greeting", "even"})
		@Bean
		public Supplier<Integer> ten() {
			return () -> 10;
		}

		@Tags({"greeting", "odd"})
		@Bean
		public Supplier<Integer> eleven() {
			return () -> 11;
		}
	}
}