# Assignment 1 - Group 16

## Introduction

In this assignment, we were tasked to design and implement a reliable publish-subscribe service.
Using it, a user must be able to put a message in any topic, so that other users subscribed to it can get those messages.
Aside from that, the service should guarantee "exactly-once" delivery and handle possible failures.

## Implementation

The system was implemented in Java, using JeroMQ.

### Server

The server is implemented as an instance of a runnable class **ConcreteServer** that implements the **Server** interface.
The **Server** interface only has two methods specified, *receive()* which returns a **ReceivePair** and *send()* which returns void.

A **ReceivePair** is a record with two fields, an address (byte[]) and a message (Message), the address being the address of the client that sent the message. 

The server uses a *ROUTER* socket to receive requests and send messages from and to clients. We used a *ROUTER* socket 
instead of a *REP* socket because this way we could receive multiple requests before responding since when a *REP* 
socket receives a message it goes into a state where it can only reply and not receive more messages. The *ROUTER* socket
could also allow us to route incoming messages to worker threads through *inter-process communication* if we wanted, but 
we chose to use **Runnable** handlers and an **ExecutorService** thread pool instead.

Incoming messages are handled through instances of subclasses of **Handler**, an abstract class that implements the 
**Runnable** interface. Each type of message the server is capable of handling has its own **Handler** subclass, which is
instantiated after the server receives the message. The **Handler** instance is then submitted to the **ExecutorService**
which handles task scheduling.

### Client

The client is implemented as an instance of a runnable class **ConcreteClient** that implements the **Client** interface.
The **Client** interface has seven methods specified: 
- *send()*: to send a message to the server;
- *receive()*: to receive a message from the server
- *get(String topic)*: to get a message from a specific topic
- *get()*: to get a message for each of the topics the client is subscribed to
- *put()*: to publish a message to a topic
- *subscribe()*: 
The client uses a *REQ* socket to send requests and receive replies. When the client makes a request it must receive a 
reply to that request or go through the timeout procedure before being able to do make any other request. 

### Messages

The __Messages__ are the communication between Server and Clients and are divided into 2 categories: __serverMessages__ and __clientMessages__.

All the specific __Messages__ extends the **Message** abstract class.

The __clientMessages__ are messages that the client sends to server, in this case, represents all the requests sent to the server.

There are 5 types of __clientMessages__:
 - _GetMessage_: Consume the last unread message of the specified topic from the server
 - _GetMessageAck_:
 - _PutMessage_: Publish a message on the specified topic in the server
 - _ShutdownServerMessage_: Shutdown the server
 - _SubscribeMessage_: Subscribe specified topic
 - _UnsubscribeMessage_: Unsubscribe specified topic

The __serverMessages__ are messages that the server send to client, in this case, represents all the replies sent to client.

There are 5 types of __serverMessages__:
 - _NotSubscribedMessage_: Serves as an acknowledgment message that tells to the client that are not subscribed to a topic.
 - _ShutdownReplyMessage_: Serves as an acknowledgment message that tells to the client that the server is shutdown.
 - _PutReplyMessage_: Serves as an acknowledgment message that guarantees to the client that the message was put on the topic.
 - _SubscriptionReplyMessage_: Sends the result of subscribe/unsubscribe operation. The responses are: __SUBSCRIBED__ if
the client is now subscribed to a topic, __UNSUBSCRIBED__ if the client is not subscribed to a topic and __ERROR__ if occurs an error.
 - _TopicArticleMessage_: Sends a message from a topic to the client. Usually is the reply of a get operation.

## Design

The application followed a client-server architecture. The main class _ConcreteServer_ runs the main server and includes the necessary functions and handlers
to treat incoming requests.
The main class _ConcreteClient_ allows a user to get or put messages and subscribe or unsubscribe to topics. This is done by typing simple messages in the terminal (ex: ```get [topic]```).  


## Failing Circumstances

# Exactly-once on put

In a __get__ request, the following happens, sequentially:
- A client sends a _GetMessage_, with the topic in which they want the message from.
- If the client is subscribed to the topic, the server replies with a _TopicArticleMessage_, if it is not subscribed, it will send a _NotSubscribedMessage_ and if the topic doesn't exist, it will reply with a _NonExistentTopicMessage_  
- Should the client received a _TopicArticleMessage_, it will then send an _GetMessageAckHandler_ to the server. Otherwise, it won't send anything else.
- Finally, having sent a message before, the client will then receive an _AckMessage_ from the server.

By having the client acknowledging that it received a message from a topic, it will ensure that the client won't be sent back the same message once again.

# Exactly-once on get

# Contingency plan

Whenever a new topic, message or subscriber is added to the system, the server saves that data on non-volatile storage.
As such, were a server to crash, it wouldn't lose any previous data, aside from anything being currently handled.

