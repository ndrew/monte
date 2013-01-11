(ns monte.miners.impl
  (:gen-class))


 
(defprotocol Miner
  "Miner abstraction"
  (f [this] "run miner")
  (get-schema[this] "return a scheme(empty configuration) for miner"))


(defmacro defminer [t & body]
  `(do
     (deftype ~t ~['config])
     (extend-type ~t Miner ~@body))
)

(defminer DummyMiner      
  (f [this]     
     (let [cfg (.config this)]
       (:data cfg)))
  
  (get-schema[this] 
    {:data :everything}))

(comment 
(defn list-types-implementing[protocol]
  ; todo: add namespace filtering?
  (distinct (keys (:impls protocol)))) 