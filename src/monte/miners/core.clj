(ns monte.miners.core
  (:use monte.miners.impl)
  (:gen-class))


(defmacro from-ns[nmsps & body] 
  "launches body from namespace"
  `(binding 
     [*ns* (find-ns ~nmsps)] 
       (eval
          (quote (do ~@body)))))

 
(defprotocol Miner
  "Miner abstraction"
  (f [this] "run miner")
  (get-schema[this] "return a scheme(empty configuration) for miner"))


(defmacro defminer[miner-name & body]
  "defines a miner"
  `(from-ns 'monte.miners.impl
      (deftype ~miner-name [~'config])   
      (extend-type ~miner-name Miner ~@body)))


(defmacro list-types-implementing[protocol] ; macro as I thought it would find test namespace
  "list all types that implement specified protocol in miner-ns" ; todo: refactor
  `(remove nil? 
     (map #(let [[k# v#] %
         [_# miner-ns# miner-fn#] (re-find #"(.*)\.(.*)$" (.getName k#))]
      (cond (not (nil? (find-ns (symbol miner-ns#))))
        [k# (ns-resolve (find-ns (symbol miner-ns#)) (symbol miner-fn#))]
        :else 
          (do 
            (println (str "Can't load " miner-ns# "/->" miner-fn#))
            nil)
      )
     ) (:impls ~protocol))))


(defn load-extern-miners[path]
  "loads miners from file path"
  (binding [*ns* (find-ns 'monte.miners.core)] (load-file path))) 

;;;;;;;;;;;;
; miner impls

(defminer DummyMiner      
  (f [this]     
     (let [cfg (.config this)]
       (:data cfg)))
  
  (get-schema[this] 
    {:data :everything}))

;(def)

