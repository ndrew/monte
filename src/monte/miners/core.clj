(ns monte.miners.core)

(def miners (atom {}))

(def vars (atom []))


(def dummy-miner 
  {
  :cfg {
       :dummies []}

  :f (fn [cfg]
        (:dummies cfg )
        
      )})


(defn init-miners [] 
  (reset! miners 
          (merge @miners 
            {:dummy-miner dummy-miner})))

