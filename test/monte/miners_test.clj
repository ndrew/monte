(ns monte.miners-test
  (:use clojure.test
        monte.logger
       	monte.miners.core))

(defminer TestForMinerCreation []   
  (f[x] :f))


(deftest miner-defining
 	(is (not (nil? monte.miners.impl.TestForMinerCreation)))
 	(let [miner (monte.miners.impl.TestForMinerCreation. [:config] {:miner :schema})
        result (f miner)
        schema (m-meta miner)]
    
        (is (= [:config] (.config miner)))
	    	(is (= [] schema))
        (is (= {:miner :schema} (.meta miner)))
        (is (= :f result))))



(defminer-map DummyMiner {}
  {:f (fn [this]     
        (let [cfg (.config this)] ; use cfg later
          :dummy))})



(defminer DummyMiner1 {}
  (f [this] (let [cfg (.config this)] ; use cfg later
                  monte.dummies/tasks)))
  

(defminer DummyMiner2 [:testo :pesto]
  (f [this] :f2))
  
  
(deftest external-miners
  (load-extern-miners "test/monte/extern-miners.clj")) 


(deftest miner-listings 
  (let [miner-fns (list-all-miners)]
    (is (not (empty? miner-fns)))
      (doall(map (fn[x] 
        (let [[k constructor schema] x]
           ;(println "creating " (.getName k))
           ;(println (str "result: " (pr-str (f (v {})))))
           (is (class? k))
           (is (var? constructor))
           (is (or (map? schema) 
                 (vector? schema)))
           
           )) miner-fns))))