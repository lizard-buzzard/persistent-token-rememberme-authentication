### Persistent Token Approach for HttpSecurity rememberMe() Authentication ###
The purpose of this work was to understand '__remember me__' HttpSecurity configuration of WebSecurityConfigurerAdapter which is based on __Persistent Token Approach__.

#### A List of References ####
There are a lot of articles and code examples in the Internet which didn't give me enough clear understanding. 

Here is a list of references that it's worth to look through before:
* [Spring Security Reference. 18.3 Persistent Token Approach](https://docs.spring.io/spring-security/site/docs/5.0.0.BUILD-SNAPSHOT/reference/htmlsingle/#remember-me-persistent-token)
* [Spring Security Remember Me](https://www.baeldung.com/spring-security-remember-me)
* [Lesson 3: Remember Me with Persistence](https://courses.baeldung.com/courses/learn-spring-security-the-starter-class/lectures/924437)
* [Spring Security - Remember Me with Persistence (LSS - Module 3 - Lesson 3)](https://www.youtube.com/watch?v=1t-eV996jsw&list=PLjXUjSTUHs0SjCsFDcWI8Sq06Cknk1vkk&t=0s&index=4)
* [Spring Security - Remember-Me Authentication using Persistent Token](https://www.logicbig.com/tutorials/spring-framework/spring-security/persist-token-remember-me.html)
* [Spring Security 5 - Remember-Me authentication example](https://www.boraji.com/spring-security-5-remember-me-authentication-example)
* [Spring Security Remember Me Example](https://www.mkyong.com/spring-security/spring-security-remember-me-example/)
* [Baeldung/spring-security-registration](https://github.com/Baeldung/spring-security-registration)
* [Spring Security Persisted Remember Me Example Project](https://github.com/eugenp/tutorials/tree/master/spring-security-mvc-persisted-remember-me#readme)
* [Spring Security – Persistent Remember Me](https://www.baeldung.com/spring-security-persistent-remember-me)
* [Spring Security - A Simple Remember Me Flow (LSS - Module 3 - Lesson 1)](https://www.youtube.com/watch?v=9HgREWvDsYk)
* [Spring Boot running data.sql before creating entities in presence of schema.sql](https://github.com/spring-projects/spring-boot/issues/9048)
* ["Remember Me" in Spring Security Example](https://www.concretepage.com/spring/spring-security/remember-me-in-spring-security-example#database)

#### Goals of the work ####
The goals of the work were:
* to have a code of the example based on __Spring Boot__ rapid application development platform;
* to use a pure Spring __Annotation-based__ configuration for this task;
* to use __Apache Maven__ as a project build automation tool;
* to use __Tomcat as Embedded Web Server__ feature provided by Spring Boot using Maven;
* to reach a transparency of '__remember me__' HttpSecurity configuration for __Persistent Token Approach__.

An example, described in the article ["Remember Me" in Spring Security Example](https://www.concretepage.com/spring/spring-security/remember-me-in-spring-security-example#database) (and the code attached to this article), was taken as a starting point of the development.

#### Development ####

##### 'Mavenizing' of the project #####

In order to 'mavenize' the project, a __pom.xml__ file with Spring Boot dependencies was created. For detailed list of dependencies used please refer to the __pom.xml__ file.
Make sure to include in the dependency list dependencies which prevent the [JSP file not rendering in Spring Boot web application](https://stackoverflow.com/questions/20602010/jsp-file-not-rendering-in-spring-boot-web-application) issue (for me __scope provided__ commented works):
```xml
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
    <!--<scope>provided</scope>-->
</dependency>
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jstl</artifactId>
    <!--<scope>provided</scope>-->
</dependency>

```
##### Spring MVC Annotation based configuration #####
1. Create a package structure starting from com.lizardbuzzard

2. Create a class which starts the application to run:
    ```java
    @SpringBootApplication
    public class Apps {
        public static void main(String[] args) {
            SpringApplication.run(Apps.class, args);
        }
    }
    ```
2. 


as we use Spring Boot we can avoid of using of SecurityWebApplicationInitializer




##### DataBase configuration #####






 
[Spring Security Form Login](https://www.baeldung.com/spring-security-login)
The default URL where the Spring Login will POST to trigger the authentication process is /login which can be overridden via the loginProcessingUrl method. This HttpSecurity configuration 
```
loginProcessingUrl("/loginFormPostTo").

```
correlates with customLogin.html form's action attribute 
```html
<form name="myform" th:action="@{/loginFormPostTo}" method="POST" onsubmit="return validateFormFields();">

```



spring.jpa.hibernate.ddl-auto=update

#### ExpressionUrlAuthorizationConfigurer.AuthorizedUrl configuration ####
Code fragment below shows two variants of AuthorizedUrl configuration
```
http.authorizeRequests()
        .antMatchers("/app/homepage/adminconsole/**").access("hasRole('ROLE_ADMIN')");
http.authorizeRequests()
        .antMatchers("/app/homepage/user/**", "/app/redirect").hasRole("USER");  // "ROLE_" adds automatically
```
[Spring Security Java Config Preview: Web Security](https://spring.io/blog/2013/07/03/spring-security-java-config-preview-web-security/):
"When creating our users ... , we do not specify “ROLE_” as we would with the XML configuration. Since this convention is so common, the “roles” method automatically adds “ROLE_” for you. If you did not want “ROLE_” added you could use the authorities method instead."
[Intro to Spring Security Expressions](https://www.baeldung.com/spring-security-expressions):
"So hasAuthority(‘ROLE_ADMIN’) is similar to hasRole(‘ADMIN’) because the ‘ROLE_‘ prefix gets added automatically."
But the good thing about using authorities is that we don’t have to use the ROLE_ prefix at all.

#### User Page ####
[Adding Static Resources (css, JavaScript, Images) to Thymeleaf](https://memorynotfound.com/adding-static-resources-css-javascript-images-thymeleaf/)

Html page
```html
<div class="row" >
    <div class="col-sm-3">
        <!--<img th:src="@{/images/funnycat.png}" >-->
        <img src="/images/funnycat.png" >
    </div>
    <div class="col-sm-9">
        <iframe th:src="@{/app/redirect}" style="width: 100%; height: 500px;" frameborder="no"/>
    </div>
</div>
```
In order to get static resource (image) on html page
```
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/resources/**", "/images/**")
            .addResourceLocations("/", "/resources/", "classpath:/static/images/");
}
```
Controller's method servs redirect to external site URL
```html
@RequestMapping("/redirect")
public String redirectPage() {
    return "redirect:https://www.w3.org/";
}
```



In order to implement login/authentication with Spring Security, we need to implement the UserDetailsService interface. 
The UserDetailsService interface is used to retrieve user-related data. Spring Security provides a UserDetailsService interface to lookup the username, password and GrantedAuthorities for any given user. See
* [Spring Security: Authentication with a Database-backed UserDetailsService](https://www.baeldung.com/spring-security-authentication-with-a-database)
* [SPRING BOOT WEB APPLICATION, PART 6 – SPRING SECURITY WITH DAO AUTHENTICATION PROVIDER](https://springframework.guru/spring-boot-web-application-part-6-spring-security-with-dao-authentication-provider/)
