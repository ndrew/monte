(ns monte.runtime
  "Monte runtime engine — holds session and a list of workspace changes"
  (:require [monte.miners.core :as miners])
  (:use [clojure.data :only [diff]]))


; wrapper around merge
(defn merge-data [a b]
  (let [before (select-keys a (keys b))
        after  (select-keys b (keys a)) 
        ]
    ;(when-not (empty? before)
    ;  (println (str before "(" a ") will be replaced with " after "( " b ")")))
    
    )
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

(defn get-miner-schemas[]
  (doall
    (map (fn[x] 
      (let [[miner-type constructor] x
             miner (constructor {})] ; todo: loading config
              [(.getName miner-type) miner-type (miners/get-schema miner)]))
              (miners/list-all-miners))))


(defn miners-for-project[]
  (let [cfg {}]
    ; TODO: ?storing miners somewher
   (doall
     (map (fn[x] 
            (let [[miner-type constructor] x
                   miner (constructor cfg)] ; todo: loading config
                    [(last (clojure.string/split (.getName miner-type) #"\.")) 
                     miner-type 
                     (miners/get-schema miner)]))
          (miners/list-all-miners)))))


(defn entities-for-project[]
  [["tasks=(JIRAMiner)"]
   ;["classes=(code_miner)"]
   ;["test-cases=classes.class_name{:ends 'Test'}"] 
   ;["commits=(git-miner)"]
   ;["users=(unify tasks.asignee classes.javadoc.author commits.author)"]
   ])


(defn connections-for-project[]
  [
   ;["dummy_miner="]
   ])


(defn vars-for-project[]
  [["REPO-URL" :url "https://github.com/ndrew/monte.git"]
   ["WORK-DIR" :path "Users/ndrw/monte/"]
   ["BUILD-SCRIPT-PATH" :path "Users/ndrw/monte/buildScript.sh"]])


(defn list-projects []
  ; retrieving from fs
  (map #(merge % {:hash (hash (:name %))})
    [{
      :name "Monte" ; todo: cfg by name
      :vars (vars-for-project)
      :miners (miners-for-project)  
      :entities (entities-for-project)
      :connections (connections-for-project)
    }
    ; these are awaiting for better times
    ;{:name "MyFolder"}
    ;{:name "Metropolis"}
    ;{:name "KMC Booking"}
    ]))



(defn set-project [hash]
  (let [proj (first(filter (fn [x] 
                (= (:hash x) hash)) 
                (:projects @workspace)))]
     (reset! changes 
          (conj @changes 
                [(System/currentTimeMillis) 
                 {:current proj
                  :projects []}]))
     proj
  ))


(defn init []
  "initializes workspace to its initial state" 
  (reset! workspace 
          {:projects (list-projects)
           :miners (get-miner-schemas)
          }) 
  @workspace)


(defn to-initial-state []
  "resets all changes"
  (println "Workspace had been reset to initial state.")
  (reset! changes 
          (conj [] [(- (System/currentTimeMillis) 1000) (init)])))




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


(defn full-refresh []
  "merge and return all changes merged"
  (workspace-diff 0)) 


(defn partial-refresh [timestamp]
  "merge and return changes done after last-updated"
  (workspace-diff timestamp))


; uncomment for development 
(to-initial-state)

