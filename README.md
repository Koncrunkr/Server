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
# whether to change schema on entity change (google for values)

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
