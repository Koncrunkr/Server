# Server documentation

Copy git link ```https://github.com/Koncrunkr/Server.git``` and use any of git managers to clone repo.

To run you have to specify in `application.properties`
these values:
```properties
# url to postgres sql(or any other sql if you add its dependency to pom.xml)
#spring.datasource.url=
#spring.datasource.username=
#spring.datasource.password=

# the same as the three of the above
#spring.flyway.url=
#spring.flyway.user=
#spring.flyway.password=

# google secret from https://console.cloud.google.com/apis/credentials
#spring.security.oauth2.client.registration.google.client-secret=

# vk secret from https://vk.com/apps?act=manage
#spring.security.oauth2.client.registration.vk.client-secret=

# image compression service url
# app might work without it, but it won't be able to consume images
#ru.comgrid.server.image-compressor=none

# token secret to generate auth tokens
#ru.comgrid.auth.tokenSecret=
# tokens expiration time in ms
#ru.comgrid.auth.tokenExpirationMsec=
# admin key to make user an admin
#ru.comgrid.auth.admin-key=
# vk service token from https://vk.com/apps?act=manage currently not in use
#ru.comgrid.auth.vkAccessToken=
```

| Метод           | Корень 1            | Корень 2           |
|-----------------|---------------------|--------------------|
| В лоб           | -57.41713566385335  | 32.039743232742175 |
| Кремер          | -57.417135663846246 | 32.03974323273819  |
| Гаусс           | -57.417135663794674 | 32.039743232710336 |
| Гаусс с выбором | -57.41713566381257  | 32.03974323272017  |
| Истинный корень | -57.4171356638126   | 32.0397432327202   |


To compile `fat jar` use command `mvn clean install spring-boot:repackage -DskipTests`
