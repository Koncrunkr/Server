# Server documentation

# Authentication
### Initial authentication
First you have to log in, if you already haven't: You have to redirect user to
```https://comgrid.ru:8443/oauth2/authorization/{provider}```, where ```{provider}``` 
is ```google``` or other provider(in future)

### Authentication check
To check whether user has logged in you might send request:
```http request
GET https://comgrid.ru:8443/user/login
```
If you get 200 status code, then user is logged in. 401 means user is not logged in. 
Other status code might indicate other exceptions or maybe connection problems.


## User requests:
### User info
Get info about user:
```http request
GET https://comgrid.ru:8443/user/info
```
Params:

| param                 | include   | Description                  |
|-----------------------|-----------|------------------------------|
| includeChats: boolean | optional  | Whether to include chat list |

Returns Person object with following fields:

| field           | includes | description                                   |
|-----------------|----------|-----------------------------------------------|
| id: string      | always   | unique person id consisting of digits         |
| name: string    | always   | name of user                                  |
| email: string   | always   | email of user                                 |
| avatar: string  | always   | link to avatar of user                        |
| chats: Chat[]   | optional | list of Chats, that this user participates in |

## Table requests:
### Create table
Create table for current user.
```http request
POST https://comgrid.ru:8443/table/create
```
Params:

| param           | includes | description                |
|-----------------|----------|----------------------------|
| name: string    | always   | name of chat               |
| creator: string | never    | chat's creator's unique id |
| width: integer  | always   | width of chat in cells     |
| height: integer | always   | height of chat in cells    |
| avatar: string  | always   | link to avatar of chat     |

<i>Note: you should not include creator's id, because authenticated user's will be used</i>

Returns created Chat object with following fields:

| field              | includes | description                               |
|--------------------|----------|-------------------------------------------|
| id: integer        | always   | unique id of table(chat)                  |
| name: string       | always   | name of chat                              |
| creator: string    | always   | chat's creator's unique id                |
| width: integer     | always   | width of chat in cells                    |
| height: integer    | always   | height of chat in cells                   |
| avatar: string     | always   | link to avatar of chat                    |

### Get table info
```http request
GET https://comgrid.ru:8443/table/info
```
Get info about table user participates in.

Params:

| field                        | includes | description                                    |
|------------------------------|----------|------------------------------------------------|
| includeParticipants: boolean | optional | whether to include participants of chat or not |
| chatId: integer              | always   | unique chat's id                               |

Returns Chat with given chatId, if user participates in it:

| field                  | includes | description                                    |
|------------------------|----------|------------------------------------------------|
| id: integer            | always   | unique id of table(chat)                       |
| name: string           | always   | name of chat                                   |
| creator: string        | always   | chat's creator's unique id                     |
| width: integer         | always   | width of chat in cells                         |
| height: integer        | always   | height of chat in cells                        |
| avatar: string         | always   | link to avatar of chat                         |
| participants: Person[] | optional | list Person objects, participants of this chat |

#### Exceptions
Might return 404 if chat doesn't exist

### Get messages of table
Get messages of table in given square with specified topLeft and bottomRight point.
```http request
GET https://comgrid.ru:8443/table/messages
```
Params:

| param               | includes | description                                                                  |
|---------------------|----------|------------------------------------------------------------------------------|
| chatId              | always   | unique chatId                                                                |
| xCoordLeftTop       | always   | Top left point of square's x coord                                           |
| yCoordLeftTop       | always   | Top left point of square's y coord                                           |
| xCoordRightBottom   | always   | Bottom right point of square's x coord                                       |
| yCoordRightBottom   | always   | Bottom right point of square's y coord                                       |
| amountOfMessages    | optional | Amount of messages that will be loaded(maximum available 100, default is 50) |
| sinceDateTimeMillis | optional | Minimum time of messages to include(default no limit)                        |
| untilDateTimeMillis | optional | Maximum time of messages to include(default no limit)                        |

Returns Message json array consisting of Message objects:

| field             | includes | description                                      |
|-------------------|----------|--------------------------------------------------|
| id: integer       | always   | unique id of message                             |
| x: integer        | always   | x coordinate of message                          |
| y: integer        | always   | y coordinate of message                          |
| chatId: integer   | always   | chatId this message corresponds to               |
| time: integer     | always   | time when this message was sent(since 1.01.1970) |
| senderId: integer | always   | unique sender's id                               |
| text: string      | always   | content of message                               |



# Examples
### Example of GET request
```javascript
fetch(
    "https://comgrid.ru:8443/user/info?includeChats=true",
    {
        method: "GET",
        credentials: "include"
    }
).then(
    response => {
        if(response.status == 200) {
            person = JSON.parse(response.text())
            // person is js object...
        }else{
            // some error occured
        }
    }
)
```
### Example of POST request
```javascript
fetch(
    "https://comgrid.ru:8443/table/create",
    {
        method: "POST",
        credentials: "include",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            "name": $("#name").val(),
            "avatar": $("#avatar").val(),
            "width": parseInt($("#width").val()),
            "height": parseInt($("#height").val())
        })
    }
).then(
    response => {
        if(response.status == 200){
            table = JSON.parse(response.text())
            // table is js object...
        }else{
            // some error occured
        }
    }
)
```