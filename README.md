# Portfolio
This repository contains examples of how I prioritize tasks and solve problems. It is meant as a way to show potential employers what they can expect from me, as a developer.

For anyone not trying to employ me, this is just an example of how I would build a micro-service based infrastructure, for looking up information in an environment centered around an HBASE index, using spring boot for implementation. 

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
|**endpoints**|ul><li>/HbaseIndexingService/getSchema</li><li>/HbaseIndexingService/getSamples</li><li>/HbaseIndexingService/getDataType</li><li>/HbaseIndexingService/invertedIndexLookup</li><li>/HbaseIndexingService/getStatistics</li><li>/HbaseIndexingService/getReferences</li>|

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
|**endpoints**|ul><li>/RenderService/renderAsSimpleRep</li><li>/RenderService/renderAsText</li><li>/RenderService/renderAsHtml</li>|

One could argue that this service contains functionality that should be a part of the HBASE index service, but I have chosen to separate them, because I expect that the teams maintaining either service will be very different (HBASE specialists vs. front-end specialists). Still, it is heavily model dependent, and will likely need to be updated any time the HBASE index service is updated.

When started, it will generate a sample data set (based on a random seed, default 1234) using a sample data model, both of which are exposed to the infrastructure on demand. 

### Eureka
_Simple discovery service implementation_

[Source](https://github.com/ViktorKob/portfolio/tree/master/infrastructure "Infrastructure root")

| Settings | |
|---|---|
|**port**|8000|
|**Technologies**|Eureka, Spring|
|**endpoints**|<ul><li>/</li><li>/eureka/*</li>|

This is my discovery service for the infrastructure. It doesn't really contain any code, just configuration. When using a reverse-proxy setup, the status page is used as the root entry point for the server. All standard Eureka endpoints are accessible through /eureka.

### Nginx
_Reverse proxy for hiding ports and simplifying some endpoints_

[Source](https://github.com/ViktorKob/portfolio/tree/master/setup/nginx "Nginx root")

| Settings | |
|---|---|
|**port**|80|
|**Technologies**|Nginx|
|**endpoints**|<ul><li>__All of the above__</li><li>/schema.json</li><li>/graphql/*</li>|

Reverse proxy for the entire setup. May seem counter intuitive in a Eureka setup, but is used to hide ports and simplify the graphql / graphiql endpoints.
