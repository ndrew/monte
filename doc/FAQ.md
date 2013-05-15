#FAQ

Monte — is data processing and visualization web system for software developers. It can be launched locally or be used in separate environment as an addition to continious integration server.

##Data

Monte has a plugin system for retrieving/integrating data from different sources. These plugins are called miners(guess why? 8]). Basically, miner is a function that return data for visualization based on user-entered arguments. Arguments are described in the miner's meta-data. There also may be defined format of miners expected result. The miner meta-date is called *miner schema*. 

###Miners
For convinience there several possible miner types: that fetch data from filesystem/web/... 
<sup>*todo!*</sup>. Another possible source of data is monte itself, as it can collect and store events via json/edn API. <sup>Not here yet</sup>

Miners are executed in parallel, so they produce a lazy hash-map<sup>todo: now not very lazy now</sup> that will be used as a source for visualization. The data retrieved can be stored locally for quick access or versioned for comparison with other data from miners. 

###Data transformation: filtering/reducing/diff/etc.

As it is hard to return the only data needed for visualization or return it in proper format, monte can do some data preparation on the resulting map, like transform/enrich/modify the data.
As each miner provide a schema, user can specify transformations without actual data retrieval or try them on small subset of existing data.


##Data querying and visualization

As data from miners are gathered the visualization can be build based upon them. User can create separate views with generic visualizations based on data. Visualizations can be build incrementally via long polling.

#Rationale
… later

#Differences from other visualization tools
… later
##Incanter (or R)
… tbd 
##Excel
… tbd
##Graphviz
… tbd
