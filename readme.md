<h1 align="center">
  Java Kafka Publisher (JaKaPu)
</h1>
<p align="center">
  <img src="images/jakapu.png" alt="Jakapu - Amex" width="50%" />
</p>

<p align="center"><b>
JaKaPu is a config-driven SDK to publish events to Kafka topics without writing any boilerplate code.
</b></p>

---

## ✨ Features

- Build a Kafka Publisher using a single annotation
- Seamlessly integrate into existing Spring Boot applications
- Publish messages to multiple Kafka topics
- Route messages based on header keys
- Validate Kafka headers before publishing
- Easily configure broker, topic, and security settings via YAML
- Built-in REST endpoint `/jakapu/publish` for publishing messages

---

## 📖 Prerequisites

- Java 8+
- Maven 3+
- Spring Framework
- Kafka Topic(s)

---

## 🚀 Getting Started

### Add Maven Dependency

```xml
<dependency>
  <groupId>com.americanexpress.jakapu</groupId>
  <artifactId>jakapu</artifactId>
  <version>${latest.version}</version>
</dependency>
```

##### Add @EnableJakapu as a dependency annotation on Spring Boot Main Application class.

```java
     @EnableJakapu
      public class SampleSpringApplication {

      public static void main(String[] args) {
          logger.info("Spring Boot Sample App Started.............");
          ApplicationContext app = SpringApplication.run(SampleSpringApplication.class);
      }
  }
```

##### Configure :

    - Dynamically add any number of topics to your configuration under **jakapu.psf.publisher.topics**
    - Define mandatory kafka header attribute names and their validation pattern(**jakapu.psf.publisher.payload.headerAttribute**)
    - header-based topic routing can be added in the configuration under **Jakapu.psf.publisher.headerTopicMap**

```yaml
jakapu:
  psf:
    publisher:
      topics:
        - name: Topic1
          id: t1
          numPartitions: 4
          replication-factor: 1
        - name: Topic2
          id: t2
          numPartitions: 8
          replication-factor: 1

    payload:
      headerAttribute:
        source-type: "re(jakapu.psf.payload.headerTopicMap)"
        event-type: "rx(\\w{1,})" #Java Regex to validate the type of event. Accepts any string size more than 1.
        source-uniqueid: "rx(\\w{1,})" #Java Regex to validate string. Accepts any string size more than 1.
        source-timestamp: "rx(\\d{13})" #Java Regex to validate timestamp. 13 digit number.
      headerTopicMap:
        MySource1: t1
        MySource2: t2

  ### kafka config related to broker, ssl, keystores etc

  kafka:
    security:
      enabled: false
      protocol: SSL
    ssl:
      protocol: TLSv1.2
      keystore:
        type: JKS
        location: your-private-keystore.jks
        password: $$$$
      key:
        password: $$$$
      truststore:
        location: client-truststore.jks
        password: $$$$
    bootstrap-servers: "yourkafka.domain.com"
```

##### To publish a message, autowire Publisher interface and then use the API call - **publisher.publish()**

```java
public class SampleKafkaPublisher {

    @Autowired
    private Publisher publisher;

    public send(String message) {
        Map<String,String> headerMap = ... ; // add kafka headers that are mandatory/needed
        publisher.publish(headerMap, message);
    }
}
```

##### Example Publish Message using Curl to the inbuilt REST endpoint:

```unix
curl -H 'content-type: application/json' \
-H 'source-type: MySource1' \
-H 'event-type: TestEventType' \
-H 'source-uniqueid: 231324' \
-H 'source-timestamp: 1576020324880' \
--data '{"message":"test"}' \
https://hostname/jakapu/publish
```

## 🏆 Contributing

We welcome Your interest in the American Express Open Source Community on Github. Any Contributor to
any Open Source Project managed by the American Express Open Source Community must accept and sign
an Agreement indicating agreement to the terms below. Except for the rights granted in this
Agreement to American Express and to recipients of software distributed by American Express, You
reserve all right, title, and interest, if any, in and to Your Contributions. Please
[fill out the Agreement](https://cla-assistant.io/americanexpress/jakapu).

## 🗝️ License

Any contributions made under this project will be governed by the
[Apache License 2.0](./LICENSE.txt).

## 🗣️ Code of Conduct

This project adheres to the [American Express Community Guidelines](./CODE_OF_CONDUCT.md).
By participating, you are expected to honor these guidelines.
