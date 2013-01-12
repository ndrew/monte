(ns monte.macro-test
  (:require [monte.miners.dummy :as test-ns])
  (:use clojure.test))

(defprotocol Foo (f[x] ""))


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


(defmacro defminer[a & body] 
  `(from-ns 'monte.miners.dummy
    	(deftype ~a [])   
     	(extend-type ~a Foo ~@body)   
))
 
;(println (macroexpand-1 '(defminer kurva (f[x] "ffff"))))


(deftest ttt
  (defminer mnr (f[x] :hello))

    ;(println (ns-publics 'monte.miners.dummy))
    
    (let [miner (ns-resolve (find-ns 'monte.miners.dummy) '->mnr)]
      (is (= :hello (f (miner))))
    )
    
    
    ; todo: 
)