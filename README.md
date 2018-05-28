# Portfolio
This repository contains examples of how I prioritize tasks and solve problems. It is meant as a way to show potential employers what they can expect from me, as a developer.

For anyone not trying to employ me, this is just an example of how I would build a micro-service based infrastructure, for looking up information in an environment centered around an HBASE index, using spring boot for implementation. 

# Status at the moment
The project contains a set of services:

### Eureka
_Simple discovery service implementation_

[Source](https://github.com/ViktorKob/portfolio/tree/master/infrastructure "Infrastructure root")

|*Default port*|8000|
|*Technologies*|Eureka, Spring|

This is my discovery service for the infrastructure. It doesn't really contain any code, just configuration. When using a reverse-proxy setup, the status page is used as the root entry point for the server.





