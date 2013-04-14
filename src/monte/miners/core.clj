(ns monte.miners.core
  (:use monte.miners.impl
        [monte.logger :only [dbg err]])
  (:require [monte.dummies :as dummies]
            [clojure.reflect :as r])
  ;(:gen-class)
  )


(def miner-init-cfg {}) ; init data for miners TBD configured later

(def miners-impls (atom {})) ; memoize for miners


(defmacro from-ns[nmsps & body] 
  "launches body from namespace"
  `(binding 
     [*ns* (find-ns ~nmsps)] 
       (eval
          (quote (do ~@body)))))

 
(defprotocol Miner
  "Miner abstraction"
  (f [this] "run miner")
  ; (get-schema[this] "return a scheme(empty configuration) for miner")
  
  )

(defn get-m [s]
  "returns miner class symbol"
  (cond (symbol? s) (get-m (str s))
      
        (= java.lang.String (type s)) (symbol (if-not (.startsWith "monte.miners.impl" s)
                                      (str "monte.miners.impl." s) s))
        :else s
      ))


(defn m-to-keyword [m] 
  (keyword (.getName (get-m m))))


(defn m-meta [m]
  (get (meta #'Miner) 
       (m-to-keyword (if (= java.lang.Class (type m))
                            m (type m)))))


(defmacro defminer[m-name m-schema & body]
  "defines a miner"
  `(from-ns 'monte.miners.impl
      (deftype ~m-name [~'config ~'meta])   
      (alter-meta! #'Miner assoc (keyword (.getName ~m-name)) ~m-schema)
      (extend-type ~m-name Miner ~@body)
      ;(import ~(get-m m-name))
      )
   )

(defmacro defminer-map[m-name m-schema & body]
  "defines a miner"
    `(defminer ~m-name ~m-schema
       (~'f [~'x] ((~@(get (eval `'~@body) :f)) ~'x )))) 
  

  
(defmacro list-types-implementing[protocol] ; macro as I thought it would find test namespace
  "list all types that implement specified protocol in miner-ns" ; todo: refactor
  `(remove nil? (map #(let [[k# v#] %
                            [_# miner-ns# miner-fn#] (re-find #"(.*)\.(.*)$" (.getName k#))]
      (if-let [~'s (find-ns (symbol miner-ns#))]
        [k#  (ns-resolve ~'s (symbol (str "->" miner-fn#))) (m-meta k#) ]
        (do 
            (err (str "Can't load " miner-ns# "/->" miner-fn#))
            nil)
        )
     ) (:impls ~protocol))))


(defn list-all-miners[]
  "returns list[miner-class miner-constructor-func] of all loaded miners"
  (list-types-implementing Miner))


(defn get-miner-impl [type]
  (when-not (@miners-impls type) 
    (when-let [miner (first (filter #(= (first %) type) (list-all-miners)))]
      (swap! miners-impls merge { type ( (miner 1) miner-init-cfg (m-meta type) ) })))
  (@miners-impls type))

(defn load-extern-miners[path]
  "loads miners from file path"
  ;; WARNING: not secure. Use on your own risk
  (binding [*ns* (find-ns 'monte.miners.core)] (load-file path))) 

(defn list-miners [cb]
  "return all miners formatted by callback cb(miner-type, miner-impl)"
  (map #(let [[type _] %  impl (get-miner-impl type)]
                (cb type impl)) (list-all-miners)))



;;;;;;;;;;;;
; miner impls

  
(defminer JIRAMiner {}
  (f [this]     
     (let [cfg (.config this)] ; use cfg later
       monte.dummies/tasks))
  ) ; tbd

#_(defminer VCSMiner []
  (f [this]     
     (let [cfg (.config this)] ; use cfg later
       monte.dummies/commits))
  ) ; tbd


#_(defminer SRCMiner []
  (f [this]     
     (let [cfg (.config this)] ; use cfg later
       monte.dummies/src-analysis-data))
  
  ) ; tbd
