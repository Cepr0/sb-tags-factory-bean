package io.github.cepr0.demo;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.*;

public class TagsFactoryBean<T> implements FactoryBean<Map<String, T>>, ApplicationContextAware {

	private ApplicationContext ctx;
	private final List<String> tags;

	private final Class<T> type;
	private final Class<?>[] generics;

	private TagsFactoryBean(@NonNull Class<T> type, Class<?>[] generics, @NonNull String... tags) {

		Assert.notNull(type, "Type must not be null!");
		Assert.notNull(tags, "Tags must not be null!");

		this.type = type;
		this.generics = generics;
		this.tags = Arrays.asList(tags);
	}

	public static <T> Builder<T> builder() {
		return new Builder<>();
	}

	@Override
	public Map<String, T> getObject() {

		Map<String, T> beans = new HashMap<>();

		ResolvableType rt;

		if (generics != null) {
			rt = ResolvableType.forClassWithGenerics(type, generics);
		} else {
			rt = ResolvableType.forClass(type);
		}

		String[] beanNames = ctx.getBeanNamesForType(rt);
		for (String beanName : beanNames) {
			BeanDefinition bd = ((BeanDefinitionRegistry) ctx).getBeanDefinition(beanName);
			if (bd.getSource() instanceof AnnotatedTypeMetadata) {
				AnnotatedTypeMetadata metadata = (AnnotatedTypeMetadata) bd.getSource();
				String annName = Tags.class.getName();
				if (metadata.isAnnotated(annName)) {
					Map<String, Object> attributes = metadata.getAnnotationAttributes(annName);
					if (attributes != null) {
						String[] annTags = (String[]) attributes.get("value");
						if (annTags != null) {
							List<String> tagList = new ArrayList<>(Arrays.asList(annTags));
							tagList.retainAll(tags);
							if (!tagList.isEmpty()) {
								//noinspection unchecked
								beans.put(beanName, (T) ctx.getBean(beanName));
							}
						}
					}
				}
			}
		}
		return beans;
	}

	@Override
	public Class<?> getObjectType() {
		return Map.class;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}

	public static class Builder<T> {

		private String[] tags;
		private Class<T> type;
		private Class<?>[] generics;

		Builder() {
		}

		public Builder<T> tags(String... tags) {
			this.tags = tags;
			return this;
		}

		public Builder<T> type(Class<T> type) {
			this.type = type;
			return this;
		}

		public Builder<T> generics(Class<?>... generics) {
			this.generics = generics;
			return this;
		}

		public TagsFactoryBean<T> build() {
			return new TagsFactoryBean<>(type, generics, tags);
		}
	}
}
