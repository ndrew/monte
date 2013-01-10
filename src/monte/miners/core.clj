(ns monte.miners.core)

;;;;
; note, that you can use miner implementation only within monte.miners.core
;

(defprotocol Miner
  "Miner abstraction"
  (f [this] "run miner")
  (get-schema[this] "return a scheme(empty configuration) for miner"))


(deftype DummyMiner [config]
  Miner
  (get-schema[this]
    {:name ""
     :data :everything})
  (f [this]
     ;(println miner)
     (:data config)))

;(f (DummyMiner. {:name "dummy miner"
;                :data [1 2 3]}))



(deftype GitMiner [config]
  Miner
  (get-schema[this]
    {:name ""
     :git-url :path})

  (f [this]
     ;(println config)
     nil))


(defn implements? [protocol atype]
  (and atype (.isAssignableFrom ^Class (:on-interface protocol) atype)))



(defn list-types-implementing[protocol]
  (println (str "list-types-implementing" (:on-interface protocol)))
  (filter (fn[x] (let [[a b] x]
            (when (.startsWith (str a) "->") ; dark magic        
              (implements? protocol 
               (resolve (symbol 
                (.replace (str "monte.miners.core." a) "->" "")))))
            )) 
         (ns-publics 'monte.miners.core)))


(defn list-miners[] 
  "return list of all miners registered in system"
  
  (println "LISTING MINERS:")
  (let [miners (list-types-implementing Miner)]
    (println
      (map #(hash-map
       (keyword(str(first %))) 
       (get-schema((first(rest %)) {})))
      miners)))
  []
)

