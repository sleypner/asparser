plugins {
    application
    java
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "dev.sleypner"
version = "1.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }
}
application {
    mainClass = "dev.sleypner.asparser.Application"
}
springBoot {
    mainClass = "dev.sleypner.asparser.Application"
}
repositories {
    mavenCentral()
}

dependencies {

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.3.1")
    implementation("org.springframework.boot:spring-boot-starter-mail:3.3.1")
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.3.1")
    implementation("org.springframework.boot:spring-boot-starter-security:3.3.1")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client:3.3.4")
    implementation("org.springframework.boot:spring-boot-devtools:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-cache:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.5.0")
    implementation("org.springframework.boot:spring-boot-devtools:3.3.1")
    implementation("org.springframework.boot:spring-boot-starter-mail:3.3.1")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.5.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")


    implementation("org.hibernate.orm:hibernate-community-dialects:7.0.0.Alpha3")
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.17.1")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("com.mysql:mysql-connector-j:9.0.0")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6:3.1.2.RELEASE")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.1")
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")
    implementation("systems.manifold:manifold-ext-rt:2025.1.20")
    annotationProcessor("systems.manifold:manifold-ext:2025.1.20")

    implementation("org.postgresql:postgresql:42.7.5")

    implementation("com.github.ua-parser:uap-java:1.6.1")

//	implementation("org.jobrunr:jobrunr-spring-boot-3-starter:7.3.2")
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    implementation("io.github.resilience4j:resilience4j-reactor:2.3.0")
    implementation("io.github.resilience4j:resilience4j-ratelimiter:2.3.0")
}
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xplugin:Manifold")
}
tasks.withType<Test> {
    useJUnitPlatform()
}
