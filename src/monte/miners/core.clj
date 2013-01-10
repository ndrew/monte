(ns monte.miners.core
  (:gen-class))

;;;;
; note, that you can use miner implementation only within monte.miners.core
;

(defprotocol Miner
  "Miner abstraction"
  (f [this] "run miner")
  (get-schema[this] "return a scheme(empty configuration) for miner"))


(defn implements? [protocol atype]
  (and atype (.isAssignableFrom ^Class (:on-interface protocol) atype)))



(defn list-types-implementing[protocol n]
  (println (str "list-types-implementing " (:on-interface protocol)))
  (filter (fn[x] (let [[a b] x]
            (comment(
             when (.startsWith (str a) "->") ; dark magic        
              (implements? protocol 
               (resolve (symbol 
                (.replace (str n "." a) "->" "")))))
            ))
            true
            ) 
         (ns-publics n)))


(defn list-miners[n] 
  "return list of all miners registered in system"
  
  (let [miners (list-types-implementing Miner n)]
      (map #(hash-map
       (keyword(str(first %)))
       (get-schema((first(rest %)) {}))
      )
      miners)))