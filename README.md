# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![My Sequence Diagram](Client-Diagram.png)](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQHPCMykoKp+h-Ds0KPMB4lUEiMAIEJ4oYoJwkEkSYCkm+hi7jS+4MkyU76XOnl3kuwowGKEpujKcplu8SqYCqwYam6RgQGoMBoBAzBWmiN4BQ6Sa+s6XYbu6W5uSB1T+m6ABy6Uir4nBRjGcaFFpeXwMgqYwOm+GjGMOaqHm8zQUWJb1PpqXpTAABmdW7HRTalR5vIjvUh5yCgz7xOel7XsOuWVMuAZrgG20lQV7Y6aWDnihkqgAZgF1lRJYGEeWxGkd8FFUfWJGoQ2LXJu12EwLh3Xga98w-TB5GXl9yGQ2h9GMd4fj+F4KDoDEcSJGjGMOb4WCiYKoH1A00gRvxEbtBG3Q9HJqgKcMn2Ieg6HwpUF31EzSH3WZGEWVZNn2Pj9lCfjTlqC527uVSt78t5YAbVt8HM2g-lLXtQr1KFT4nfIsrylzLNxeqx16tASAAF55AUMCQEhSUpWlGXWjk2Xq4u-MuqbV6nZ2j01PUG2w9VYC1T49XRigsYKazmGA2AaZOAAjARfUDTRw3QJzMMq+NzDTeHs2NgxC0frzl2i6eN13Q9rXEzAIyaeV8IAyUCfA3hDdzQxnjIwEKLrv42Dihq-FojAADiSoaITFmSRPlM0-YSqMzn3Mtez5fZ8r3O1-lnb1MgORTzmGJByrrlnYtC6jjAjIK5eSuURfbs3-ewXa97EUG2vRuqglWpw4UXNhbFADFdoe33pZL2G1XxXznqWc+SEQ5hwjo1GO-1KiiUTinHqad8xDWLFnBU0xLwgLAQ2RGC0ZY5XpNZNEJ81AYjVm-IKWtxTrhNAgO+08pbnS3mPHIAAeRhPJyg81hHzBBLwxjLxzAsYYjRxhyJQAASWkAWJO4RgiBBWGMTY8RdQoDdJyPYqxhhjGSKANUJjIKLG+CoyqSp7EXE6E3J6cc244TwgRFRqgFFoCUbIpU6jNHaN0fowxk4iKDTGOY5CViQA2JiWYkEjjnFxNcd3JGzF-AcAAOxuCcCgJwMQIzBDgFxAAbPACchhGEwCKPHImzcSatA6EvFepCd7oCzOkuY7jEyb0kaWQ2ilRj9JQKZEZLSD4wFWuiRhGI4B1MYeLEkfDr5eTvkyRWYyWH7nfuwiUsDtD62NL-QoxsNQbRAdbQJdsMYcGSgKJ2MBMquwgQKOuBVA662AJs6R3tg41Rmg1KOTVY6tWwZ1ZOqd+Tp0hpnUZly85TRmpQpsXz-ZWR7H2TZND3a3wWSgOA8R+RLMmQcwK+1gqPknkqOBftWoc1qUeFAaz-wIEAuXf2-plEhI0fULROjBlSKwfHEmINOhZmWCo0JwrwnZN7rkywKA+wQE2JjJACQwBqo1VqgAUhAcUDK5gxESWqJpbdZkB0aE0ZkMkegqNXj08ZIxsAIGAGqqAcAIA2SgGseV0gxVsweCM7ez8kJZk9d6ygfqA1BsFdMr8tqrJ+C0IspU7JsCZo5UqdZktqEyFlnQ++ezLnUo1gdT+py9aRTGbFf+3s7lNVtrnZ5jsJofKwNin5cy60AtLlA-0SD0AoLBZHaO8ZMFtS8bC3B2YEX5iRUQlFbq0UF04JikuV9CU33lpSwVVbFy0uOeuURZzIrBoJSW2ht8M1rSWZe+QJ7vlnvgPENi9TGVXvlDe4d4avyulzU+gtXKeUzP7XaxuULW4dRBlmZVTEUZeG9dq3VaH5SIGDLAYA2BPWEHuY02eI7Syk3JpTamvRjBQuGcBruEjU2e0KtIdVTIUDSAAEKFtvV8+oHB2Poh5HoAwzDX6HLYTIIThgRP6GVM2tj3A22TXBGAwFZH6hfPBdO5qzdPEdQAKw+LwcuwahY131AxIJ1Q45iBtogJNd5a7iQ7o00BuEWmZPcertypj5kgWwdnTCkGjHEZAA)

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
