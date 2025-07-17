# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

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

https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+uB5afJCIJqTsXzQo8wHiVQSIwAgQnihignCQSRJgKSb6GLuNL7gyTJTspXI3l5d5LsKMAAGbiuuboynKZbvEqmAqsGGpukaHAQGoaAAOTMFaaKBbywWWdZPZ9tuHmWf6AByEBkD4IDxG6UYxnGhRaUmlSifUACseEETmqh5vMhbFtArpEYY6VqDAaAQLl1o5A29GJaqGoAOreA4MDGQCMAFQuAodR2Vkuhu7pbu5IHVP6G0FPYTT6H8OzNSgsYKeh8LJsgqYwOmACM-X8kNBZjEWJb1AAkmgIDQCi4AwA9WwmXRTbDg6R3ueNm7yOVgpo-Sh5yCgz7xOel7Xvjh2VA+a4BuTF3OhVlQ6aWjnihkqgAZgLNXRJYGEfpw2kd8FFUfWJGoQ27VfSUYA4X1owC-FxHC7Bl5i8hEu0Y2DGMd4fj+F4KDoDEcSJEbJuOb4WCiYKoH1A00gRvxEbtBG3Q9HJqgKcMouIegH3aeZ-p+0h3PB1AxWnbZ9jWw5QnW85aiuStyUwOKMPFig8MUTAkBh5TvMnV2dMvgznYY9dpZQ5ncNgCTGsvW98bS5133Yb1+FoDA2ZA-m0Fg2NMA17D2fMCTMAa0tqO3ujUclyTr6M+2lP1BwKDcMe6v+2gZPwTvc5Bej1PChkMwQDQpdXuXxftiz9RW6eHNczzld8y8mlV5h7dyzAuFdyMFGusmIGxROufw2BxQan4miGAABxJUGhbaVVLA0OBrsPb2CVL7beYd2rMwjvUUOAdX7zwZIghyaIk4klxp5Qq-IfL10vHvSiB99r7nvKFCKEoG6IVlPKYhhQkrqnTtDUeOdLx5x3uwoqvpGb1EXjfUQb8aiQzEVncAvCkJN1aoHI6XVO7DB7mMAawMB6jWruouuV9J472ngxQuKjrKKJxpdOhB0H5ogQXMGR-IyEwAyGAHw7wYAgGCSiUgWCcy0I-IQgSXilTPwQIBCORddLLCiWoAsDRxiZIhtIAsf1wjBECCCTY8RdQoFSpBRY3xkigDVNUgyYxviZKqkqWpFwYCdE-nzb+st5YAIyYg7JuSlT5MKcU0pyxymVKafMWpIJ6kgEaRNPYrSlTtLmJ07pQC9bMX8BwAA7G4JwKAnAxAjMEOAXEABs8AJyGG8YYIoP87ZVwdq0DomDsHTFwegLMbSlS9MTAQ2E6SxhAuaUsMy4L4RyM7PUQm6JnkYmRSgZ51CU5uKpLPekMBGRgFRVCgKjiT71DFBKTJ-CCWbISsIjU1KjQmjNBaBaYBbR4sOv455S9OxpNLFs4mVZQzhjQDo96rd4A-x6grYxpj+4jXBvAiszKRU1jFfY3xi5-GlX7DimQXKkWPOJUqCmXLOEKNppkvlt8jr31CROTF-5knhzhQKl4wy5gTPqEUkpMAQUYX0TKv+CsRhepQD6mAfrAgBp1vsg2lgN62U2KbJACQwBJr7BAVNAApCA4oVVzBiMstUrzZbvPfo0JozIZI9EyTg-eSEszYAQMAJNUA4AQFslANYeTpCBvhQ8OFnrIV0uhXG1+CLi71AAFYFrQKi-N4pnWEmTm5Rm7jvIEqZCTDEWj0CH3oYuclMAABqlAkBhXlAeoRq1h5WLHsaS8+cA6OP8S44AMTp3+hHhophTbIzRleroqVXUYCGMVgqhZSqh5-usRPKeeyGVFsMNIDKqhsrMCjPld907rK8qUe+H9gqIDPIlS3L+waBkQblb3XMirQYWPqM84e0gjAYZmnNGAeVFp7Lw8dAjZqiNM2HV+eoy7F2JJdSk91KjdKDv6T9f+WY9kJoCF4dtaaM2aflIgYMsBgDYFbYQPIBQ9rIJIw7J2Ls3Ye2MB9WJI7QncDwEYbQegDBuq-JWmdLmDMYn03gLFG7+VbpHEi1zsA3iiLChAbVVMhSRYM0KVOIiRTgmCYYSA-m8AJd1b2fVy8UEUsyyiBCQWoAUbalRmWP1IPyr7jBpjyqMunBQOuHLIAotaoE5jHuILBxOfEwN7zFkSujbAyGlTowgFAA

NEW UPDATED URL: 
https://sequencediagram.org/index.html#initialData=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-EyicnL-xtNDnSzpYETlzEuTAa5gQaXOKll9NBWZU25OkEMkZWaQmeF1gESwD9bKbGNkgBIYBq19ggHWgAUhAcUZCKz+AWSANUqLpborgY0JozIZI9Bycw7OSEszYGedWqAcAIC2SgGsAZ0hiUYQaXSl4haNkrKWLSr8w7GUwAAFadrQKyjt4o42EkTm5LVvLP6+skZnImasn2dhxj69OgqlTCvkSFeoAA1SgSAwryk-QY1V39wpKMlWFbwMwJEVjyQUkN990RQBmOKQw87gCLuXaupayU7WjwdVXWDwa1XHQ1cAZO1DrGUfADBhx0ZnpOONV1b6Tg-ryy8da4GviWO2MdcEv5srkUyAyqobKzAoz5TdQy6yyKr4-u1aWGqyLDUz0QS401fGl4eKE28m1W8ZN5KMHJqaM0YB5XmiElTh1rIolKeUwjxGV3QCY1GuE9Rb3XpafG9pSatPwJzXPQ5qC5YYL+f8vWXgiP1sbcl+UiBgywGANgedhA8gFB2lQhlkkHZOxdm7Ywb0d3RoqdwPARhtB6AMImk9RSQD1agBiTLeAuXfpAbE7ydWstxTlWFCAmGQPDYa1V2VIpwRlMMJAabUACnut7J66+zGYDzdOIthCPWoB6bagZ3N2EYAAFY5ambXuZkTtrdvcHXMtjrWWXWTaKfF+p-ndLHosttr7p3ot5ri5goAA
