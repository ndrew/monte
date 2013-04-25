(ns monte.miners.data
"Monte data handling routines"
  (:require [clojure.data.json :as json]
            [clojure.core.reducers :as r]
            [clojure.set :as s]))

; thanx to http://www.thebusby.com/2012/07/tips-tricks-with-clojure-reducers.html

(defn fold-into-vec [coll]
  "Provided a reducer, concatenate into a vector.                                                                                                                                                                                                                                         
   Note: same as (into [] coll), but parallel."
  (r/fold (r/monoid into vector) conj coll))


(defn p-smart-merge[data config]
  "Uses core.reducers/fold to return a hash-map produced from data(list of hash-maps) that were merged 
  with function(s) specified in config as {:key-fn (fn[resulting-map current-item])}"
  (let [f (fn [memo item] ; merge function for reducers/fold
              (reduce #(let [ [k v] %2
                              merge-f-id (keyword (str (name k) "-fn"))]
                          (if-let [merge-f (get %1 merge-f-id)]
                            (merge-f %1 item) %1)) 
              memo item))
        result (r/fold 
                  (r/monoid f (constantly config)) f data)]
    
    (apply dissoc result (filter #(.endsWith (name %) "-fn") (keys config)))))
    
    
(defn p-smart-merge-v [data config]
  "smart merge via vector config"
  (let [f (fn [memo item] ; merge function for reducers/fold
              (merge memo (reduce (fn [i f1] (f1 memo i)) 
                                  item config)))]
      (r/fold 
            (r/monoid f hash-map) f data)))
    
  
; awaiting better times
; (defn p-sequential-smart-merge [data config] ; as vector )
    

(defn merger-by-key
  "merge function for monoid used to produce the following 
    [{key-id value, ...}, {key-id value1, ...}] => {key-id [{...}, {...}]}, where {...} is result of (f value)
    if f is not provided — default impl is provided that returns a hash-map without key-id"
  
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
          (assoc memo k [filtered-item]))))))


(defn aggregator-by-key[k monoid]
  "[{key-id value, ...}, {key-id value1, ...}] => {key-id (f value, value1 ...)}"
  (fn [memo item] ; find a way to autogenerate it
     (let [item-v (get item k)]
       (if-let [memo-v (get memo k)]
          (assoc memo k (monoid memo-v item-v))
          (assoc memo k (monoid (monoid) item-v))))))


(defn filter-by-f[filter-f] 
  "parallel filter func"
    (r/filter filter-f))


; other util stuff



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