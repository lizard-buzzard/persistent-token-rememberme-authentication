# Persistent Token Approach for HttpSecurity rememberMe() Authentication #
The purpose of this work was to understand '__remember me__' HttpSecurity configuration of WebSecurityConfigurerAdapter which is based on __Persistent Token Approach__.

## A List of References ##
There are a lot of articles and code examples in the Internet which haven't given me enough clear understanding so I decided to develop such a code example by myself.

Here is a list of references that it's worth to look through:
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

## Goals of the work ##
The goals of the work were:
* to have a code of the example based on __Spring Boot__ rapid application development platform;
* to use a pure Spring __Annotation-based__ configuration;
* to use __Apache Maven__ as a project build automation tool;
* to use __Tomcat as Embedded Web Server__ feature provided by Spring Boot using Maven;
* to reach a transparency of '__remember me__' HttpSecurity configuration for __Persistent Token Approach__.

The article ["Remember Me" in Spring Security Example](https://www.concretepage.com/spring/spring-security/remember-me-in-spring-security-example#database) inspired me, and its use-case and a database structure were taken as a starting points of the development.

## Environement ##
An environment, used for the development, includes:
* Ubuntu 18.04.1 LTS
* java version "1.8.0_181"
* Apache Maven 3.5.2
* mysql  Ver 14.14 Distrib 5.7.23, for Linux (x86_64)
* Google Chrome Version 68.0.3440.106 (Official Build) (64-bit)
* FireFox Quantrum 62.0 (64-bit)

## Application's landscape ##
The application is developed on __Java__, it's web pages are developed on __HTML__ with tiny inclusions of __CSS__ and __Javascript__ fragments. 
On the HTML pages CDN __Bootstrap__ v.4.1.3 stylesheets and __Thymeleaf-4__ templates are used. 
### Application's high-level structure ###
A structure of the application's project is as on a picture below:
```text
.
├── pom.xml
├── README.md
└── src
    └── main
        ├── java
        │   └── com
        │       └── lizardbuzzard
        │           ├── Apps.java
        │           ├── controller
        │           │   └── RequestController.java
        │           ├── persistence
        │           │   ├── CustomMySQLDialect.java
        │           │   ├── dao
        │           │   │   ├── AuthorityRepository.java
        │           │   │   └── UserRepository.java
        │           │   └── model
        │           │       ├── AuthorityEntity.java
        │           │       ├── AuthorityId.java
        │           │       └── UserEntity.java
        │           ├── security
        │           │   ├── config
        │           │   │   ├── MyCustomAuthenticationSuccessHandler.java
        │           │   │   ├── MyJdbcTokenRepositoryImpl.java
        │           │   │   ├── SecurityConfig.java
        │           │   │   └── SecurityWebApplicationInitializer.java
        │           │   └── service
        │           │       ├── UserDetailsServiceImpl.java
        │           │       ├── UserDTO.java
        │           │       └── UserProcessingService.java
        │           └── spring
        │               ├── config
        │               │   ├── DataSourceConfig.java
        │               │   └── MvcConfig.java
        │               └── PopulateDatabaseOnContextRefreshedEventListener.java
        ├── resources
        │   ├── jdbc.properties
        │   ├── static
        │   │   └── images
        │   │       └── funnycat.png
        │   └── templates
        │       ├── adminConsolePage.html
        │       ├── authenticationError.html
        │       ├── customLogin.html
        │       └── userPage.html
        └── webapp
```

### Spring Boot Maven Project ### 
Maven pom.xml refers to Spring Boot parent project version 2.0.4.RELEAS:
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.4.RELEASE</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>
```
And then it uses following org.springframework.boot dependencies: __spring-boot-starter-web__, __spring-boot-starter-data-jpa__, __spring-boot-starter-security__. 

This project uses [Thymeleaf](https://www.thymeleaf.org/) as a HTML pages template engine and includes __spring-boot-starter-thymeleaf__ in the dependencies.

Also the project dependencies include mysql:mysql-connector-java:5.1.46 dependency.

## Database configuration and creation ##
To run the code you should have MySQL server installed. In order to install it please refer, for example, to [How To Install MySQL on Ubuntu 14.04](https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-ubuntu-14-04) and [B.5.3.2 How to Reset the Root Password](https://dev.mysql.com/doc/refman/5.7/en/resetting-permissions.html). After the server is installed you need to create a user to connect to the database:
```sql
mysql -u root -p 
> CREATE USER 'remembermeuser'@'localhost' IDENTIFIED BY 'remembermepwd';
> GRANT ALL PRIVILEGES ON *.* TO 'remembermeuser'@'localhost';
> FLUSH PRIVILEGES;
``` 
/resources/jdbc.properties file contains properties which allow to create and to connect to the database with name of 'rememberMeDb': 
```
jdbc.driverClassName=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/rememberMeDb?createDatabaseIfNotExist=true
jdbc.username=remembermeuser
jdbc.password=remembermepwd

spring.jpa.database=mysql
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=com.lizardbuzzard.persistence.CustomMySQLDialect
hibernate.show_sql=false
```
The database 'rememberMeDb' created (spring.jpa.hibernate.ddl-auto=update property) if it not yet exists and 'remembermeuser' has access to the database (see [81. Database Initialization](https://docs.spring.io/spring-boot/docs/current/reference/html/howto-database-initialization.html) and [Spring boot ddl auto generator](https://stackoverflow.com/questions/21113154/spring-boot-ddl-auto-generator)). 
The property spring.jpa.properties.hibernate.dialect refers to CustomMySQLDialect class.
```java
/**
 * works in conjunction with spring.jpa.properties.hibernate.dialect=com.lizardbuzzard.persistence.CustomMySQLDialect
 * should be tuned for correct working with MySql version currently installed. Otherwise gives an error
 */
public class CustomMySQLDialect extends MySQL57Dialect {
    @Override
    public boolean dropConstraints() {
        return false;
    }
}
``` 
It's important to have this class corresponds to the dialect of the database you work with (see [SQL Dialects in Hibernate](https://www.javatpoint.com/dialects-in-hibernate)). As on the computer where the development was done 

I decided to use __MySQL57Dialect__ from a list of dialects accessible for org.hibernate.dialect.MySQLDialect
```html
Dialect (org.hibernate.dialect)
    MySQLDialect (org.hibernate.dialect)
        MySQL5Dialect (org.hibernate.dialect)
            MySQL55Dialect (org.hibernate.dialect)
                MySQL57Dialect (org.hibernate.dialect)
```
because I have mysql version on my computer installed 
```html
$ mysql -V
mysql  Ver 14.14 Distrib 5.7.23, for Linux (x86_64) using  EditLine wrapper
```

## Database Structure ##
Database schema contains two tables, __users__ and __authorities__:
```mysql-sql
CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `enabled` smallint(6) NOT NULL,
  `password` varchar(64) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `authorities` (
  `authority` varchar(50) NOT NULL,
  `username` varchar(50) NOT NULL,
  PRIMARY KEY (`authority`,`username`),
  KEY `FKhjuy9y4fd8v5m3klig05ktofg` (`username`),
  CONSTRAINT `FKhjuy9y4fd8v5m3klig05ktofg` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
JPA creates these two tables, which are described by two entity classes, __UserEntity__ and __AuthorityEntity__ (annotated by @Entity):
```java
@Entity
@Table(name = "users")
public class UserEntity {...}

@Entity
@Table(name = "authorities")
@IdClass(AuthorityId.class)
public class AuthorityEntity {...}
``` 
In order to manipulate of thise entities there are two JPA repositories, __UserRepository__ and __AuthorityRepository__ (the interfaces, annotated by @Repository):
```java
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByUsername(String username);
}

@Repository
public interface AuthorityRepository extends JpaRepository<AuthorityEntity, AuthorityId> {
    List<AuthorityEntity> findByUsername(String username);
    List<AuthorityEntity> findByUser(UserEntity user);
}
```
Since AuthorityEntity class maintains __many-to-one__ relationship between users' authorities and users, the best way to implement composite primary key is @IdClass annotation (see [How to effectively map and use Composite Primary keys in JPA?](http://theelitegentleman.blogspot.com/2018/01/how-to-effectively-map-and-use.html)).
This annotation has __AuthorityId.class__ as a parameter.

Third table, namely __persistent_logins__, is created by __MyJdbcTokenRepositoryImpl__ class, which extends Spring's JdbcTokenRepositoryImpl:
```mysql-sql
CREATE TABLE `persistent_logins` (
  `username` varchar(64) NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
A role which this table plays in __remember me persistent token aproach__ scenario is described in the [articles listed above](#a-list-of-references).

### Inserting Records into the Entities Tables ###
__PopulateDatabaseOnContextRefreshedEventListener__ class is responsible for insertion of objects in the database just after the database is created. It creates a following list of users, their roles and passwords:

This class implements ApplicationListener interface and the method 
```java
@Override
public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {...}
```
executes each time when Spring context is initialised. An additional information about this is in the next sources:
* [java listen to ContextRefreshedEvent](https://stackoverflow.com/questions/20275952/java-listen-to-contextrefreshedevent)
* [Spring Events](https://www.baeldung.com/spring-events)
* [Event Handling in Spring](https://www.tutorialspoint.com/spring/event_handling_in_spring.htm)
* [Better application events in Spring Framework 4.2](https://spring.io/blog/2015/02/11/better-application-events-in-spring-framework-4-2)
* [How to add a hook to the application context initialization event?](https://stackoverflow.com/questions/8686507/how-to-add-a-hook-to-the-application-context-initialization-event)

#### Users and Roles ####
A logical representation of the data inserted by __PopulateDatabaseOnContextRefreshedEventListener__ is shown in the table below:

|User No.| 	User Name| 	Password | List of User's Roles|
|--------|-----------|-----------|---------------------|
|1 		 |  admin 	 | admin123  |ROLE_ADMIN           |
|2 		 | 	joker 	 | joker123  |ROLE_ADMIN, ROLE_USER|
|3 		 | 	user1 	 | user1123  |ROLE_USER            |
|4 		 | 	user2 	 | user2123  |ROLE_USER            |
|5 		 | 	user3 	 | user3123  |ROLE_USER            |
|6 		 | 	user4 	 | user4123  |ROLE_USER            |
|7 		 | 	user5 	 | user5123  |ROLE_USER            |

## How to run the Application ##
The simplest way is to run the command in the project's home:
```text
$ mvn spring-boot:run
```
Otherwise, you can first build a __jar__ file by
```text
$ mvn clean package
```
command, which builds target/persistent-token-rememberme-authentication-1.0-SNAPSHOT.jar, and then you can run this jar:
```text
java -jar target/persistent-token-rememberme-authentication-1.0-SNAPSHOT.jar

```
Both the ways make Spring to start and to run the application:
```text
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.0.4.RELEASE)

```
After the server starts, you can enter in the browser's address bar __http://localhost:8080/app__ and the applicaion should show its __login page__:

![login page](readmeimages/login-page.png?raw=true)

Then you can enter users and their passwords from the [table above](#users-and-roles).

## Scenarios to explore ##
### Correct credentials scenario ###

You should fill in both the fields on the login form. In case if you leave one of the fields empty, the application shows an error message:

![empty login fields error](readmeimages/empty-login-fields-error.png?raw=true)

This check is done by Javascript __validateFormFields()__ function on the customLogin.html page.

In case when you filled in both of the fields but entered bad credentials, you will be informed by Spring's login form check:

![bad credentials](readmeimages/bad-credentials.png?raw=true)

The correct credentials (username/password) are listed in [this table](#users-and-roles).

A subject of the investigation is to ensure that 
* first, how works the grants on different URL depending on the roles of the user entered
* second, how works the 'remember me' set-up

### Authorize Requests Rules ###
In order to investigate the first point, we will enter with different users with different roles. Consider the following snippet from __configure(HttpSecurity http)__ method of __SecurityConfig__ class:
```java
http.authorizeRequests()
        .antMatchers("/app", "/app/accessDenied").permitAll()
        .and().exceptionHandling().accessDeniedPage("/app/accessDenied")
        .and().csrf().disable();

// two variants of AuthorizedUrl configuration
http.authorizeRequests()
        .antMatchers("/app/homepage/adminconsole/**").access("hasRole('ROLE_ADMIN')");
http.authorizeRequests()
        .antMatchers("/app/homepage/user/**", "/app/redirect").hasRole("USER");  // "ROLE_" adds automatically
```
Here we have next access rules configuration: 
* first, all the users with correct credentials are permitted to access "/app" and "/app/accessDenied" URL patterns;
* second, users with the '__ROLE_ADMIN__' role are granted to access all of the "/app/homepage/adminconsole/**" URL patterns;
* third, users with the '__ROLE_USER__' role are granted to access all of the "/app/homepage/user/**" and "/app/redirect" URL patterns;
* fourth, in case of violation of security access rules application redirects the user to error page by "/app/accessDenied" path.

The following snippets of code demonstrates a configuration done for __login__ page.  
```java
http.formLogin()
        .loginPage("/app")
        .loginProcessingUrl("/loginFormPostTo")
        .usernameParameter("myLoginPageUsernameParameterName")
        .passwordParameter("myLoginPagePasswordParameterName")
        .successHandler(authenticationSuccessHandler);
```
It's quite transparent except, may be, of __authenticationSuccessHandler__ parameter of __successHandler()__ method of AbstractAuthenticationFilterConfigurer class.
__authenticationSuccessHandler__ is an object of a class which implements AuthenticationSuccessHandler interface:
```java
@Autowired
@Qualifier("authenticationSuccessHandler")
private AuthenticationSuccessHandler authenticationSuccessHandler;
``` 
AuthenticationSuccessHandler is implemented by __MyCustomAuthenticationSuccessHandler__ class, the main purpose of which is to define a redirection page depending on a role of the user:
```java
String targetUrl = null;
Supplier<Stream<? extends GrantedAuthority>> sup = () -> auth.getAuthorities().stream();

if(sup.get().anyMatch(a->(((GrantedAuthority) a).getAuthority().equals("ROLE_ADMIN")))) {
    targetUrl = "/app/homepage/adminconsole";
} else if(sup.get().anyMatch(a->(((GrantedAuthority) a).getAuthority().equals("ROLE_USER")))) {
    targetUrl = "/app/homepage/user";
} else {
    targetUrl = "/app";
}

redirectStrategy.sendRedirect(request, response, targetUrl);
```
This code redirects to "/app/homepage/adminconsole" URL in case if the user has "ROLE_ADMIN" role and to "/app/homepage/user" URL in case of "ROLE_USER" role.

#### Authorize Request Tests ####

* Enter as the user with __ROLE_USER__ role

    Fill in __user1/user1123__ on login form. As expected the application redirects the user to "/app/homepage/user" and then via __userHomePage()__ method of __RequestController__ class
    ```java
    @RequestMapping("/homepage/user")
    public String userHomePage(Model model) {
        ...
        return "userPage";
    }
    ```    
    the user goes to the __userPage.html__ page:
    
    ![user work area page](readmeimages/user-work-area-page.png?raw=true)

* Enter as the user with __ROLE_ADMIN__ role

    Fill in __admin/admin123__ on login form. As expected the application redirects the user to "/app/homepage/adminconsole" and then via __adminHomePage()__ method of __RequestController__ class
    ```java
    @RequestMapping("/homepage/adminconsole")
    public String adminHomePage(Model model) {
        ...
        return "adminConsolePage";
    }
    ``` 
    the user goes to the __adminConsolePage.html__ page:
    
    ![admin console page](readmeimages/admin-console-page.png?raw=true)

    On the __adminConsolePage.html__ page there is the \<a> tag href attribute in which goes to the __userPage.html__ page:
    ```html
     <a th:href="@{/app/homepage/user/}">User Work Area Page</a>
    ```
    But if the user __admin/admin123__ tries to click on this link, he will be redirected on "/app/accessDenied" URL 
    ```java
    @RequestMapping("/accessDenied")
    public String authorizationErrorPage() {
        return "authorizationError";
    }
    ```
    and will go to __authorizationError.html__ page:
    
    ![authorization error page](readmeimages/authorization-error.png?raw=true)

* Enter as the user with both __ROLE_ADMIN__ and __ROLE_USER__ roles.

    Fill in __joker/joker1123__ on login form. The application redirects the user to "/app/homepage/adminconsole" URL and then to the user goes to the __adminConsolePage.html__ page:
    
    ![admin console joker page.png](readmeimages/admin-console-joker-page.png?raw=true)

    Since this user has __ROLE_USER__ role, after he clicks on \<a th:href="@{/app/homepage/user/}"> link, he goes to the __userPage.html__ page:
    
    ![user page joker.png](readmeimages/user-page-joker.png?raw=true)

### Remember Me Persistent ###
Setting of the "Remember Me" configuration is done in the following code snippet:
```java
http.rememberMe()
        .tokenRepository(persistentTokenRepository())
        .rememberMeParameter("myRememberMeParameterName")
        .rememberMeCookieName("my-remember-me")
        .tokenValiditySeconds(86400);
```
Here "myRememberMeParameterName" is the HTTP parameter used to indicate to remember the user. It is equal to the 'name' attribute of the checkbox of the \<input> tag on the __customLogin.html__ page:
```html
<label>
    <input class="checkbox" type="checkbox" name="myRememberMeParameterName">
    <span class="checkbox-custom"></span>
    <span class="label">Remember Me</span>
</label>
```
And "my-remember-me" is the name of cookie which store the token for remember me authentication.
__PersistentTokenRepository__ bean is configured as following:

```java
@Bean
public PersistentTokenRepository persistentTokenRepository() {
    MyJdbcTokenRepositoryImpl tokenRepository = new MyJdbcTokenRepositoryImpl();
    tokenRepository.setDataSource(dataSource);
    return tokenRepository;
}
```
MyJdbcTokenRepositoryImpl class extends JdbcTokenRepositoryImpl with purpose to override __initDao()__ method. Persistent Token Repository manipulates data in the __persistent_logins__ table.

#### Remember Me Persistent Test ####
In this test we will check how 'Remember Me Persistent' acts in case of two users entered the application in two different browsers. We launch two browsers, FireFox and Chrome, go to login page and enter __admin/admin123__ in Firefox and __user1/user1123__ in Chrome.

As we see there are two cookies in each of the browsers, they are __JSESSIONID__ and __my-remember-me__: 

![admin cookies](readmeimages/admin-cookies.png?raw=true)

and in Chrome:

![user cookies](readmeimages/user-cookies.png?raw=true)

And in the __rememberMeDb__ database two records for two user's __tokens__ in the __persistent_logins__ table have been created:

![persistent logins](readmeimages/persistent-logins.png?raw=true)
 
We will follow to the scenario described in ["Remember Me" in Spring Security Example](), in the [Persistent Token Approach using Annotation](https://www.concretepage.com/spring/spring-security/remember-me-in-spring-security-example#persistent-token-annotation) section:
* delete __JSESSIONID__ cookie. After deleting this cookie, session is expired;
* stop and then restart the server again
* then try to access the URLs __http://localhost:8080/app/homepage/adminconsole__ in __FireFox__ and __http://localhost:8080/app/homepage/user__ in __Chrome__. In each cases we enter to these pages without authentication on __login__ page.
* then try to access the URLs __http://localhost:8080/app/homepage/adminconsole__ in __Chrome__ and __http://localhost:8080/app/homepage/user__ in __FireFox__. This time in each of the browsers we are redirected to __login__ pages.

And after logout for both of the users __admin/admin123__ and __user1/user1123__ in both of the browsers, the token records in the __persistent_logins__ table will be deleted.

## Some additional Notes on Spring Security Configuration ##

1. A class which makes the application to run:
    ```java
    @SpringBootApplication
    public class Apps {
        public static void main(String[] args) {
            SpringApplication.run(Apps.class, args);
        }
    }
    ```
2. As soon as we use __Spring Boot__ we can avoid of using of __SecurityWebApplicationInitializer__

3. The default URL where the Spring Login will POST to trigger the authentication process is __/login__ which can be overridden via the __loginProcessingUrl()__ method (see [Spring Security Form Login](https://www.baeldung.com/spring-security-login)). This HttpSecurity configuration 
    ```
    loginProcessingUrl("/loginFormPostTo").
    
    ```
    corresponds to __customLogin.html__ form's action attribute 
    ```html
    <form name="myform" th:action="@{/loginFormPostTo}" method="POST" onsubmit="return validateFormFields();">

    ```
4. __ExpressionUrlAuthorizationConfigurer.AuthorizedUrl__ configuration.
    Code fragment below shows two variants of AuthorizedUrl configuration
    "When creating our users ... , we do not specify “ROLE_” as we would with the XML configuration. Since this convention is so common, the “roles” method automatically adds “ROLE_” for you. If you did not want “ROLE_” added you could use the authorities method instead."([Spring Security Java Config Preview: Web Security](https://spring.io/blog/2013/07/03/spring-security-java-config-preview-web-security/)).
    
    "So hasAuthority(‘ROLE_ADMIN’) is similar to hasRole(‘ADMIN’) because the ‘ROLE_‘ prefix gets added automatically."
    But the good thing about using authorities is that we don’t have to use the ROLE_ prefix at all.([Intro to Spring Security Expressions](https://www.baeldung.com/spring-security-expressions))

5. How to use __static resources__ on HTML Page. [Adding Static Resources (css, JavaScript, Images) to Thymeleaf](https://memorynotfound.com/adding-static-resources-css-javascript-images-thymeleaf/)
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
    In order to get static resource (image) on HTML page
    ```
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**", "/images/**")
                .addResourceLocations("/", "/resources/", "classpath:/static/images/");
    }
    ```
    Controller's method which process a redirect to external site URL
    ```html
    @RequestMapping("/redirect")
    public String redirectPage() {
        return "redirect:https://www.w3.org/";
    }
    ```
6. In order to implement login/authentication with Spring Security, we need to implement the UserDetailsService interface. 
    The UserDetailsService interface is used to retrieve user-related data. Spring Security provides a UserDetailsService interface to lookup the username, password and GrantedAuthorities for any given user. See
    * [Spring Security: Authentication with a Database-backed UserDetailsService](https://www.baeldung.com/spring-security-authentication-with-a-database)
    * [SPRING BOOT WEB APPLICATION, PART 6 – SPRING SECURITY WITH DAO AUTHENTICATION PROVIDER](https://springframework.guru/spring-boot-web-application-part-6-spring-security-with-dao-authentication-provider/)
