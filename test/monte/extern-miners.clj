(print "loading extenal miner...")

(defminer ExternMiner []
  (f [this]     
     (let [cfg (.config this)]
       (:data cfg))))

(println "done")

