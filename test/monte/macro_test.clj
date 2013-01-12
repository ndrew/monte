(ns monte.macro-test
  (:require [monte.miners.dummy :as test-ns
             monte.miners.core :miners])
  (:use clojure.test
        ))
;
 
 
(comment         
(defprotocol Foo (f[x] "")(g[x] ""))

(defmacro from-ns[nmsps & body] 
  `(binding 
     [*ns* (find-ns ~nmsps)] 
       (eval
          (quote (do ~@body))
            )))

(deftest from-ns-test
    
    (from-ns 'monte.miners.dummy (def test-from-ns :test))  
      ;(println (ns-publics 'monte.miners.dummy))
    
    (is (= :test @(ns-resolve (find-ns 'monte.miners.dummy) 'test-from-ns)))
)


(defmacro defminer1[a & body] 
  `(from-ns 'monte.miners.dummy
    	(deftype ~a [])   
     	(extend-type ~a Foo ~@body)   
))
 

(deftest ttt
  (defminer1 mnr (f[x] :hello)(g[x] :world))
      ;(println (ns-publics 'monte.miners.dummy))
    
  (let [miner ((ns-resolve (find-ns 'monte.miners.dummy) '->mnr))]
    (is (= :hello (f miner)))
    (is (= :world (g miner)))
     
  )

  ;(defminer1 mnr1 (f[x] :hello)(g[x] :world))

      ; todo: 
)

)