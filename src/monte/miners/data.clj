(ns monte.miners.data
"Monte data handling routines"
  (:require [clojure.data.json :as json]
            [clojure.core.reducers :as r]
            [clojure.set :as s]))


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