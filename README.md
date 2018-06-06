# Portfolio
This repository contains examples of how I prioritize tasks and solve problems. It is meant to give potential employers an idea of, what they can expect from me as a developer.

For anyone not wishing to employ me, this is an example of how I would build a micro-service based infrastructure, for looking up information in an environment centered around an HBASE index, using spring boot for implementation.

# Quickstart
### To just see the infrastructure
Either click [selector query example](http://35.163.101.53/NexusService/graphiql?query=query%20ExampleSelectorLookup(%24uid%3A%20String%2C%20%24username%3A%20String!%2C%20%24justification%3A%20String)%20%7B%0A%20%20Localname(uid%3A%20%24uid)%20%7B%0A%20%20%20%20headline%0A%20%20%20%20statistics(user%3A%20%24username%2C%20justification%3A%20%24justification)%20%7B%0A%20%20%20%20%20%20dayTotal%0A%20%20%20%20%20%20weekTotal%0A%20%20%20%20%20%20quarterTotal%0A%20%20%20%20%20%20infinityTotal%0A%20%20%20%20%7D%0A%20%20%20%20knowledge%20%7B%0A%20%20%20%20%20%20alias%0A%20%20%20%20%20%20isKnown%0A%20%20%20%20%20%20isRestricted%0A%20%20%20%20%7D%0A%20%20%20%20events(user%3A%24username%2C%20justification%3A%20%24justification)%20%7B%0A%20%20%20%20%20%20Document%7B%0A%20%20%20%20%20%20%20%20timeOfEvent%0A%20%20%20%20%20%20%20%20...%20on%20Email%20%7B%0A%20%20%20%20%20%20%20%20%20%20from%20%7B%0A%20%20%20%20%20%20%20%20%20%20%20%20headline%0A%20%20%20%20%20%20%20%20%20%20%20%20displayedName%20%7B%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20name%0A%20%20%20%20%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20%20%20%20%20address%20%7B%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20headline%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20localname%20%7B%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20name%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20domain%20%7B%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20domainPart%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20domain%20%7B%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20domainPart%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20domain%20%7B%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20domainPart%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20domain%20%7B%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20domainPart%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20domain%20%7B%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20domainPart%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%7D%0A%20%20%7D%0A%7D&operationName=ExampleSelectorLookup&variables=%7B%0A%20%20"uid"%3A%20"A7FDF0BF6E361F9258C1161ECE1115B4"%2C%0A%20%20"username"%3A%20"me"%2C%0A%20%20"justification"%3A%20"Because...%20Reasons..."%0A%7D 
or 
[email query example](http://35.163.101.53/NexusService/graphiql?query=query%20SampleEmailQuery(%24uid%3A%20String%2C%20%24username%3A%20String!%2C%20%24justification%3A%20String)%20%7B%0A%20%20Email(uid%3A%20%24uid)%20%7B%0A%20%20%20%20uid%0A%20%20%20%20type%0A%20%20%20%20headline%0A%20%20%20%20html%0A%20%20%20%20references%20%7B%0A%20%20%20%20%20%20originalId%0A%20%20%20%20%20%20source%0A%20%20%20%20%20%20classifications%0A%20%20%20%20%7D%0A%20%20%20%20timeOfEvent%0A%20%20%20%20timeOfInterception%0A%20%20%20%20formattedTimeOfEvent%0A%20%20%20%20formattedTimeOfInterception%0A%20%20%20%20subject%0A%20%20%20%20message%0A%20%20%20%20from%20%7B%0A%20%20%20%20%20%20uid%0A%20%20%20%20%20%20type%0A%20%20%20%20%20%20headline%0A%20%20%20%20%20%20displayedName%20%7B%0A%20%20%20%20%20%20%20%20uid%0A%20%20%20%20%20%20%20%20type%0A%20%20%20%20%20%20%20%20headline%0A%20%20%20%20%20%20%20%20html%0A%20%20%20%20%20%20%20%20simpleRep%0A%20%20%20%20%20%20%20%20name%0A%20%20%20%20%20%20%20%20statistics(user%3A%20%24username%2C%20justification%3A%20%24justification)%20%7B%0A%20%20%20%20%20%20%20%20%20%20dayTotal%0A%20%20%20%20%20%20%20%20%20%20weekTotal%0A%20%20%20%20%20%20%20%20%20%20quarterTotal%0A%20%20%20%20%20%20%20%20%20%20infinityTotal%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20address%20%7B%0A%20%20%20%20%20%20%20%20uid%0A%20%20%20%20%20%20%20%20type%0A%20%20%20%20%20%20%20%20headline%0A%20%20%20%20%20%20%20%20html%0A%20%20%20%20%20%20%20%20simpleRep%0A%20%20%20%20%20%20%20%20statistics(user%3A%20%24username%2C%20justification%3A%20%24justification)%20%7B%0A%20%20%20%20%20%20%20%20%20%20dayTotal%0A%20%20%20%20%20%20%20%20%20%20weekTotal%0A%20%20%20%20%20%20%20%20%20%20quarterTotal%0A%20%20%20%20%20%20%20%20%20%20infinityTotal%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%7D%0A%20%20%20%20to%20%7B%0A%20%20%20%20%20%20uid%0A%20%20%20%20%20%20type%0A%20%20%20%20%20%20headline%0A%20%20%20%20%20%20html%0A%20%20%20%20%20%20displayedName%20%7B%0A%20%20%20%20%20%20%20%20uid%0A%20%20%20%20%20%20%20%20type%0A%20%20%20%20%20%20%20%20headline%0A%20%20%20%20%20%20%20%20html%0A%20%20%20%20%20%20%20%20simpleRep%0A%20%20%20%20%20%20%20%20name%0A%20%20%20%20%20%20%20%20statistics(user%3A%20%24username%2C%20justification%3A%20%24justification)%20%7B%0A%20%20%20%20%20%20%20%20%20%20dayTotal%0A%20%20%20%20%20%20%20%20%20%20weekTotal%0A%20%20%20%20%20%20%20%20%20%20quarterTotal%0A%20%20%20%20%20%20%20%20%20%20infinityTotal%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20address%20%7B%0A%20%20%20%20%20%20%20%20uid%0A%20%20%20%20%20%20%20%20type%0A%20%20%20%20%20%20%20%20headline%0A%20%20%20%20%20%20%20%20html%0A%20%20%20%20%20%20%20%20simpleRep%0A%20%20%20%20%20%20%20%20statistics(user%3A%20%24username%2C%20justification%3A%20%24justification)%20%7B%0A%20%20%20%20%20%20%20%20%20%20dayTotal%0A%20%20%20%20%20%20%20%20%20%20weekTotal%0A%20%20%20%20%20%20%20%20%20%20quarterTotal%0A%20%20%20%20%20%20%20%20%20%20infinityTotal%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%7D%0A%09%09rawData%0A%20%20%7D%0A%7D&operationName=SampleEmailQuery&variables=%7B%0A%20%20%22uid%22%3A%20%22E6AD3476223E6DBDF15B73F28D036ACB%22%2C%0A%20%20%22username%22%3A%20%22me%22%2C%0A%20%20%22justification%22%3A%20%22Because...%20Reasons...%22%0A%7D). **Note that this doesn't seem to work in IE**

To log in, use **service-user** and **password**.

Click the play button, and you are running a query on the AWS instance I have setup for this purpose. 

Feel free to experiment, but also note that the resources are very limited (AWS t2.medium service with 2 cores and 4 GiB of ram, that is running 8 services simultaneously). If it is very slow, or starts hanging and/or failing, either scale back your queries, or come back at a later time and try again.  

To familiarize yourself with GraphiQL, I recommend going to [their introduction of queries](https://graphql.github.io/learn/queries/). The model can be browsed directly from the tool (in the right pane), and it will try to auto-complete queries when typing. 

### For a local setup
- To experiment locally, check out the entire repository.
- I recommend hooking it up to an IDE (Eclipse project files are included, and there are three pom.xml's you can import as well).
- Build everything (first tools, then infrastructure, then services).
- Start the infrastructure service from the infrastructure folder. 
- Run each service using its respective *Application.java. Order should not matter, if you start all of them shortly after each other.

Now you can do as described above, but locally. Note that unless you also setup a local reverse proxy, you will need to specify ports directly when running queries, though. An example is the nexus, that should be running at (localhost:8100/NexusService/graphiql).

# Status at the moment
The project contains a set of services:

### Nexus
_GraphQL service that enables easy access to the other services_

[Source](https://github.com/ViktorKob/portfolio/tree/master/services/nexus-service "Nexus root")

| Settings | |
|---|---|
|**Port**|8100|
|**Technologies**|Graph(i)QL, Spring|
|**User**|service-user|
|**Password**|password|
|**Endpoints**|<ul><li>/schema.json</li><li>/graphql</li><li>/graphiql</li><li>/vendor</li>|

Here every other service is tied together. Using the GraphiQL interface, it is possible to transparently interact with the data in the other services. 

### HBASE Index
_Fake HBASE service, allowing for model-discovery and emulating lookups in HBASE tables_

[Source](https://github.com/ViktorKob/portfolio/tree/master/services/hbase-indexing-service "HBASE indexing root")

| Settings | |
|---|---|
|**Port**|8120|
|**Technologies**|Spring|
|**User**|service-user|
|**Password**|password|
|**Endpoints**|<ul><li>/HbaseIndexingService/{version}/schema</li><li>/HbaseIndexingService/{version}/documents/{type}/samples</li><li>/HbaseIndexingService/{version}/documents/{type}/{uid}</li><li>/HbaseIndexingService/{version}/documents/{type}/{uid}/references</li><li>/HbaseIndexingService/{version}/selectors/{type}/samples</li><li>/HbaseIndexingService/{version}/selectors/suggestions/{string}</li><li>/HbaseIndexingService/{version}/selectors/{type}/{uid}</li><li>/HbaseIndexingService/{version}/selectors/{type}/{uid}/statistics</li><li>/HbaseIndexingService/{version}/selectors/{type}/{uid}/invertedIndex</li><li>/HbaseIndexingService/{version}/entities/{type}/samples</li><li>/HbaseIndexingService/{version}/entities/{type}/{uid}</li></ul>|

The purpose of this service is to emulate lookups into HBASE tables. This should be seen as an index build on top of whatever data is ingested into the infrastructure, with a data model representing the content of the index.

When started, it will generate a sample data set (based on a random seed, default 1234) using a sample data model, both of which are exposed to the infrastructure on demand. 

### Render
_Rendering service that can lookup data in the Hbase index and then renderer it in a meaningful manner_

[Source](https://github.com/ViktorKob/portfolio/tree/master/services/render-service "Render root")

| Settings | |
|---|---|
|**Port**|8150|
|**Technologies**|Spring|
|**User**|service-user|
|**Password**|password|
|**Endpoints**|<ul><li>/RenderService/renderAsSimpleRep</li><li>/RenderService/renderAsText</li><li>/RenderService/renderAsHtml</li></ul>|"

One could argue that this service contains functionality that should be a part of the HBASE index service, but I have chosen to separate them, because I expect that the teams maintaining either service will be very different (HBASE specialists vs. front-end specialists). Still, it is heavily model dependent, and will likely need to be updated any time the HBASE index service is updated.

_Note that HTML rendering is not implemented yet_. 

### Usage data
_Service responsible for storing and showing user interaction with the model_

[Source](https://github.com/ViktorKob/portfolio/tree/master/services/usage-data-service "Usage data root")

| Settings | |
|---|---|
|**Port**|8200|
|**Technologies**|MySQL, jOOQ, Spring|
|**User**|service-user|
|**Password**|password|
|**Endpoints**|<ul><li>/UsageDataService/fetchUsageActivity</li><li>/UsageDataService/storeUsageActivity</li></ul>|

To enable storage of data about usage of the data model, this service employ a mysql backend and exposes two endpoint for manipulating the contents of this. jOOQ is used as a middle layer to enable compile-time validation of SQL queries. 

On startup it will attempt to contact a mysql-server as specified in the properties and make sure it contains a database schema with the name "usage-data". If the server doesn't contain one, it will be created with the necessary tables. If it does, it will assume that it has the correct structure and attempt to use it. 

### Analytics
_Fake service representing interaction with the analytical information in the company_

[Source](https://github.com/ViktorKob/portfolio/tree/master/services/analytics-service "Analytics root")

| Settings | |
|---|---|
|**Port**|8300|
|**Technologies**|Spring|
|**User**|service-user|
|**Password**|password|
|**Endpoints**|<ul><li>/AnalyticsService/lookupPriorKnowledge</li></ul>|

Another fake service, this time representing the existing analytical knowledge in the company, outside this infrastructure.

### Legal
_Legal service responsible for validating legal requirements and audit logging model access_

[Source](https://github.com/ViktorKob/portfolio/tree/master/services/legal-service "Legal root")

| Settings | |
|---|---|
|**Port**|8350|
|**Technologies**|Spring|
|**User**|service-user|
|**Password**|password|
|**Endpoints**|<ul><li>/LegalService/checkLegalityOfQueryOnSelector</li><li>/LegalService/auditLogInvertedIndexLookup</li><li>/LegalService/auditLogStatistics</li></ul>|

The purpose here is to enclose all logic related to legal requirements into a separate service, to allow the developers working with the legal department to focus on a simple API instead of actual usage scenarios. 

It has two responsibilities:
- allow the system to check the legality of a query before actually executing it
- audit log the execution of a query into protected data on demand  

### Eureka
_Simple discovery service implementation_

[Source](https://github.com/ViktorKob/portfolio/tree/master/infrastructure "Infrastructure root")

| Settings | |
|---|---|
|**Port**|8000|
|**Technologies**|Eureka, Spring|
|**Endpoints**|<ul><li>/</li><li>/eureka/*</li></ul>|

This is my discovery service for the infrastructure. It doesn't really contain any code, just configuration. When using a reverse-proxy setup, the status page is used as the root entry point for the server. All standard Eureka endpoints are accessible through /eureka.

### Nginx
_Reverse proxy for hiding ports and simplifying some endpoints_

[Source](https://github.com/ViktorKob/portfolio/tree/master/setup/nginx "Nginx root")

| Settings | |
|---|---|
|**Port**|80|
|**Technologies**|Nginx|
|**Endpoints**|<ul><li>__All of the above__</li><li>/schema.json</li><li>/graphql/*</li></ul>|

Reverse proxy for the entire setup. May seem counter intuitive in a Eureka setup, but is used to hide ports and simplify the graphql / graphiql endpoints.

# Plans for the future
- [ ] Automated service level acceptance testing
- [ ] Failover for services and red-green deployment of changes 
- [ ] Make Eureka and the clients AWS aware
- [ ] Add OpenAPI 3 integration to the services
- [x] Add support for the build-in spring rest endpoints (info, health, mappings, metrics, etc.)
- [ ] Add circuit breakers where relevant
- [ ] Add Hystrix integration
- [ ] Look into replacing / supplementing proxy with Zuul
- [ ] Add authentication layer, e.g. using UAA, JWS, OAuth2, JWT, JWA and OpenID
- [ ] Containerization of services (probably using Docker)
