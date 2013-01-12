(ns monte.macro-test
  (:require [monte.miners.dummy :as test-ns])
  (:use clojure.test))

(defprotocol Foo (f[x] ""))

(defmacro from-miner-ns[& body] 
  `(binding 
     [*ns* (find-ns 'monte.miners.dummy)] 
       (eval
          (quote (do ~@body))
            )))


(deftest from-ns
    (is (empty? (ns-publics 'monte.miners.dummy)))
    
    (from-miner-ns (def test-from-miner-ns :test))  
    (is (not (empty? (ns-publics 'monte.miners.dummy))))

    (println (ns-publics 'monte.miners.dummy))
    
    (is (= :test @(ns-resolve (find-ns 'monte.miners.dummy) 'test-from-miner-ns)))
)


(defmacro defminer[a & body] 
  `(from-miner-ns 
    	(deftype ~a [])   
     	(extend-type ~a Foo ~@body)   
))
 
(println (macroexpand-1 '(defminer kurva (f[x] "ffff"))))
;(defminer kurva (f[x] "ffff"))

;(deftest ttt
;    (println (f (kurva.)))
;    (is (= 1 0))
;  )