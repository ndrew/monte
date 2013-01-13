(ns monte.miners-test
  (:use clojure.test
       	monte.miners.core))

(defminer TestForMinerCreation      
  (f[x] 
    :f) 
  (get-schema[x] 
    :schema))

(deftest miner-defining
 	(is (not (nil? monte.miners.impl.TestForMinerCreation)))
 	(let [miner (monte.miners.impl.TestForMinerCreation. {})
          result (f miner)
          schema (get-schema miner)]

        (is (= result :f))
	    	(is (= schema :schema))))


(def dummy-miner (monte.miners.impl.DummyMiner. {:data :DUMMY}))

(deftest dummy-miner-tests
    (let [result (f dummy-miner)]
      (is (= result :DUMMY))))

(deftest external-miners
  (load-extern-miners "test/monte/extern-miners.clj")   
) 


(deftest miner-listings 

  (def miner-fns (list-types-implementing Miner))

  (println miner-fns)
  (is (not (empty? miner-fns)))

  ; todo: try creating ?
)    
      
