(ns monte.miners.core
  (:use monte.miners.impl)
  (:gen-class))
 
(defprotocol Miner
  "Miner abstraction"
  (f [this] "run miner")
  (get-schema[this] "return a scheme(empty configuration) for miner"))

(defmacro from-ns[nmsps & body] 
  `(binding 
     [*ns* (find-ns ~nmsps)] 
       (eval
          (quote (do ~@body))
            )))

(defmacro defminer[a & body] 
  `(from-ns 'monte.miners.impl
      (deftype ~a [~'config])   
      (extend-type ~a Miner ~@body)   
))


(defminer DummyMiner      
  (f [this]     
     (let [cfg (.config this)]
       (:data cfg)))
  
  (get-schema[this] 
    {:data :everything}))

; macro as I thought it would find test namespace

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


(defn load-extern-miners[path]
  (binding [*ns* (find-ns 'monte.miners.core)] (load-file path))
) 

