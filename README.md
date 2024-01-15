## 简介

`spring-boot-starter-lrucache` 是一个基于 Spring Boot 的自定义启动器，用于提供易于配置和使用的 LRU (Least Recently Used) 缓存机制。这个启动器通过简单的配置，允许开发者在 Spring Boot 应用中快速集成和使用高效的缓存策略。

## 特性

- **最近最少使用（LRU）缓存策略**: 自动移除长时间未使用的缓存项，确保缓存的高效利用。
- **易于配置**: 通过 Spring Boot 的配置文件进行配置。
- **过期策略**: 支持基于访问时间和写入时间的自动过期。
- **事件监听**: 可以添加监听器来监控缓存项的添加和移除。
- **缓存统计**: 提供缓存命中率和缓存项数量等统计信息。

## 快速开始

1. 拉取代码

   ```text
   https://github.com/lidachui1998/spring-boot-starter-lrucache.git
   ```

2. maven本地打包安装

   ```text
   mvn clean install
   ```

3. **添加依赖**: 在你的 `pom.xml` 或 `build.gradle` 文件中添加 `spring-boot-starter-lrucache` 的依赖。

   Maven:

   ```xml-dtd
   <dependency>
     <groupId>com.lidachui</groupId>
     <artifactId>spring-boot-starter-lrucache</artifactId>
     <version>1.0.0</version>
   </dependency>
   ```

   Gradle:

   ```
   dependencies {
     implementation 'com.lidachui:spring-boot-starter-lrucache:1.0.0'
   }
   ```

4. **配置属性**: 在你的 `application.properties` 或 `application.yml` 中配置缓存的属性。

   application.properties 示例:

   ```yaml
   lru.cache.capacity=100
   lru.cache.expireAfterAccess=10000
   lru.cache.expireAfterWrite=10000
   ```

5. **使用 LRUCache**: 在你的 Spring Boot 应用中注入 `LRUCache` 实例并使用。

   Java 示例:

   ```java
   @Autowired
   private LRUCache<String, MyObject> cache;
   
   public void useCache() {
     cache.put("key1", new MyObject());
     MyObject cachedObject = cache.get("key1");
     // ...
   }
   ```

## 配置选项

- `capacity`: 缓存的最大容量。
- `expireAfterAccess`: 访问后自动过期时间（秒），小于等于0表示不启用。
- `expireAfterWrite`: 写入后自动过期时间（秒），小于等于0表示不启用。

##  监听器说明

`spring-boot-starter-lrucache` 提供了一个功能强大的监听器机制，用于监听缓存中的 `put` 和 `remove` 事件。这允许开发者在缓存项被添加或移除时执行自定义逻辑，例如记录日志、更新统计数据或执行其他业务相关操作。

### 如何使用监听器

1. **实现 `CacheListener` 接口**: 创建一个类并实现 `CacheListener` 接口。该接口包含两个方法：`onEntryAdded` 和 `onEntryRemoved`，分别对应缓存项的添加和移除事件。

   ```java
   public class MyCacheListener<K, V> implements CacheListener<K, V> {
       @Override
       public void onEntryAdded(K key, V value) {
           // 缓存项添加时执行的操作
           System.out.println("Cache entry added: " + key);
       }
   
       @Override
       public void onEntryRemoved(K key, V value) {
           // 缓存项移除时执行的操作
           System.out.println("Cache entry removed: " + key);
       }
   }
   ```

2. **注入 `LRUCache` 并添加监听器**: 在你的 Spring Boot 应用中，注入 `LRUCache` 的实例，并将你的监听器实例添加到缓存中。

   ```
   @Autowired
   private LRUCache<String, MyObject> cache;
   
   @Bean
   public MyCacheListener<String, MyObject> myCacheListener() {
       return new MyCacheListener<>();
   }
   ```

   在这个例子中，`MyCacheListener` 类实现了 `CacheListener` 接口，并将其注入spring容器。这样，每当缓存项被添加或移除时，`MyCacheListener` 中相应的方法就会被调用。

### 使用场景

监听器在很多场景中都非常有用，例如：

- **记录日志**: 当缓存项被添加或移除时，记录相关信息到日志系统。
- **监控**: 监控缓存的使用情况，例如统计添加或移除的次数。
- **触发事件**: 在缓存项变更时触发其他系统事件或业务流程。

### 结论

通过实现和注册 `CacheListener` 接口，开发者可以轻松地监控和响应缓存系统中的关键事件，从而使缓存管理更加灵活和高效。

## 高级用法

- **

添加自定义监听器**: 实现 `CacheListener` 接口并注册到缓存中，可以监听缓存项的添加和移除事件。

```java
public class MyCacheListener<K, V> implements CacheListener<K, V> {
    @Override
    public void onEntryAdded(K key, V value) {
        // 缓存项添加时的逻辑
    }

    @Override
    public void onEntryRemoved(K key, V value) {
        // 缓存项移除时的逻辑
    }
}
```

- **获取缓存统计信息**: 使用 `getCacheStats()` 方法获取缓存的统计信息，如命中率、缓存大小等。

  ```
  CacheStats stats = cache.getCacheStats();
  System.out.println("Cache Hit Rate: " + stats.getHitRate());
  ```

## 贡献与支持

如果您在使用 `spring-boot-starter-lrucache` 时遇到任何问题，或者有任何功能请求，欢迎通过以下方式联系我们或贡献代码：

- **Issue 追踪**: 在 GitHub 项目页面提交问题。
- **代码贡献**: 欢迎通过 Pull Requests 提交改进。

## 关于作者

`spring-boot-starter-lrucache` 由 lidachui 创建和维护，专注于为 Java 开发者提供高效、易用的缓存解决方案。

------

此 README.md 文件旨在提供 `spring-boot-starter-lrucache` 项目的基本介绍和使用指南。如果有任何具体问题或建议，欢迎反馈！