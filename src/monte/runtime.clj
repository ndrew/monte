(ns monte.runtime
  "Monte runtime engine — holds session and a list of workspace changes"
  (:use [clojure.data :only [diff]])
  )

; wrapper around merge
(defn merge-data [a b]
  (let [before (select-keys a (keys b))
        after  (select-keys b (keys a)) 
        ]
    (when-not (empty? before)
      (println (str before "(" a ") will be replaced with " after "( " b ")"))))
  (merge a b))



; list of changes
;   each change is stored as [timestamp & change info]
;
; <!> please note that any change does not remove keys from workspace map, 
; so you need to provide nil or empty list/vector in order to remove something  
(def changes (atom []))

(add-watch changes :watch-change 
  (fn [key _changes old-val new-val]
  ; (println (str "new change!: key= " key "; old=" old-val "; new=" new-val))
  ; todo: merge change to workspace
  
  ))



; current workspace
(def workspace (atom {}))

;;;;;;
; data handling routines


(defn list-projects []
  [{:name "Monte" :hash (hash "Monte")}
   {:name "MyFolder" :hash (hash "MyFolder")}
   {:name "Metropolis" :hash (hash "Metropolis")}
   {:name "KMC Booking" :hash (hash "KMC Booking")}
   {:name "Diploma" :hash (hash "Diploma")}
   ])



(defn set-project [hash]
  (let [proj (first(filter (fn [x] 
                (= (:hash x) hash)) 
                (:projects @workspace)))]
     (reset! changes 
          (conj @changes 
                [(System/currentTimeMillis) proj]))
     proj
  ))


(defn init 
  "initializes workspace to its initial state" 
  []
  
    (reset! workspace
            {:projects (list-projects)})
    
    @workspace)


(defn to-initial-state
  "resets all changes" ; maybe start new session instead?  
  []
  (println "starting from a blank page.")
  (reset! changes 
          (conj @changes 
                [(System/currentTimeMillis) (init)])))


; uncomment for development 
(to-initial-state)


(defn merge-changes [c1 c2]
  (let [[t1 & data1] c1
        [t2 & data2] c2]

    ; assume for now that data contains single change-set
    (let [a (first data1)
          b (first data2)]
    [t2 (merge-data a b)]
  )))


(defn workspace-diff [timestamp]
  (let [new-changes 
        (filter (fn [x] 
            (let [[t & data] x]
                ;(println (str "time is " t)) (println data) 
              (>= t timestamp))) @changes)]
    
    (when-not (empty? new-changes)
      (first(rest
       (reduce merge-changes new-changes))))))


(defn full-refresh 
  "merge and return all changes merged"
  []
  (workspace-diff 0)) 


(defn partial-refresh 
  "merge and return changes done after last-updated"
  [timestamp]
  (workspace-diff timestamp))
