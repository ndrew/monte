(ns monte.miners.core
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

;(println monte.miners_test/->TestForMinerCreation)





(defmacro list-types-implementing[protocol]
  `(remove nil? (map #(let [[k# v#] %
               [_# miner-ns# miner-fn#] (re-find #"(.*)\.(.*)$" (.getName k#))]
            (cond (not (nil? (find-ns (symbol miner-ns#))))
              [k# (ns-resolve (find-ns (symbol miner-ns#)) (symbol miner-fn#))]
              :else 
                (do 
                  (println (str "Can't load " miner-ns# "/->" miner-fn#))
                  nil)
            )
           ) (:impls ~protocol)))
  )