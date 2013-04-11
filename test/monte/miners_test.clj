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

(comment 
(dbg 
  (macroexpand-1 '(defminer-map DummyMiner      
  {:f (fn [this]     
        (let [cfg (.config this)] ; use cfg later
          :dummy))
  
  :get-schema (fn [this] 
    {:schema :yep})
  }))) 
(dbg " ")



(defminer-map DummyMiner      
  {:f (fn [this]     
        (let [cfg (.config this)] ; use cfg later
          :dummy))
  
  :get-schema (fn [this] 
    {:schema :yep})
  })


(defminer DummyMiner1
  (f [this] (let [cfg (.config this)] ; use cfg later
                  monte.dummies/tasks))
  (get-schema [this] {}))
  

  
(def m (monte.miners.impl.DummyMiner. {:dummy :config}))      (dbg m)
(def m1 (monte.miners.impl.DummyMiner1. {:dummy :config}))    (dbg m1)

(dbg "m.f() = "(f m))
(dbg "m1.f()=" (f m1))
  
;#_(System/exit 0)
)

;(def dummy-miner (monte.miners.impl.DummyMiner. {:data :DUMMY}))
;
;(deftest dummy-miner-tests
;    (let [result (f dummy-miner)]
;      (is (= result :DUMMY))))


(deftest external-miners
  (load-extern-miners "test/monte/extern-miners.clj")) 


(deftest miner-listings 
  (let [miner-fns (list-all-miners)]
    (is (not (empty? miner-fns)))
      (doall(map (fn[x] 
        (let [[k constructor] x]
           ;(println "creating " (.getName k))
           ;(println (str "result: " (pr-str (f (v {})))))
           (is (class? k))
           (is (var? constructor))
           
           )) miner-fns))))