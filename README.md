# Server documentation

Copy git link ```https://github.com/Koncrunkr/Server.git``` and use any of git managers to clone repo.

To run you have to specify in `application.properties`
these values:
```properties
spring.datasource.url=
## spring.datasource.url must be deleted if building for production
spring.datasource.username=
spring.datasource.password=

spring.flyway.baseline-on-migrate=true
spring.flyway.enabled=true
spring.flyway.user=
spring.flyway.password=
spring.flyway.url=
## spring.flyway.url must be deleted if building for production

spring.security.oauth2.client.registration.google.client-id=
spring.security.oauth2.client.registration.google.client-secret=
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/oauth2/callback/{registrationId}
spring.security.oauth2.client.registration.google.scope=email,profile

spring.security.oauth2.client.provider.vk.authorization-uri=https://oauth.vk.com/authorize
spring.security.oauth2.client.provider.vk.token-uri=https://oauth.vk.com/access_token
spring.security.oauth2.client.provider.vk.user-info-uri=https://api.vk.com/method/users.get
spring.security.oauth2.client.provider.vk.user-name-attribute=id
spring.security.oauth2.client.provider.vk.user-info-authentication-method=form
spring.security.oauth2.client.registration.vk.client-id=
spring.security.oauth2.client.registration.vk.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.vk.redirect-uri={baseUrl}/oauth2/callback/{registrationId}
spring.security.oauth2.client.registration.vk.client-secret=
# 4194304 - email, 1 - notification, 2 - friends. sum is scope.
spring.security.oauth2.client.registration.vk.scope=4194307


spring.jpa.hibernate.ddl-auto=validate
# whether to change schema on entity change (google for values)
ru.comgrid.server.file-controller.image-path=/images/
ru.comgrid.server.image-compressor.allowed-extensions=png,jpg,webp,avif
ru.comgrid.server.image-compressor=http://image-optimizer:3000/optimize?size=1080&format=webp
## ru.comgrid.server.image-compressor must be deleted if building for production

ru.comgrid.websocket.trace.max-count=100
# websocket max count of traces
management.trace.http.enabled=false
ru.comgrid.websocket.trace.enabled=true
# websocket trace
ru.comgrid.http.trace.enabled=true
# http trace

spring.servlet.multipart.max-file-size=10MB
# maximum file size to be uploaded
spring.servlet.multipart.max-request-size=10MB
#maximum request size


ru.comgrid.auth.tokenSecret=
# Secret token, should be strong(512 symbols+)
ru.comgrid.auth.tokenExpirationMsec=864000000
ru.comgrid.auth.vkAccessToken=not-used

ru.comgrid.auth.authorizedRedirectUris=http://localhost:8080/,https://comgrid.ru,https://comgrid.ru:8443

```
To compile `fat jar` use command `mvn clean install spring-boot:repackage -DskipTests`
