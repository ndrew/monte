(ns monte.miners-test
  (:use clojure.test
     	monte.miners.impl))
 

 (comment 
   (deftype TestForMinerCreation [config])
 
 (extend-type TestForMinerCreation monte.miners.impl/Miner 
   (f[x] 
    :f) 
  (get-schema[x] 
    :schema))
 )
   
(defminer TestForMinerCreation      
  (f[x] 
    :f) 
  (get-schema[x] 
    :schema))



(deftest miner-defining
 	(is (not (nil? TestForMinerCreation)))
 	(let [miner (TestForMinerCreation. {})
          result (f miner)
          schema (get-schema miner)]

        (is (= result :f))
	    	(is (= schema :schema))))


(def dummy-miner (monte.miners.impl.DummyMiner. {:data :DUMMY}))

;(println dummy-miner)

(deftest dummy-miner-tests
    (let [result (f dummy-miner)]
      (is (= result :DUMMY))))
 

(println monte.miners.impl.@miners)


(doseq [[k v] (:impls monte.miners.impl/Miner)] 
  (println k)  
  (println (pr-str v))
  
  (println (find-protocol-impl monte.miners.impl/Miner k))
  (println (find-protocol-impl monte.miners.impl/Miner v))
  
  )

     
    
    

 
