(ns monte.miners.impl
  (:gen-class)
  (:use monte.miners.core))

(deftype DummyMiner [config]
  Miner
  (get-schema[this]
    {:data :everything})
  
  (f [this]
     ;(:data config)
   	"Hello From DummyMiner"  
     ))



(deftype GitMiner [config]
  Miner
  (get-schema[this]
    {:git-url :path})
  
  (f [this]
     ;nil
     "Hello From GitMiner" 
     ))


; smth like init statement

;(println
;(f (DummyMiner. {:name "dummy miner"
;                :data [1 2 3]}))
;)
