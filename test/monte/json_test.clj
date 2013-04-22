(ns monte.json-test
  (:use clojure.test)
  (:require [clojure.data.json :as json]
            [clojure.core.reducers :as r]
            [clojure.set :as s]))


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



(defn p-smart-merge[data config]
  "Uses core.reducers/fold to return a hash-map produced from data(list of hash-maps) that were merged 
  with function(s) specified in config as {:key-fn (fn[resulting-map current-item])}"
  (let [f (fn [memo item] ; merge function for reducers/fold
              ;(println "fn " memo item)
              (reduce #(let [ [k v] %2
                              merge-f-id (keyword (str (name k) "-fn"))]
                          (if-let [merge-f (get %1 merge-f-id)]
                            (merge-f %1 item) %1)) 
              memo item))
        result (r/fold 
                  (r/monoid f (constantly config)) f data)]
    
    (apply dissoc result (filter #(.endsWith (name %) "-fn") (keys config)))))
    
  
; awaiting better times
; (defn p-sequential-smart-merge [data config] ; as vector )
    

(defn merger-by-key
  "[{key-id value, ...}, {key-id value1, ...}] => {key-id [{...}, {...}]}"
  
([key-id]
  (fn [memo item] ; find a way to autogenerate it 
     (let [k (get item key-id)
           filtered-item (dissoc item key-id)]
      (if-let [v (get memo k)]
          (assoc memo k (conj v filtered-item))
          (assoc memo k [filtered-item])))))
([key-id f];ilter
 (fn [memo item] ; find a way to autogenerate it 
     (let [k (get item key-id)
           filtered-item (f item)]
      (if-let [v (get memo k)]
          (assoc memo k (conj v filtered-item))
          (assoc memo k [filtered-item])))))
)




(defn aggregator-by-key[k monoid]
  "[{key-id value, ...}, {key-id value1, ...}] => {key-id (f value, value1 ...)}"
  (fn [memo item] ; find a way to autogenerate it
     (let [item-v (get item k)]
       (if-let [memo-v (get memo k)]
          (assoc memo k (monoid memo-v item-v))
          (assoc memo k (monoid (monoid) item-v))))))


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


; thanx to http://www.thebusby.com/2012/07/tips-tricks-with-clojure-reducers.html

(defn fold-into-vec [coll]
  "Provided a reducer, concatenate into a vector.                                                                                                                                                                                                                                         
   Note: same as (into [] coll), but parallel."
  (r/fold (r/monoid into vector) conj coll))


(defn fold-into-sorted-map-desc [coll]
  "Provided a reducer, concatenate into a vector.                              
   Note: same as (into [] coll), but parallel."
   
   ; todo: for map — (vec coll)
   ; for other colls?
  (r/fold (r/monoid conj #(sorted-map-by 
                            (fn [key1 key2] (compare key2 key1)))) (vec coll)))
        
        
; todo!
;(defn fold-into-vec-format [coll f]
;  "Provided a reducer, concatenate into a vector.                                                                                                                                                                                                                                         
;   Note: same as (into [] coll), but parallel."
;  (r/fold (r/monoid #(into (f %1) %2) vector) conj coll))


; todo: add modifications of this

(defn rindex-by-fn
  "Uses core.reducers/fold to return a map indexed by the value of key-fn                                                                                                                                                                                                                 
   applied to each element in coll.                                                                                                                                                                                                                                                       
   Note: key-fn can return a collection of multiple keys."
  ([key-fn coll ] (rindex-by-fn key-fn identity coll))
  ([key-fn value-fn coll] (doall (clojure.core.reducers/fold
                                  ;; combinef                                                                                                                                                                                                                                             
                            (clojure.core.reducers/monoid (partial merge-with into)
                                          hash-map)
                                  ;; reducef                                                                                                                                                                                                                                              
                                  (fn [ndx elem] (let [key (key-fn elem)]
                                                   (if (coll? key)
                                                     (reduce (fn [sndx selem] (assoc sndx selem (conj (get sndx selem [])
                                                                                                      (value-fn elem))))
                                                             ndx key)
                                                     (assoc ndx key (conj (get ndx key [])
                                                                    (value-fn elem))))))
                            coll))))

(def log-file "/Users/ndrw/monte/sqls.log")

(with-open [rdr (time (clojure.java.io/reader log-file))]
    (let [raw-data (time (json/read rdr))]
      (let [sqls (time (get raw-data "sqls"))
            ;reducer (comp (r/filter #(> 1 (- (get % "end") (get % "start"))) )  
            ;              (r/map inc))
            slow-seconds 0.5
            
            
            ; good version
            slow-sqls (r/filter #(> (- (get % "end") (get % "start")) slow-seconds ) sqls)
            slow-vec (fold-into-vec slow-sqls)
            
            folded-by-sql (p-smart-merge 
                            slow-vec 
                            {:sql-fn (merger-by-key "sql"
                                                    (fn [item]
                                                       (assoc (dissoc item "sql" "result")
                                                              :time (- (get item "end") (get item "start")))))})

            
            
                        
            ;slow-map (time (rindex-by-fn #(get % "sql") slow-sqls))
            ;report (time (fold-into-sorted-map-desc 
            ;         (map identity slow-map) ))
            
            ]
              
              
              (doall (clojure.pprint/pprint folded-by-sql))
              ;(println "==============================")
              ;(doall (println (pr-str slow-sqls)))
              ;(doall (println folded-by-sql))
              
                     
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
      )))
