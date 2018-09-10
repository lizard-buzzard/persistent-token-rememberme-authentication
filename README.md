### Persistent Token Approach for HttpSecurity rememberMe() Authentication ###
The purpose of this work was to understand '__remember me__' HttpSecurity configuration of WebSecurityConfigurerAdapter which is based on __Persistent Token Approach__.

#### A List of References ####
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

#### Goals of the work ####
The goals of the work were:
* to have a code of the example based on __Spring Boot__ rapid application development platform;
* to use a pure Spring __Annotation-based__ configuration for this task;
* to use __Apache Maven__ as a project build automation tool;
* to use __Tomcat as Embedded Web Server__ feature provided by Spring Boot using Maven;
* to reach a transparency of '__remember me__' HttpSecurity configuration for __Persistent Token Approach__.

The article ["Remember Me" in Spring Security Example](https://www.concretepage.com/spring/spring-security/remember-me-in-spring-security-example#database) inspired me, and its use-case and a database structure were taken as a starting point of the development.

#### Environement ####
An environment, used for the development, includes:
* Ubuntu 18.04.1 LTS
* java version "1.8.0_181"
* Apache Maven 3.5.2
* mysql  Ver 14.14 Distrib 5.7.23, for Linux (x86_64)
* Google Chrome Version 68.0.3440.106 (Official Build) (64-bit)
* FireFox Quantrum 62.0 (64-bit)

#### Application's landscape ####
The application is developed on __Java__, it's web pages are developed on __HTML__ with tiny inclusions of __CSS__ and __Javascript__ fragments. 
On the HTML pages CDN __Bootstrap__ v.4.1.3 stylesheets and __Thymeleaf-4__ templates are used. 
##### Application's high-level structure #####
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

##### Spring Boot maven project ##### 
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

In place of JSTL this project uses [Thymeleaf](https://www.thymeleaf.org/) as a HTML pages template engine and includes __spring-boot-starter-thymeleaf__ in the dependencies.

Also the project dependencies include mysql:mysql-connector-java:5.1.46 dependency.

#### Database configuration and creation ####
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

#### Database structure ####
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

##### Inserting records into the entities tables #####
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

###### Users and Roles ######
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

#### How to run the application ####
The simplest way is to run the command in the project's home:
```text
$ mvn spring-boot:run
```
Otherwise, you can first buind a __jar__ file by
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
After the server starts, you can enter in the browser's address bar __http://localhost:8080/app__ and the application should show its login page:

![login page](readmeimages/login-page.png?raw=true)

Then you can enter users and their passwords from the [table above](#users-and-roles).

#### Scenarios to explore ####
##### ROLE_ADMIN scenario #####



##### ROLE_USER scenario #####


##### ROLE_ADMIN, ROLE_USER scenario #####


##### 'my-remember-me' cookie #####


#### Spring Security Configuration ####

##### Spring MVC Annotation based configuration #####
1. A class which makes the application to run:
    ```java
    @SpringBootApplication
    public class Apps {
        public static void main(String[] args) {
            SpringApplication.run(Apps.class, args);
        }
    }
    ```
2. As soon as we use Spring Boot we can avoid of using of SecurityWebApplicationInitializer










 
[Spring Security Form Login](https://www.baeldung.com/spring-security-login)
The default URL where the Spring Login will POST to trigger the authentication process is /login which can be overridden via the loginProcessingUrl method. This HttpSecurity configuration 
```
loginProcessingUrl("/loginFormPostTo").

```
correlates with customLogin.html form's action attribute 
```html
<form name="myform" th:action="@{/loginFormPostTo}" method="POST" onsubmit="return validateFormFields();">

```




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

Controller's method which process a redirect to external site URL
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
