# Server documentation

Copy git link ```https://github.com/Koncrunkr/Server.git``` and use any of git managers to clone repo.

To run you have to specify in `application.properties`
these values:
```properties
#spring.datasource.url=
#spring.datasource.username=
#spring.datasource.password=

#spring.flyway.user=
#spring.flyway.password=
#spring.flyway.url=

#spring.security.oauth2.client.registration.google.client-secret=

#spring.security.oauth2.client.registration.vk.client-secret=

#ru.comgrid.server.image-compressor=none

#ru.comgrid.auth.tokenSecret=
#ru.comgrid.auth.tokenExpirationMsec=
#ru.comgrid.auth.admin-key=
#ru.comgrid.auth.vkAccessToken=
```
To compile `fat jar` use command `mvn clean install spring-boot:repackage -DskipTests`
