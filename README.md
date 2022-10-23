# SDLE First Assignment

SDLE First Assignment of group 16.

Group members:

1. André Flores (up201907001@edu.fe.up.pt)
2. António Oliveira (up202204184@edu.fe.up.pt)
3. Diogo Faria (up201907014@edu.fe.up.pt)
4. Tiago Rodrigues (up201906807@edu.fe.up.pt)

## Compilation and Running

To compile the project, run:

```bash
mvn package
```

in the root directory of the project.

Afterwards there will be a target folder with a file called **proj1-1.0-SNAPSHOT-jar-with-dependencies.jar**.

To run the project you must run this jar file.

### Server

There is only one way to run the server:

```bash
java -cp proj1-1.0-SNAPSHOT-jar-with-dependencies.jar server.ConcreteServer
```

To terminate the server send an interrup signal.

### Client

The client may be run two different ways

To simply put a message in the server:

```bash
java -cp proj1-1.0-SNAPSHOT-jar-with-dependencies.jar client.ConcreteClient <put> <topic> <message>
```

To run fully initialize the client:

```bash
java -cp proj1-1.0-SNAPSHOT-jar-with-dependencies.jar client.ConcreteClient <id>
```
