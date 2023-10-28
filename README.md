# Getting Started

### Reference Documentation
This project would be helpful to understand how chain of shell commands(Workflow) can be executed synchronously and publish the log produced by the command to multiple subscribers.
In this example, we are publishing the logs over Websocket.

Dependencies used: RxJava3

Note:
1. You'll need to change dummy commands as per your need.
2. Workflow is by default started when the application starts and can be re-run using the provided endpoint = http://localhost:8082/workflow/start
3. Postman can be used to connect to the websocket = ws://localhost:8082/subscribe/logs