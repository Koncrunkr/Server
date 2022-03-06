# Server documentation

Copy git link ```https://github.com/Koncrunkr/Server.git``` and use any of git managers to clone repo.

To run you have to specify in `application.properties`
these values:
```properties
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=

spring.security.oauth2.client.registration.google.client-id=
spring.security.oauth2.client.registration.google.client-secret=

spring.jpa.hibernate.ddl-auto=update

ru.comgrid.websocket.trace.max-count=100
management.trace.http.enabled=false
ru.comgrid.websocket.trace.enabled=true
ru.comgrid.http.trace.enabled=true

ru.comgrid.chat.default-page-size=50
ru.comgrid.chat.participants.default-page-size=50

spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

server.servlet.session.cookie.secure=true
server.servlet.session.persistent=true
```
To compile `fat jar` use command `clean install spring-boot:repackage -DskipTests`