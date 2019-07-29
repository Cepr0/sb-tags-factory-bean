package io.github.cepr0.demo;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.*;
import java.util.function.Supplier;

public class TagsFactoryBean implements FactoryBean<Map<String, Supplier<String>>>, ApplicationContextAware {

	private ApplicationContext ctx;
	private final List<String> desiredTags;

	public TagsFactoryBean(String... desiredTags) {
		this.desiredTags = Arrays.asList(desiredTags);
	}

	@Override
	public Map<String, Supplier<String>> getObject() throws Exception {

		Map<String, Supplier<String>> beans = new HashMap<>();
		ResolvableType stringSupplierType = ResolvableType.forClassWithGenerics(Supplier.class, String.class);

		String[] beanNames = ctx.getBeanNamesForType(stringSupplierType);
		for (String beanName : beanNames) {
			Object bean = ctx.getBean(beanName);
			if (stringSupplierType.isInstance(bean)) {
				BeanDefinition bd = ((BeanDefinitionRegistry) ctx).getBeanDefinition(beanName);
				if (bd.getSource() instanceof AnnotatedTypeMetadata ) {
					AnnotatedTypeMetadata metadata = (AnnotatedTypeMetadata) bd.getSource();
					Map<String, Object> attributes = metadata.getAnnotationAttributes(Tags.class.getName());
					if (attributes != null) {
						String[] tags = (String[]) attributes.get("value");
						if (tags != null) {
							List<String> tagList = new ArrayList<>(Arrays.asList(tags));
							tagList.retainAll(desiredTags);
							if (!tagList.isEmpty()) {
								//noinspection unchecked
								beans.put(beanName, (Supplier<String>) bean);
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
}
