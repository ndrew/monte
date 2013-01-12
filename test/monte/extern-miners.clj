(println "ZZZZZZZ")

(defminer ExternMiner
  (f [this]     
     (let [cfg (.config this)]
       (:data cfg)))
  
  (get-schema[this] 
    {:data :everything}))

