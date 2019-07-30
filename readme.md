# An example of Spring FactoryBean implementation

Mark your beans with [@Tags](src/main/java/io/github/cepr0/demo/Tags.java) annotation:

```java
@Tags({"greeting", "2letters"})
@Bean
public Supplier<String> hi() {
   return () -> "hi";
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

@Tags("other")
@Bean
public Supplier<String> other() {
   return () -> "other";
}
```
Prepare [TagsFactoryBean](src/main/java/io/github/cepr0/demo/TagsFactoryBean.java):

```java
@Bean
public TagsFactoryBean words() {
   return TagsFactoryBean.<Supplier>builder()
         .tags("2letters", "other")
         .type(Supplier.class)
         .generics(String.class)
         .build();
}
``` 

Here `tags` is an array of desired tags whose beans should be selected, 
`type` is a selected beans type, and `generics` is an array of generic types of the beans. 
The last parameter is optional and should be used only if your beans are generic.


And finally get desired beans (`hi`, `by`, `other`) in the Map 
(bean names are keys of the Map, bean instances - its values):

```java
@Autowired
@Qualifier("words")
private Map<String, Supplier<String>> beans;
```
 
(Using `@Qualifier` annotation is mandatory, 
otherwise Spring will inject all beans of `Supplier<String>` type) 
 
Related to this [SO post](https://stackoverflow.com/a/57169506).