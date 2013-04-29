(ns monte.json-test
  (:use clojure.test)
  (:require [clojure.data.json :as json]
            [clojure.core.reducers :as r]
            [clojure.set :as s]
            [cheshire.core :refer :all]
            [monte.miners.data :refer :all]))


(defn count-data[data]
  (reduce 
    (fn [memo item]
      (assoc memo item (inc (get memo item 0))))
    {}
    ; or map data - for preparation
    data))

(defn p-count-data[data]
  (r/fold 
    (r/monoid (partial merge-with +) hash-map)
    (fn [memo item]
      (assoc memo item (inc (get memo item 0))))
    ; or map data - for preparation
    data))


(defn p-smart-count[data initial-map]
  "Uses core.reducers/fold to return a map that was merged(nubers are added, seqs are conjed) to initial-map with results."
  (let [merge-f #(cond 
                   (number? %1) (+ %1 %2) 
                   :else        (conj %1 %2))]
    (r/fold 
      (r/monoid (partial merge-with merge-f) 
                (constantly initial-map))
    (fn [memo item] (merge-with merge-f memo item))
    data)))





#_(def d (time (p-smart-merge [{:time 100 :sql "sql1"}
                {:time 200 :sql "sql2" :crap "Fuuu" :test true}
                {:time 700 :sql "sql1"}
                {:time 1000 :sql "sql1" :crap "Bazz"}
                
                ]
                ; inital
                {; "metadata" here 
                 :sql-fn (merger-by-key :sql) })))



#_(def t (p-smart-merge [{:crap "Fuuu", :test true, :time 200}]
               {:crap-fn (aggregator-by-key :crap (r/monoid
                                                     conj
                                                     (constantly [])))}))
;(dorun t);(println t)


#_(def d1 (reduce #(let [[k v] %2
                       new-v (p-smart-merge v {:time-fn (aggregator-by-key :time +)
                                               :crap-fn (aggregator-by-key :crap (r/monoid 
                                                                                   conj
                                                                                   (constantly [])))
                                               })
                       ] 
                (assoc %1 k new-v)
                ) {} d))

;(dorun d1);(println d1)




#_(def log-file "/Users/ndrw/monte/new_sqls.log")



#_(with-open [rdr (clojure.java.io/reader log-file)]
  (println "loading json")
    (let [sqls (first (parsed-seq rdr)) ; (time (json/read rdr))
          
          slow-seconds 0.5
          ; good version
          ;slow-sqls (r/filter #(> (- (get % "end") (get % "start")) slow-seconds ) sqls)
          
          f-fn (filter-by-f 
                         #(> (- (get % "end") (get % "start")) slow-seconds))
          
          slow-sqls1 (fold-into-vec (f-fn sqls))
          
          
          ;slow-sqls1 (time ((fold-into-vec (filter-by-f #(> (- (get % "end") (get % "start")))) sqls)))
          
          folded-by-sql (p-smart-merge-v
                          slow-sqls1
                          [(merger-by-key "sql" (fn [item]
                                                   (assoc (dissoc item "sql" "result")
                                                          :time (- (get item "end") (get item "start")))))]
                          )
          
          ;folded-by-sql (p-smart-merge 
          ;                  slow-sqls1 
          ;                  {:sql-fn (merger-by-key "sql"
          ;                                          (fn [item]
          ;                                             (assoc (dissoc item "sql" "result")
          ;                                                    :time (- (get item "end") (get item "start")))))})
           
           
            ;slow-map (time (rindex-by-fn #(get % "sql") slow-sqls))
            ;report (time (fold-into-sorted-map-desc 
            ;         (map identity slow-map) ))
          ]
          
          
          ;(doall (time (println (take 10 slow-sqls1))))
          
          
          
          ;(time (doall (clojure.pprint/pprint folded-by-sql)))
          ;(println "==============================")
          ;(doall (println (pr-str slow-sqls)))
          ; (doall (println folded-by-sql))
          
          ;(generate-stream folded-by-sql (clojure.java.io/writer "result.json"))
          
          (println 
          (p-smart-merge-v
            
            (get folded-by-sql "select max(joid) joid from jvjobs where pc=:pc")
            [(aggregator-by-key :time +)]
            )
          )
          
                 
          #_(doall (map #(let [[sql details] %]
                         
                         (println sql)
                         (println (count details))
                         
                         ) report))
                 
                              
          (comment ; works ok
                       
          (doall (map #(let [[sql details] %]
                         
                         (println sql)
                         (println (count details))
                         
                         ) slow-map))
           
          )           
                
          (comment ; works ok
            (doall (map #(do
              (println 
                (str "took " 
                  (pr-str (- (get % "end") (get % "start")))
                   "seconds"))
           (println (get % "sql"))
           ) slow-vec)))

      
        ;(reduce conj [] (r/map inc [1 2 3]))
        ;(r/fold #(into [] (r/filter ))
        
        ;(into [] (r/filter #(> 1 (- (get "end" % ) (get "start" %))) )  
        ;(println (pr-str (- (get f "end") (get f "start"))))        
        
        ;)
      
      
      
;     (spit "sqls.edn" raw-data)
      ))
