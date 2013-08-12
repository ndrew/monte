(ns monte.miners.core
  (:use monte.miners.impl
        [monte.logger :only [dbg err]])
  (:require [monte.dummies :as dummies]
            [clojure.reflect :as r]))


(def ^:dynamic *miner-init-data* {}) ; init data for miners TBD configured later
; TODO: add watcher that will re-create miners if init has been changed

(def ^:dynamic *miners-impls* (atom {})) ; memoize for miners ; TODO: connect with session somehow


(defmacro from-ns[nmsps & body] 
  "launches body from namespace"
  `(binding 
     [*ns* (find-ns ~nmsps)] 
       (eval
          (quote (do ~@body)))))

 
(defprotocol Miner
  "Miner abstraction"
  (f [this] "fn that does the data mining job"))


(defn m-id [s]
  "helper fn that returns miner-id keyword that can will be used for miner instantiation and so on"
  (cond (or (keyword? s) (symbol? s))
          (m-id (name s))
        (= java.lang.String (type s))
          (keyword (if-not (.startsWith s "monte.miners.impl")
                     (str "monte.miners.impl." s) s))
        (and (= java.lang.Class (type s)) (extends? Miner s))
          (m-id (.getName s))
        :else 
          (m-id (type s))))


(defn m-meta [m]
  "returns meta information defined in miner"
  (get (meta #'Miner) (m-id m)))


(defmacro defminer[m-name m-schema & body]
  "defines a miner"
  `(from-ns 'monte.miners.impl
      (deftype ~m-name [~'data ~'meta])   
      (alter-meta! #'Miner assoc (keyword (.getName ~m-name)) ~m-schema)
      (extend-type ~m-name Miner ~@body)))


(defmacro defminer-map[m-name m-schema & body]
  "defines a miner via map"
    `(defminer ~m-name ~m-schema
       (~'f [~'x] ((~@(get (eval `'~@body) :f)) ~'x )))) 
  
  
(defmacro list-types-implementing[protocol] ; macro as I thought it would find test namespace
  "list all types that implement specified protocol in miner-ns" 
  `(remove nil? 
    (map #(let [[k# v#] %
                [_# miner-ns# miner-fn#] (re-find #"(.*)\.(.*)$" (.getName k#))]
            (if-let [~'s (find-ns (symbol miner-ns#))]
              [(m-id k#)  (ns-resolve ~'s (symbol (str "->" miner-fn#)))]
              (do (err (str "Can't load " miner-ns# "/->" miner-fn#)) nil))) 
         (:impls ~protocol))))


(defn m-list-ids[]
  "returns list of miner-ids' registred"
  (map first 
      (list-types-implementing Miner)))

(defn m-list-meta[]
  "returns list of miner-ids' registred + their metadata"
  (map #(let [id (first %)] [id (m-meta id)]) 
      (list-types-implementing Miner)))



(defn m-constr [id]
  "returns a constructor for miner"
  (when-let [m-data (first (filter #(= (first %) (m-id id)) (list-types-implementing Miner)))]
    (second m-data)))

; incorrect

(defn m-impl [type]
  (let [id (m-id type)]
    (when-not (@*miners-impls* id)
      (swap! *miners-impls* merge { id ((m-constr id) *miner-init-data* (m-meta id)) }))
    (@*miners-impls* id)))



(defn load-extern-miners[path]
  "loads miners from file path"
  ;; WARNING: not secure. Use on your own risk
  (binding [*ns* (find-ns 'monte.miners.core)] (load-file path))) 



;;;;;;;;;;;;
; miner impls




;(defminer ComposableMiner {:meta-data :here}
;  (f [this]
;     (let [cfg (.meta this)
;           data (.data this)]
;        
;       nil
;       )))


  
(defminer JIRAMiner {}
  (f [this]     
     (let [cfg (.data this)] ; use cfg later
       monte.dummies/tasks))
  ) ; tbd

#_(defminer VCSMiner []
  (f [this]     
     (let [cfg (.data this)] ; use cfg later
       monte.dummies/commits))
  ) ; tbd


#_(defminer SRCMiner []
  (f [this]     
     (let [cfg (.data this)] ; use cfg later
       monte.dummies/src-analysis-data))
  
  ) ; tbd
