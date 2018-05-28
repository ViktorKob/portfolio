# Portfolio
This repository contains examples of how I prioritize tasks and solve problems. It is meant to give potential employers an idea of, what they can expect from me as a developer.

For anyone not wishing to employ me, this is an example of how I would build a micro-service based infrastructure, for looking up information in an environment centered around an HBASE index, using spring boot for implementation.

# Status at the moment
The project contains a set of services:


### Nexus
_GraphQL service that enables easy access to the other services_

[Source](https://github.com/ViktorKob/portfolio/tree/master/services/nexus-service "Nexus root")

| Settings | |
|---|---|
|**port**|8100|
|**Technologies**|Graph(i)QL, Spring|
|**User**|service-user|
|**Password**|password|
|**endpoints**|<ul><li>/NexusService/schema.json</li><li>/NexusService/graphql</li><li>/NexusService/graphiql</li><li>/NexusService/vendor</li>|

Here every other service is tied together. Using the GraphiQL interface, it is possible to transparently interact with the data in the other services. 

### HBASE Index
_Fake HBASE service, allowing for model-discovery and emulating lookups in HBASE tables_

[Source](https://github.com/ViktorKob/portfolio/tree/master/services/hbase-indexing-service "HBASE indexing root")

| Settings | |
|---|---|
|**port**|8120|
|**Technologies**|Spring|
|**User**|service-user|
|**Password**|password|
|**endpoints**|<ul><li>/HbaseIndexingService/getSchema</li><li>/HbaseIndexingService/getSamples</li><li>/HbaseIndexingService/getDataType</li><li>/HbaseIndexingService/invertedIndexLookup</li><li>/HbaseIndexingService/getStatistics</li><li>/HbaseIndexingService/getReferences</li></ul>|

The purpose of this service is to emulate lookups into HBASE tables. This should be seen as an index build on top of whatever data is ingested into the infrastructure, with a data model representing the content of the index.

When started, it will generate a sample data set (based on a random seed, default 1234) using a sample data model, both of which are exposed to the infrastructure on demand. 

### Render
_Rendering service that can lookup data in the Hbase index and then renderer it in a meaningful manner_

[Source](https://github.com/ViktorKob/portfolio/tree/master/services/render-service "Render root")

| Settings | |
|---|---|
|**port**|8150|
|**Technologies**|Spring|
|**User**|service-user|
|**Password**|password|
|**endpoints**|<ul><li>/RenderService/renderAsSimpleRep</li><li>/RenderService/renderAsText</li><li>/RenderService/renderAsHtml</li></ul>|

One could argue that this service contains functionality that should be a part of the HBASE index service, but I have chosen to separate them, because I expect that the teams maintaining either service will be very different (HBASE specialists vs. front-end specialists). Still, it is heavily model dependent, and will likely need to be updated any time the HBASE index service is updated.

_Note that HTML rendering is not implemented yet_. 

### Usage data
_Service responsible for storing and showing user interaction with the model_

[Source](https://github.com/ViktorKob/portfolio/tree/master/services/usage-data-service "Usage data root")

| Settings | |
|---|---|
|**port**|8200|
|**Technologies**|MySQL, jOOQ, Spring|
|**User**|service-user|
|**Password**|password|
|**endpoints**|<ul><li>/UsageDataService/fetchUsageActivity</li><li>/UsageDataService/storeUsageActivity</li></ul>|

To enable storage of data about usage of the data model, this service employ a mysql backend and exposes two endpoint for manipulating the contents of this. jOOQ is used as a middle layer to enable compile-time validation of SQL queries. 

On startup it will attempt to contact a mysql-server as specified in the properties and make sure it contains a database schema with the name "usage-data". If the server doesn't contain one, it will be created with the necessary tables. If it does, it will assume that it has the correct structure and attempt to use it. 

### Analytics
_Fake service representing interaction with the analytical information in the company_

[Source](https://github.com/ViktorKob/portfolio/tree/master/services/analytics-service "Analytics root")

| Settings | |
|---|---|
|**port**|8300|
|**Technologies**|Spring|
|**User**|service-user|
|**Password**|password|
|**endpoints**|<ul><li>/AnalyticsService/lookupPriorKnowledge</li></ul>|

Another fake service, this time representing the existing analytical knowledge in the company, outside this infrastructure.

### Legal
_Legal service responsible for validating legal requirements and audit logging model access_

[Source](https://github.com/ViktorKob/portfolio/tree/master/services/legal-service "Legal root")

| Settings | |
|---|---|
|**port**|8350|
|**Technologies**|Spring|
|**User**|service-user|
|**Password**|password|
|**endpoints**|<ul><li>/LegalService/checkLegalityOfQueryOnSelector</li><li>/LegalService/auditLogInvertedIndexLookup</li><li>/LegalService/auditLogStatistics</li></ul>|

The purpose here is to enclose all logic related to legal requirements into a separate service, to allow the developers working with the legal department to focus on a simple API instead of actual usage scenarios. 

It has two responsibilities:
- allow the system to check the legality of a query before actually executing it
- audit log the execution of a query into protected data on demand  

### Eureka
_Simple discovery service implementation_

[Source](https://github.com/ViktorKob/portfolio/tree/master/infrastructure "Infrastructure root")

| Settings | |
|---|---|
|**port**|8000|
|**Technologies**|Eureka, Spring|
|**endpoints**|<ul><li>/</li><li>/eureka/*</li></ul>|

This is my discovery service for the infrastructure. It doesn't really contain any code, just configuration. When using a reverse-proxy setup, the status page is used as the root entry point for the server. All standard Eureka endpoints are accessible through /eureka.

### Nginx
_Reverse proxy for hiding ports and simplifying some endpoints_

[Source](https://github.com/ViktorKob/portfolio/tree/master/setup/nginx "Nginx root")

| Settings | |
|---|---|
|**port**|80|
|**Technologies**|Nginx|
|**endpoints**|<ul><li>__All of the above__</li><li>/schema.json</li><li>/graphql/*</li></ul>|

Reverse proxy for the entire setup. May seem counter intuitive in a Eureka setup, but is used to hide ports and simplify the graphql / graphiql endpoints.
