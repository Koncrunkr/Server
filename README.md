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

#RabbitMQ properties
#ru.comgrid.websocket.rabbitMq.relayHost=
#ru.comgrid.websocket.rabbitMq.relayPort=
#ru.comgrid.websocket.rabbitMq.systemLogin=
#ru.comgrid.websocket.rabbitMq.systemPassword=
#ru.comgrid.websocket.rabbitMq.clientLogin=
#ru.comgrid.websocket.rabbitMq.clientPassword=

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

To compile `fat jar` use command `mvn clean install spring-boot:repackage -DskipTests`
