(ns monte.runtime
  "Monte runtime engine — holds session and a list of workspace changes"
  (:require [monte.miners.core :as miners]
            [monte.core :as core])
  (:use [clojure.data :only [diff]]
        [monte.logger :only [dbg err]]))

;(defrecord Runtime )


; wrapper around merge
(defn merge-data [a b]
  #_(let [before (select-keys a (keys b))
        after  (select-keys b (keys a))]
    (when-not (empty? before)
      (dgb before "(" a ") will be replaced with " after "( " b ")"))
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
    (dbg "new change!: key= " key "; old=" old-val "; new=" new-val)
    ; todo: merge change to workspace
  ))



; current workspace
(def workspace (atom {}))

;;;;;;
; data handling routines


(defn entities-for-project[]
  ["tasks=(JIRAMiner)" ; display: id
   "commits=(VCSMiner)"; display: title
   "src=(SRCMiner)"    ; 
   
   ;["classes=(code_miner)"]
   ;["test-cases=classes.class_name{:ends 'Test'}"] 
   ;["commits=(git-miner)"]
   ;["users=(unify tasks.asignee classes.javadoc.author commits.author)"]
   ])


(defn connections-for-project[]
  ["tasks.id=commits.task"
   "commits.file=src.file"
   ;["dummy_miner="]
   ])


(defn vars-for-project[]
  [["REPO-URL" :url "https://github.com/ndrew/monte.git"]
   ["WORK-DIR" :path "Users/ndrw/monte/"]
   ["BUILD-SCRIPT-PATH" :path "Users/ndrw/monte/buildScript.sh"]])


(defn- miners-for-project [vars]
  ; todo: use session here
  (miners/m-list-meta))


(defn- monte-proj[]
  ; todo: loading from fs
  (let [vars (vars-for-project)
        miners (miners-for-project vars)]
        {
          :name "Monte"
          :vars vars
          :miners miners
          :entities (entities-for-project)
          :connections (connections-for-project)}))


(defn list-projects []
  ; todo: retrieving from fs
  (map #(merge % {:hash (hash (:name %))})
    [(monte-proj)]))


(defn set-project [hash]
  "sets current project"
  (let [proj (first(filter (fn [x] 
                (= (:hash x) hash)) 
                (:projects @workspace)))]
     (swap! changes conj  
                [(System/currentTimeMillis) {:current proj
                                             :projects []}])
     proj))


(defn init []
  "initializes workspace to its initial state" 
  (reset! workspace 
          {
           :projects (list-projects)
           :miners (miners/m-list-meta)
          })
  @workspace)


(defn to-initial-state []
  "resets all changes"
  ;(dbg "Workspace had been reset to initial state.")
  (let [change (init)]
    (dbg "Initial " change)
    (reset! changes 
            (conj [] 
                  [(- (System/currentTimeMillis) 1000) change]))))


(defn merge-changes [c1 c2]
  (let [[t1 & data1] c1
        [t2 & data2] c2]

    ; assume for now that data contains single change-set
    (let [a (first data1)
          b (first data2)]
    [t2 (merge-data a b)])))


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
;(to-initial-state)


(defn run-miners-proj[proj]
  (let [miners (:miners proj)
        entities (:entities proj)]
        ;(println "==============================")
        
        
        ;(map #(conj miners (core/parse-entity %1)) entities)
        
        (comment 
          (def result (doall
          (pmap 
             #(let[[d m] %1
                   r (core/process-entity-new d m)]
                  
                  ;(println (first d))
                  ;(println (pr-str r))
                  {(first d) r})
          (doall (map #(conj miners (core/parse-entity %1)) entities)))))
                  
        
        (swap! changes conj [(System/currentTimeMillis) {:data result
                                                         :connections (map core/process-connections 
                                                                           (:connections proj))}])
        result)))

(defn run-miners[project-id]
  "runs all miners"
  (println (str "running miners for project=" project-id))
  (when-let [proj (first(filter (fn [x] 
                (= (:hash x) project-id)) 
                (:projects @workspace)))]
    (run-miners-proj proj)))
