# Monte architecture

Monte is a web application for data analysis/visualization. It uses concept of miners for retrieving/producing data on backend for further visual analysis in the frontend(usually via visualizations)

## Front-end specific stuff

Front-end uses common ui elements such as list of items, table and so on. Each ui element is defined by a config(hash map actually) with all data and listeners/handler needed, so messing up with css classes and similar crap will be written only once. 

In future ui defining may be refined to a dsl with macro-magic.

Currently frontend is a plain clojurescript application(jayq, strokes). In future it may be rewritten with pedestal.

## Backend stuff

Backend is responsible for handling doing data crunching. Currently backend can't scale and can work only on one machine. 

### Concurency

Currently miners are ran asynchroniously as futures, but better solution need to be provided. 

 	; tbd

### Scalability
	
One machine for now.

  	; tbd

### Performance

	; another open issue