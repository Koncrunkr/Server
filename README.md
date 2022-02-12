# Server documentation

## Chat:

| field              | includes | description                               |
|--------------------|----------|-------------------------------------------|
| id: integer        | always   | unique id of table(chat)                  |
| name: string       | always   | name of chat                              |
| creator: string    | always   | chat's creator's unique id                |
| width: integer     | always   | width of chat in cells                    |
| height: integer    | always   | height of chat in cells                   |
| avatar: string     | always   | link to avatar of chat                    |
| participants: list | optional | list consisting of {@link Person} objects |

## Create chat post request params:

| field           | includes | description                |
|-----------------|----------|----------------------------|
| name: string    | always   | name of chat               |
| creator: string | never    | chat's creator's unique id |
| width: integer  | always   | width of chat in cells     |
| height: integer | always   | height of chat in cells    |
| avatar: string  | always   | link to avatar of chat     |

<i>Note: you should not include creator's id, because authenticated user's will be used</i>

## Get info about chat request params:

| field                        | includes | description                                             |
|------------------------------|----------|---------------------------------------------------------|
| id: integer                  | always   | unique id of table(chat)                                |
| includeParticipants: boolean | optional | whether to include participants({@link Person}s) or not |

## Person:

| field                 | includes | description                                           |
|-----------------------|----------|-------------------------------------------------------|
| id: string            | always   | unique person id consisting of digits                 |
| name: string          | always   | name of user                                          |
| email: string         | always   | email of user                                         |
| avatar: string        | always   | link to avatar of user                                |
| chats: {@link Chat}[] | optional | list of {@link Chat}s, that this user participates in |

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
