# Server documentation

Copy git link ```https://github.com/Koncrunkr/Server.git``` and use any of git managers to clone repo.

To run you have to specify in `application.properties`
these values:
```properties
spring.datasource.url=
## spring.flyway.url must be deleted if building for production
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

server.servlet.session.cookie.secure=true
# whether to use secure cookie
server.servlet.session.persistent=true
# whether to save cookies between restarts
```
To compile `fat jar` use command `mvn clean install spring-boot:repackage -DskipTests`
