# Portfolio 
[![Build Status](https://travis-ci.org/ViktorKob/portfolio.svg?branch=master)](https://travis-ci.org/ViktorKob/portfolio) [![Coverage Status](https://coveralls.io/repos/github/ViktorKob/portfolio/badge.svg?branch=master)](https://coveralls.io/github/ViktorKob/portfolio?branch=master)

This repository contains examples of how I prioritize tasks and solve problems. It is meant to give potential employers an idea of, what they can expect from me as a developer.

For anyone not wishing to employ me, this is an example of how I would build a micro-service based infrastructure, for looking up information in an environment centered around an HBASE index, using spring boot for implementation and GraphQL to expose the API.

It is very much a work in progress, and I plan on adding new features continuously until I run out of ideas or get the job I want.

### Quick-and-dirty structural diagram
![Rough diagram of the services in the architecture and the flow of data](/images/rough_diagram.png)

The **boxes** are the individual parts of the system, with a *hard* box for the NGINX proxy and *soft* boxes for the Spring services. 
The **ellipses** represent other systems that are integrated with this one (everything, but the services is fake).
The **arrows** represent the flow of data in the system. 

# Quickstart
### To just see the infrastructure (Tested in Chrome and Firefox)

When running the example queries below, login using **service-user** and **password**.

***Not that this cluster is experimental and may be down at times***

| Examples | |
|---|---|
|[Eureka](https://viktorkob.tk/Infrastructure/)|The Eureka server I use for service discovery|
|[Admin](https://viktorkob.tk/Admin#/applications)|A server running the Spring Boot Admin tool, connected to the other services.|
|[Hystrix](https://viktorkob.tk/Infrastructure/hystrix/monitor?stream=https%3A%2F%2Fservice-user%3Apassword%40viktorkob.tk%2FNexusService%2Factuator%2Fhystrix.stream&delay=1000&title=Nexus%20Service%20Monitor)|A Hystrix dashboard for the adaptors that have circuit breakers (only available on the Nexus at the moment). Is less boring if you run some queries while looking at it, and sometimes fails if no-one has run a query for a while.|
|[Selector suggestion](https://viktorkob.tk/NexusService/graphiql?query=%23%20Selector%20suggestion%20example.%20Use%20this%20to%20familiarize%20yourself%20%0A%23%20with%20GraphQL%20if%20needed.%0A%23%20Note%20the%20%3C%20Docs%20button%20on%20the%20top%20right%20(Documentation%20Explorer%20when%20%0A%23%20open)%20for%20browsing%20the%20models%20and%20the%20Query%20variables%20pane%20below%20%0A%23%20for%20entering%20parameters.%0A%23%20The%20History%20button%20above%20may%20also%20become%20relevant%2C%20should%20you%20choose%0A%23%20to%20experiment.%0A%0A%23%20To%20run%20the%20query%2C%20click%20the%20play%20button%20above.%0A%0A%23%20For%20further%20information%2C%20I%20recommend%20going%20to%20the%20source%3A%0A%23%20https%3A%2F%2Fgraphql.github.io%2Flearn%2Fqueries%2F%0A%0A%23%20This%20query%20looks%20up%20the%20text%20string%20%27jhuaajsrlrue%27%20by%20first%20%0A%23%20requesting%20the%20selectors%20that%20match%20the%20string%2C%20and%20then%20asking%0A%23%20for%20the%20uid%20and%20the%20statistics%20for%20each%20of%20these.%0A%0A%23%20To%20fulfill%20this%20request%2C%20we%20use%20the%20schema%20in%20the%20HBase%20service%0A%23%20for%20selector%20resolution%20and%20then%20the%20statistics%20endpoint%20to%20look%0A%23%20up%20each%20suggestion.%20A%20user%20is%20required%20here%2C%20so%20this%20has%20been%20%0A%23%20added%20directly%20to%20the%20query.%0A%0Aquery%20GetSelectorSuggestionsForString(%24simpleRepresentation%3A%20String!%2C%20%24userid%3A%20String!)%20%7B%0A%20%20suggest(simpleRep%3A%20%24simpleRepresentation)%20%7B%0A%20%20%20%20type%0A%20%20%20%20uid%0A%20%20%20%20statistics%20(user%3A%20%24userid)%20%7B%0A%20%20%20%20%20%20dayTotal%0A%20%20%20%20%20%20weekTotal%0A%20%20%20%20%20%20quarterTotal%0A%20%20%20%20%20%20infinityTotal%0A%20%20%20%20%7D%20%0A%20%20%7D%0A%7D&operationName=GetSelectorSuggestionsForString&variables=%7B%0A%20%20%22simpleRepresentation%22%3A%20%22jhuaajsrlrue%22%2C%0A%20%20%22userid%22%3A%20%22userid%22%0A%7D)|Example of how selector suggestions are implemented.|
|[Selector lookup](https://viktorkob.tk/NexusService/graphiql?query=%23%20To%20run%20the%20query%2C%20click%20the%20play%20button%20above.%0A%0A%23%20This%20query%20looks%20up%20the%20text%20string%20%27jhuaajsrlrue%27%20as%20a%20Localname.%20%0A%23%20It%20retrieves%20the%20statistics%20for%20the%20selector%20and%20checks%20whether%20it%20%0A%23%20is%20already%20known%20by%20the%20system.%20Finally%2C%20it%20executes%20a%20lookup%20in%20%0A%23%20the%20inverted%20index%20to%20retrieve%20the%20event%20dates%20for%20the%20first%2020%0A%23%20events%20we%20have%20registered%20for%20it.%0A%0A%23%20Note%20that%20we%20here%20add%20the%20userid%20at%20the%20selector%20level%20instead%20of%0A%23%20during%20the%20statistics%20request%2C%20to%20allow%20it%20to%20be%20used%20for%20both%20%0A%23%20lookups.%20Userids%20only%20have%20to%20be%20specified%20once%20for%20the%20entire%20%0A%23%20query%2C%20when%20use%20in%20this%20manner.%20%0A%23%20In%20a%20similar%20manner%2C%20justifications%20and%20date%20bounds%20are%20shared%20for%0A%23%20a%20specific%20selector%2C%20but%20not%20globally%2C%20since%20these%20are%20selector%0A%23%20dependant.%0A%0A%23%20This%20query%20uses%20multiple%20services%20to%20fullfill%20the%20requests%2C%0A%23%20before%20merging%20everything%20back%20into%20the%20JSON%20inspired%20response%20%0A%23%20format.%0A%0Aquery%20LookupMultipleValuesRelatedToSelector(%24simpleRepresentation%3A%20String!%2C%20%24userid%3A%20String!)%20%7B%0A%20%20Localname(simpleRep%3A%20%24simpleRepresentation%2C%20user%3A%20%24userid)%20%7B%0A%20%20%20%20type%0A%20%20%20%20uid%0A%20%20%20%20statistics%20%7B%0A%20%20%20%20%20%20dayTotal%0A%20%20%20%20%20%20weekTotal%0A%20%20%20%20%20%20quarterTotal%0A%20%20%20%20%20%20infinityTotal%0A%20%20%20%20%7D%0A%20%20%20%20knowledge%20%7B%0A%20%20%20%20%20%20alias%0A%20%20%20%20%20%20isKnown%0A%20%20%20%20%20%20isRestricted%0A%20%20%20%20%7D%0A%20%20%20%20events%20%7B%0A%20%20%20%20%20%20timeOfEvent%0A%20%20%20%20%7D%0A%20%20%7D%0A%7D&operationName=LookupMultipleValuesRelatedToSelector&variables=%7B%0A%20%20%22simpleRepresentation%22%3A%20%22jhuaajsrlrue%22%2C%0A%20%20%22userid%22%3A%20%22userid%22%0A%7D)|Example of how to lookup a selector and data related to it.|
|[Inverted index lookup](https://viktorkob.tk/NexusService/graphiql?query=%23%20To%20run%20the%20query%2C%20click%20the%20play%20button%20above.%0A%0A%23%20This%20query%20looks%20up%20the%20text%20string%20%27jhuaajsrlrue%27%20as%20a%20Localname.%20%0A%23%20It%20executes%20a%20lookup%20in%20the%20inverted%20index%20to%20retrieve%20the%20events%20%0A%23%20that%20occurred%20before%20October%2023.%2C%202017%2C%20where%20this%20localname%20was%20%0A%23%20used%20by%20the%20email%20address%20that%20send%20the%20mail.%20It%20skips%20the%20first%20%0A%23%2020%20elements%20and%20fetches%20the%20next%20100.%0A%0A%23%20For%20each%20element%20that%20is%20an%20Email%20(all%20of%20them%20in%20the%20current%20%0A%23%20schema)%2C%20it%20then%20looks%20up%20the%20localnames%20of%20the%20main%20recipients%20and%0A%23%20lists%20them%20(if%20any%20such%20address%20exists).%0A%0A%23%20The%20purpose%20here%20is%20to%20showcase%20the%20ease%20with%20which%20GraphQL%20%0A%23%20fullfills%20the%20request%2C%20even%20though%20it%20has%20to%20do%20hundreds%20of%20lookups%0A%23%20in%20a%20dual-threaded%20environment%20with%20multiple%20RESTful%20services.%0A%0A%23%20Note%20that%20this%20query%20also%20returns%20an%20error.%20This%20is%20expected%2C%20since%0A%23%20we%20ask%20for%20statistics%20for%20several%20restricted%20selectors%20(e.g.%20%0A%23%20%22fmpqbyrkrasw%22%20and%20%22fwjeichifdo%22).%20Since%20we%20have%20to%20justify%20by%20%0A%23%20selector%2C%20the%20system%20simply%20refuses%20to%20return%20these%20values.%0A%23%20I%20have%20yet%20to%20determine%20how%20to%20best%20make%20GraphQL%20for%20Java%20give%20%0A%23%20usefull%20responses%20on%20errors.%0A%0Aquery%20LookupDocumentsRelatedToSelector(%24simpleRepresentation%3A%20String!%2C%20%24userid%3A%20String!)%20%7B%0A%20%20Localname(simpleRep%3A%20%24simpleRepresentation%2C%20user%3A%20%24userid)%20%7B%0A%20%20%20%20events(offset%3A%2020%2C%20limit%3A%20100%2C%20beforeDate%3A%20%222017-10-23%22%2C%20relations%3A%20%5B%22send%22%5D)%20%7B%0A%20%20%20%20%20%20formattedTimeOfEvent%0A%20%20%20%20%20%20type%0A%20%20%20%20%20%20...%20on%20Email%7B%0A%20%20%20%20%20%20%20%20to%20%7B%0A%20%20%20%20%20%20%20%20%20%20address%7B%0A%20%20%20%20%20%20%20%20%20%20%20%20localname%7B%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20simpleRep%0A%20%20%20%20%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20%20%20%20%20statistics%20%7B%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20dayTotal%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20weekTotal%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20quarterTotal%0A%20%20%20%20%20%20%20%20%20%20%20%20%20%20infinityTotal%0A%20%20%20%20%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20%20%20%7D%20%20%20%20%20%20%20%20%20%20%0A%20%20%20%20%20%20%20%20%7D%20%20%20%20%20%20%20%20%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%7D%0A%20%20%7D%0A%7D&operationName=LookupDocumentsRelatedToSelector&variables=%7B%0A%20%20%22simpleRepresentation%22%3A%20%22jhuaajsrlrue%22%2C%0A%20%20%22userid%22%3A%20%22userId%22%0A%7D)|Example of how to browse values in the inverted index.|
|[Minified GraphQL example](https://viktorkob.tk/NexusService/graphql?query=query%20MinifiedLookupDocumentsRelatedToSelector(%24simpleRepresentation%3AString!%2C%24userid%3AString!)%7BLocalname(simpleRep%3A%24simpleRepresentation%2Cuser%3A%24userid)%7Bevents(offset%3A20%2Climit%3A100%2CbeforeDate%3A%222017-10-23%22%2Crelations%3A%5B%22send%22%5D)%7BformattedTimeOfEvent%20type...on%20Email%7Bto%7Baddress%7Blocalname%7BsimpleRep%7Dstatistics%7BdayTotal%20weekTotal%20quarterTotal%20infinityTotal%7D%7D%7D%7D%7D%7D%7D&operationName=MinifiedLookupDocumentsRelatedToSelector&variables=%7B%22simpleRepresentation%22%3A%22jhuaajsrlrue%22%2C%22userid%22%3A%20%22Suggestion%20user%22%7D)|Example of how the same query looks when run without GraphiQL.|
|[Document lookup](https://viktorkob.tk/NexusService/graphiql?query=%23%20To%20run%20the%20query%2C%20click%20the%20play%20button%20above.%0A%0A%23%20Here%20we%20retrive%20a%20specific%20document%20(by%20uid)%20and%20extract%20%0A%23%20many%20different%20fields.%20Some%20of%20these%20are%20selectors%2C%20for%20%0A%23%20which%20we%20lookup%20previous%20knowledge%20and%20statistics.%0A%0A%23%20We%20also%20lookup%20references%20for%20the%20document%20and%20a%20list%20of%0A%23%20all%20user%20interaction%20with%20it.%0A%0A%23%20Not%20realy%20practical%20in%20this%20form%2C%20but%20mean%20to%20showcase%20%0A%23%20the%20wealth%20of%20information%20available%20in%20the%20tool.%0A%0Aquery%20LookupEmailFields(%24uid%3A%20String!%2C%20%24username%3A%20String!)%20%7B%0A%20%20Email(uid%3A%20%24uid%2C%20user%3A%20%24username)%20%7B%0A%20%20%20%20uid%0A%20%20%20%20type%0A%20%20%20%20headline%0A%20%20%20%20html%0A%20%20%20%20references%20%7B%0A%20%20%20%20%20%20originalId%0A%20%20%20%20%20%20source%0A%20%20%20%20%20%20classifications%0A%20%20%20%20%7D%0A%20%20%20%20timeOfEvent%0A%20%20%20%20timeOfInterception%0A%20%20%20%20formattedTimeOfEvent%0A%20%20%20%20formattedTimeOfInterception%0A%20%20%20%20usageActivities%20%7B%0A%20%20%20%20%20%20user%0A%20%20%20%20%20%20activityType%0A%20%20%20%20%20%20formattedTimeOfActivity%0A%20%20%20%20%7D%0A%20%20%20%20subject%0A%20%20%20%20message%0A%20%20%20%20from%20%7B%0A%20%20%20%20%20%20uid%0A%20%20%20%20%20%20type%0A%20%20%20%20%20%20headline%0A%20%20%20%20%20%20displayedName%20%7B%0A%20%20%20%20%20%20%20%20uid%0A%20%20%20%20%20%20%20%20type%0A%20%20%20%20%20%20%20%20headline%0A%20%20%20%20%20%20%20%20html%0A%20%20%20%20%20%20%20%20simpleRep%0A%20%20%20%20%20%20%20%20name%0A%20%20%20%20%20%20%20%20knowledge%20%7B%0A%20%20%20%20%20%20%20%20%20%20alias%0A%20%20%20%20%20%20%20%20%20%20isKnown%0A%20%20%20%20%20%20%20%20%20%20isRestricted%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20statistics%20%7B%0A%20%20%20%20%20%20%20%20%20%20dayTotal%0A%20%20%20%20%20%20%20%20%20%20weekTotal%0A%20%20%20%20%20%20%20%20%20%20quarterTotal%0A%20%20%20%20%20%20%20%20%20%20infinityTotal%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20address%20%7B%0A%20%20%20%20%20%20%20%20uid%0A%20%20%20%20%20%20%20%20type%0A%20%20%20%20%20%20%20%20headline%0A%20%20%20%20%20%20%20%20html%0A%20%20%20%20%20%20%20%20simpleRep%0A%20%20%20%20%20%20%20%20knowledge%20%7B%0A%20%20%20%20%20%20%20%20%20%20alias%0A%20%20%20%20%20%20%20%20%20%20isKnown%0A%20%20%20%20%20%20%20%20%20%20isRestricted%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20statistics%20%7B%0A%20%20%20%20%20%20%20%20%20%20dayTotal%0A%20%20%20%20%20%20%20%20%20%20weekTotal%0A%20%20%20%20%20%20%20%20%20%20quarterTotal%0A%20%20%20%20%20%20%20%20%20%20infinityTotal%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%7D%0A%20%20%20%20to%20%7B%0A%20%20%20%20%20%20uid%0A%20%20%20%20%20%20type%0A%20%20%20%20%20%20headline%0A%20%20%20%20%20%20html%0A%20%20%20%20%20%20displayedName%20%7B%0A%20%20%20%20%20%20%20%20uid%0A%20%20%20%20%20%20%20%20type%0A%20%20%20%20%20%20%20%20headline%0A%20%20%20%20%20%20%20%20html%0A%20%20%20%20%20%20%20%20simpleRep%0A%20%20%20%20%20%20%20%20name%0A%20%20%20%20%20%20%20%20knowledge%20%7B%0A%20%20%20%20%20%20%20%20%20%20alias%0A%20%20%20%20%20%20%20%20%20%20isKnown%0A%20%20%20%20%20%20%20%20%20%20isRestricted%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20statistics%20%7B%0A%20%20%20%20%20%20%20%20%20%20dayTotal%0A%20%20%20%20%20%20%20%20%20%20weekTotal%0A%20%20%20%20%20%20%20%20%20%20quarterTotal%0A%20%20%20%20%20%20%20%20%20%20infinityTotal%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20address%20%7B%0A%20%20%20%20%20%20%20%20uid%0A%20%20%20%20%20%20%20%20type%0A%20%20%20%20%20%20%20%20headline%0A%20%20%20%20%20%20%20%20html%0A%20%20%20%20%20%20%20%20simpleRep%0A%20%20%20%20%20%20%20%20knowledge%20%7B%0A%20%20%20%20%20%20%20%20%20%20alias%0A%20%20%20%20%20%20%20%20%20%20isKnown%0A%20%20%20%20%20%20%20%20%20%20isRestricted%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20statistics%20%7B%0A%20%20%20%20%20%20%20%20%20%20dayTotal%0A%20%20%20%20%20%20%20%20%20%20weekTotal%0A%20%20%20%20%20%20%20%20%20%20quarterTotal%0A%20%20%20%20%20%20%20%20%20%20infinityTotal%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%7D%0A%20%20%20%20cc%20%7B%0A%20%20%20%20%20%20uid%0A%20%20%20%20%20%20type%0A%20%20%20%20%20%20headline%0A%20%20%20%20%20%20html%0A%20%20%20%20%20%20displayedName%20%7B%0A%20%20%20%20%20%20%20%20uid%0A%20%20%20%20%20%20%20%20type%0A%20%20%20%20%20%20%20%20headline%0A%20%20%20%20%20%20%20%20html%0A%20%20%20%20%20%20%20%20simpleRep%0A%20%20%20%20%20%20%20%20name%0A%20%20%20%20%20%20%20%20knowledge%20%7B%0A%20%20%20%20%20%20%20%20%20%20alias%0A%20%20%20%20%20%20%20%20%20%20isKnown%0A%20%20%20%20%20%20%20%20%20%20isRestricted%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20statistics%20%7B%0A%20%20%20%20%20%20%20%20%20%20dayTotal%0A%20%20%20%20%20%20%20%20%20%20weekTotal%0A%20%20%20%20%20%20%20%20%20%20quarterTotal%0A%20%20%20%20%20%20%20%20%20%20infinityTotal%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20address%20%7B%0A%20%20%20%20%20%20%20%20uid%0A%20%20%20%20%20%20%20%20type%0A%20%20%20%20%20%20%20%20headline%0A%20%20%20%20%20%20%20%20html%0A%20%20%20%20%20%20%20%20simpleRep%0A%20%20%20%20%20%20%20%20knowledge%20%7B%0A%20%20%20%20%20%20%20%20%20%20alias%0A%20%20%20%20%20%20%20%20%20%20isKnown%0A%20%20%20%20%20%20%20%20%20%20isRestricted%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20statistics%20%7B%0A%20%20%20%20%20%20%20%20%20%20dayTotal%0A%20%20%20%20%20%20%20%20%20%20weekTotal%0A%20%20%20%20%20%20%20%20%20%20quarterTotal%0A%20%20%20%20%20%20%20%20%20%20infinityTotal%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%7D%0A%20%20%20%20bcc%20%7B%0A%20%20%20%20%20%20uid%0A%20%20%20%20%20%20type%0A%20%20%20%20%20%20headline%0A%20%20%20%20%20%20html%0A%20%20%20%20%20%20displayedName%20%7B%0A%20%20%20%20%20%20%20%20uid%0A%20%20%20%20%20%20%20%20type%0A%20%20%20%20%20%20%20%20headline%0A%20%20%20%20%20%20%20%20html%0A%20%20%20%20%20%20%20%20simpleRep%0A%20%20%20%20%20%20%20%20name%0A%20%20%20%20%20%20%20%20knowledge%20%7B%0A%20%20%20%20%20%20%20%20%20%20alias%0A%20%20%20%20%20%20%20%20%20%20isKnown%0A%20%20%20%20%20%20%20%20%20%20isRestricted%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20statistics%20%7B%0A%20%20%20%20%20%20%20%20%20%20dayTotal%0A%20%20%20%20%20%20%20%20%20%20weekTotal%0A%20%20%20%20%20%20%20%20%20%20quarterTotal%0A%20%20%20%20%20%20%20%20%20%20infinityTotal%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20address%20%7B%0A%20%20%20%20%20%20%20%20uid%0A%20%20%20%20%20%20%20%20type%0A%20%20%20%20%20%20%20%20headline%0A%20%20%20%20%20%20%20%20html%0A%20%20%20%20%20%20%20%20simpleRep%0A%20%20%20%20%20%20%20%20knowledge%20%7B%0A%20%20%20%20%20%20%20%20%20%20alias%0A%20%20%20%20%20%20%20%20%20%20isKnown%0A%20%20%20%20%20%20%20%20%20%20isRestricted%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%20%20statistics%20%7B%0A%20%20%20%20%20%20%20%20%20%20dayTotal%0A%20%20%20%20%20%20%20%20%20%20weekTotal%0A%20%20%20%20%20%20%20%20%20%20quarterTotal%0A%20%20%20%20%20%20%20%20%20%20infinityTotal%0A%20%20%20%20%20%20%20%20%7D%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%7D%0A%09%09rawData%0A%20%20%7D%0A%7D&operationName=LookupEmailFields&variables=%7B%0A%20%20%22uid%22%3A%20%22F69A78744432B921D64AAF54C3EE27E5%22%2C%0A%20%20%22username%22%3A%20%22me%22%0A%7D)|Example of how documents and document fields can be looked up.|
|[Mutation example](https://viktorkob.tk/NexusService/graphiql?query=%23%20This%20example%20show%20how%20to%20use%20a%20mutation%20in%20the%20service.%20%0A%23%20The%20only%20one%20that%20is%20currently%20possible%20is%20updating%20the%20%0A%23%20user%20activities%20for%20a%20specific%20document.%0A%0A%23%20When%20executed%2C%20it%20will%20add%20another%20row%20to%20the%20usage%20%0A%23%20database%2C%20with%20the%20event%20specified.%20This%20can%20then%20%0A%23%20immidiatly%20be%20looked%20up%20using%20a%20query%20on%20the%20same%20%0A%23%20document.%0A%0Amutation%20ExampleMutation(%24uid%3A%20String!%2C%20%24activityType%3A%20String!)%20%7B%0A%20%20usageActivity%20%7B%0A%20%20%20%20Email(uid%3A%20%24uid)%20%7B%0A%20%20%20%20%20%20add(user%3A%20%22Me%22%2C%20activityType%3A%20%24activityType)%20%7B%0A%20%20%20%20%20%20%20%20user%0A%20%20%20%20%20%20%20%20activityType%0A%20%20%20%20%20%20%20%20formattedTimeOfActivity%0A%20%20%20%20%20%20%7D%0A%20%20%20%20%7D%0A%20%20%7D%0A%7D%0A&variables=%7B%0A%20%20%22uid%22%3A%20%22F69A78744432B921D64AAF54C3EE27E5%22%2C%0A%20%20%22activityType%22%3A%20%22EXPORTED_DOCUMENT%22%0A%7D&operationName=ExampleMutation)|Example of a mutation storing user activity into the database.|

Feel free to experiment, but also note that the resources are very limited (AWS t2.medium service with 2 cores and 4 GiB of ram, that is running 8 services and a MySQL server simultaneously). If it is very slow, or starts hanging and/or failing, either scale back your queries, or come back at a later time and try again.  

To familiarize yourself with GraphiQL, I recommend going to [their introduction of queries](https://graphql.github.io/learn/queries/). The model can be browsed directly from the tool (in the right pane), and it will try to auto-complete queries when typing. 

### For a local setup

- To experiment locally, check out the entire repository.
- I recommend hooking it up to an IDE (Eclipse project files are included, and there are three pom.xml's you can import as well).
- Install a mysql server (I use 5.5, but any newer should work), and use [the schema](https://github.com/ViktorKob/portfolio/blob/master/services/usage-data-service/src/main/resources/schema/usage_data_schema.sql) to set it up in a database named "usage_data".
- Build everything (first tools, then infrastructure, then services).
- Start the infrastructure service from the infrastructure folder (net.thomas.portfolio.infrastructure.InfrastructureMasterApplication). 
- Run each service using its respective net.thomas.portfolio.*.*ServiceApplication.java. Order should not matter, if you start all of them shortly after each other. O.w. make sure to start the HbaseIndexingService first.

Now you can do as described above, but locally. <BR>
Note, that unless you also set up a local reverse proxy, you will need to specify ports directly when running queries (as opposed to the examples above). For instance, the hbase service should be running at (localhost:8120/HbaseIndexingService/).<BR>
Also note, that graphiql requires graphql and itself to be running at the root level (localhost:8100/graphql).

# Development strategy and major design principles used

This project was created as a green field project, but also contains code fragments from several older projects. Every component added has been (re-)written using the following.

### Prioritization

In general, my focus is on reducing wasted development time and getting features to market as soon as possible, both for the added value and to gain feedback early. I try to observe the following:

- Never get sucked down by irrelevant details, time spend working on one component is time not spend working on everything else
- Make it work at all, before trying to make it nice
- Early feedback is key for quality; rather than maturing a feature extensively, throw it out of the nest and check whether it can fly
- Write tests for units that worry you right away, do the rest when the units have matured reasonably
- Consider (and preferably fix) bugs as soon as possible
- Whenever possible, write tests for public bugs before fixing them to guarantee reoccurrences will be caught and fixed
- If someone else already made it for you, consider if using their solution is better than building your own
- Adding features is gold, but remember to also go back and cleanup the code; try to always keep up with changes in the immidiate code base

### Development approach

I use a modern IDE (preferably eclipse) and a proper build pipeline (IDE -> VCS -> build server -> artifact store -> deployment server -> execution environment), when possible.<BR>
Also, I value using static code analyzers like [FindBugs](http://findbugs.sourceforge.net/), pylint (for Python) and the IDE itself, and coverage tools like [Emma](http://emma.sourceforge.net/). Finally I use debugging for simple problems and profiling (e.g. [VisualVm](https://visualvm.github.io/)) to track down and fix the harder issues.<BR>
For bug-tracking and issue management, I have experience using [JIRA](https://www.atlassian.com/software/jira) and some [companion](https://www.atlassian.com/software/bitbucket) [Atlassian](https://www.atlassian.com/software/fisheye) [products](https://www.atlassian.com/software/confluence). 

But ultimately these are just tools. When I develop a new feature, a plan I often use is the following:
 
1. Plan layout based on domain knowledge, feature requirements and existing infrastructure
2. Define points of contact with the existing system and planned (near future) sister components
3. Build prototype component super-structure, faking the details (as little as is required to emulate the actual component)
4. Deploy the system and make sure the fake component behaves as intended; change whatever doesn't
5. Either check for thirds party tools that match / can be used for parts of the implementation, or reason why it should be implemented directly
6. Either replace the fake with the integration of a third party tool or implement the details
7. Deploy component with the rest of the system and check everything works
8. Cleanup obvious omissions and do general refactoring of the system
9. Over time, visit the component once in a while and check if anything should be refactored based on other feature implementations  

If I am "just" adding features to an existing component, many of the steps are pretty light-weight or perhaps even skipped, but it is still the primary approach I use.

### Design principles

When I write code, there is a set of principles that I try to respect more than others. "Try", because it is a process, not a goal, but still I value these highly. If you already know "Clean Code" by Robert C. Martin (Uncle Bob), much of this will seem self-evident. I do not agree, however, with the principle that you should always prioritize code quality over development speed, rather I believe both to be equally important. I do not expect you do agree with these, but I stand by them, and update them as I evolve. 

- Only start feature implementation that can be completed in at most a few days; instead make the sub-features production ready, before working on the complex features that depend on these
- Write the code to be read, using comments to elaborate invisible details, not to explain the code itself
- Use meaningful names; use the domain, spend time choosing them, don't use personal acronyms, and refactor when encountering strange or misleading names
- Keep it small; start large, but work towards short functions, short classes and split into meaningful sub-classes when possible
- Consider every single warning (both during development and when using static analysis) in the code and decide how to handle it; leave nothing for the build server
- Code coverage is a tool, not a goal; 100% in itself is irrelevant
- Perfection is a dream, continuous improvement is real; use the [Boy Scout Rule](https://medium.com/@biratkirat/step-8-the-boy-scout-rule-robert-c-martin-uncle-bob-9ac839778385), but keep moving
- Make it work at all before worrying about details and niceness
- Stay agile and "light-weight" for as long as possible; optimize for change initially, and add documentation and tests when the right level of maturity has been reached
- [SOLID](https://en.wikipedia.org/wiki/SOLID)
- [KISS](https://en.wikipedia.org/wiki/KISS_principle)
- When checking in, try to make it clear what is changed, e.g. by using a ticket id and a few lines about the specific change
- Commit often, merge often, deploy often, get feedback often; argue why not rather than why 

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
|**Endpoints**|[Swagger](https://viktorkob.tk/HbaseIndexingService/swagger-ui.html)|

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
|**Endpoints**|[Swagger](https://viktorkob.tk/RenderService/swagger-ui.html)|

One could argue that this service contains functionality that should be a part of the HBASE index service, but I have chosen to separate them, because I expect that the teams maintaining either service will be very different (HBASE specialists vs. front-end specialists). Still, it is heavily model dependent, and will likely need to be updated any time the HBASE index service is updated.

_Note that HTML rendering has not been implemented yet_. 

### Usage data
_Service responsible for storing and showing user interaction with the model_

[Source](https://github.com/ViktorKob/portfolio/tree/master/services/usage-data-service "Usage data root")

| Settings | |
|---|---|
|**Port**|8200|
|**Technologies**|MySQL, jOOQ, Spring|
|**User**|service-user|
|**Password**|password|
|**Endpoints**|[Swagger](https://viktorkob.tk/UsageDataService/swagger-ui.html)|

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
|**Endpoints**|[Swagger](https://viktorkob.tk/AnalyticsService/swagger-ui.html)|

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
|**Endpoints**|[Swagger](https://viktorkob.tk/LegalService/swagger-ui.html)|

The purpose here is to enclose all logic related to legal requirements into a separate service, to allow the developers working with the legal department to focus on a simple API instead of actual usage scenarios. 

It has two responsibilities:
- allow the system to check the legality of a query before actually executing it
- audit log the execution of a query into protected data on demand  

### Eureka
_Simple discovery service implementation with a hystrix UI_

[Source](https://github.com/ViktorKob/portfolio/tree/master/infrastructure "Infrastructure root")

| Settings | |
|---|---|
|**Port**|8000|
|**Technologies**|Eureka, Hystrix, Spring|
|**Endpoints**|<ul><li>/Infrastructure/</li></ul>|

This is my discovery service for the infrastructure. It doesn't really contain any code, just configuration. All standard Eureka endpoints are accessible through the sub-context-path /eureka. It also enables the Hystrix UI, if you have access to a Hystrix stream you wan't to monitor.

### Admin
_Service _

[Source](https://github.com/ViktorKob/portfolio/tree/master/infrastructure "Infrastructure root")

| Settings | |
|---|---|
|**Port**|8000|
|**Technologies**|Spring Boot Admin, Spring|
|**Endpoints**|<ul><li>/Admin/</li></ul>|

The Admin service / UI gives easy access to data from the actuator endpoints. It uses the discovery service both for service discovery and for looking up context paths.

### Zuul
_Reverse proxy for hiding ports, handling HTTPS and simplifying some endpoints_

[Source](https://github.com/ViktorKob/portfolio/tree/master/proxy "Proxy root")

| Settings | |
|---|---|
|**Port**|443, 80 (redirected)|
|**Technologies**|Spring, Zuul|
|**Endpoints**|<ul><li>__All of the above__</li></ul>|

Reverse proxy for the entire setup and single point of access to the services. Also responsible for diverting HTTP calls to HTTPS and is the only encrypted service in the infrastructure at the moment.

# Pending experimentation ideas
- [ ] Config server 
- [ ] Serialization format 
- [ ] Read and write sample data from disk during queries 
- [ ] Global model name constants
- [ ] Red-green deployment of changes 
- [ ] Add OpenAPI 3 integration to the services
- [ ] Add authentication layer, e.g. using UAA, JWS, OAuth2, JWT, JWA and OpenID
- [ ] Containerization of services (probably using Docker)
