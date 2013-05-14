       __  _______  _  ____________
      /  |/  / __ \/ |/ /_  __/ __/
     / /|_/ / /_/ /    / / / / _/  
    /_/  /_/\____/_/|_/ /_/ /___/ 

A software visualization tool written in clojure for my master's degree thesis

## Usage

Clone the repo and do
    lein run 
if you want to have jar do
    lein uberjar
    java -jar target/monte-0.0.0-SNAPSHOT-standalone.jar 

## Command line switches

    Switches                         Default  Desc              
    --------                         -------  ----              
    -p, --port                       8899     Port to listen on 
    -a, --no-auto-open, --auto-open  false                      
    -c, --no-color, --color          false                      
    -d, --no-debug, --debug          false                      
    -h, --no-help, --help            false    Show help  
 
## Status

[![Build Status](https://travis-ci.org/ndrew/monte.png)](https://travis-ci.org/ndrew/monte)

## License

Copyright © 2012-2013, Andrew Sernyak

Distributed under the Eclipse Public License, the same as Clojure.
