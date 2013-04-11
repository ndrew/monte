(ns monte.core
	"Monte core stuff"
  (:use [clojure.string :only [split]]
        [monte.logger :only [dbg]]))

(def data {
  :tasks [
          { :assigne "Someone Else <some@one.else>"
            :id "TASK-0: initial task"
           }
          { :assigne "Andrew Sernyak <ndrew.sernyak@gmail.com>"
            :id "TASK-1: do something"}
          { :assigne "Ihor Lozynsky <aigooor@gmailc.com>"
            :id "TASK-2: do something else"
          }]
  :classes [{ :file "{WORK-DIR}/project.clj"
              
            }
            { 
              :file "{WORK-DIR}/Core.java"
              :class_name "Core"
              :dependencies ["java.util.List" "Utils"]
              :javadoc [{:author "Andrew" :raw "12:/** @author Andrew \n impl of TASK-1 */"}  {:author "Ihor" :raw "42:/** @author Ihor \n necessary changes for TASK-2 */"} ]    
            }
            
            { 
              :file "{WORK-DIR}/Utils.java"
              :class_name "Utils"
              :dependencies ["java.util.List"]
              :javadoc [{:raw "43:/** \n impl of TASK-1 */"}]    
            }]
 	:commits [{
               :rev "1"
               :files ["{WORK-DIR}/Core.java"]
               :msg "Started with TASK-0"
               :author "ndrew.sernyak@gmail.com"
             }
             {
               :rev "2"
               :files ["{WORK-DIR}/Core.java"] 
               :msg "Implementing TASK-1"
               :author "ndrew.sernyak@gmail.com"
             }
             {
               :rev "3"
               :files ["{WORK-DIR}/Utils.java" "{WORK-DIR}/Core.java"]
               :msg "Implementing TASK-2"
               :author "aigooor@gmail.com"
             }
             {
              :rev "4"
               :files ["{WORK-DIR}/Utils.java"] 
               :msg "bugfix"
               :author "some@one.else"
              }]
  
  })

(def scheme {
  :entities ["tasks=(jira_miner).task{:regex #'regex'}"
             "classes=(code_miner)"
             "dummy-classes=(code_miner){:foo 'Bar'}"
             ; подумати про кластеризацію, в ідеалі — автоматом
             "test-cases=classes.class_name{:ends 'Test'}"
             "commits=(git-miner)"
             ; TODO: regex for unify
             ;"users=(unify tasks.asignee classes.javadoc.author commits.author)"
             ]

  :connections ["classes.javadoc{:contain tasks.id}"
                "classes.dependencies{:contain classes.class_name}"
                "commit.files{:contain classes.file}"
                "commit.msg{:regex '{regex here}' tasks.id}"
                "classes.javadoc.raw{:contain users}"
                "users{:contain commits.author}"
                "users{:contain tasks.assignee}"]
})



(def props-regex #"\.(.*)[\{]|\.(.*)$")
(def filters-regex #"[\{](.+)[\}]")
(def miners-regex #"\((.+)\)")
(def not-entity-regex #"\(.+\)|\..+|\{.+\}")


(defn- extract-filters[s]
  (let [[_ fltr] (re-find filters-regex s)]
    (when-not (nil? fltr)
      {:filter fltr} )))

   
(defn- extract-miners[s] 
  (let [[_ miner] (re-find miners-regex s)]
    (when-not (nil? miner)
      (if (not (.startsWith miner "unify"))
      {:miner miner} 
      (let [unifier (clojure.string/replace miner #"unify\s|unify" "")]
      	(when-not (empty? unifier) {:unify unifier})
      )))))


(defn- extract-unification[s]
  (let [[_ miner] (re-find miners-regex s)]
    (when-not (nil? miner)
      (if (.startsWith miner "unify")
        (let [unifier (clojure.string/replace miner #"unify\s|unify" "")]
          (when-not (empty? unifier) {:unify unifier}))))))    

(defn- extract-props[s]
  (let [[a b c] (re-find props-regex s)]
		(when-not (nil? a)
      {:props (if (nil? b) c b)})))


(defn- extract-entities[s]
  (let [e (clojure.string/replace s not-entity-regex "")]
  	(when-not (empty? e)
      {:entity e})))


(defn parse-expression[s]
  ; add UNIFICATION
  ; 
  ;
  (if-let [unifier (:unify (extract-unification s))]
    {:tobe :done}
    (let [keys (split s  #"\.")
          data-f #(reduce merge ((juxt extract-miners extract-entities extract-filters) %))
          prop-f #(reduce merge ((juxt extract-props extract-filters) %))]
      (reduce 
          #(conj %1 (prop-f (str "." %2)))
           (vector(data-f (first keys))) (rest keys)))))
  
(defn parse-entity[s]
  (when-not (nil? s)
    (let [[z a b](re-find #"(.+)=(.+)" s)]
      (when-not (empty? z)
      	(vector (keyword a) (parse-expression b))))))


;;;;;;;;;;;;;
; accessors
  
(defn- access-single-property[data property]
  (let [prop (keyword property)]
    (dbg "access-single-property: " prop)
    (cond
      (map? data) (get data prop)
      (vector? data) (vec(map #(access-single-property % prop) data))
    )
  )
)


(defn access-property[data prop]
  "accesses 'property' of data, nil otherwise"
  (let [keys (split prop #"\.")]
        (reduce #(access-single-property %1 %2)
                data keys)))


;;;;;;;;;;;;;
; miner and entities storage

(def raw-data (atom {}))

 
 
;(defmacro log[& body] ; for testing purposes
;  `(println (str ~@body " at " (System/currentTimeMillis) )))


(defn get-async[key & f]
  
  (when key
    
    ; should be synchronized without this
    (when (nil? f)
      (while (not (get @raw-data key))
        ;(log "waiting for " key)             
        (Thread/sleep 100) 
        (get @raw-data key)))
    
    
    
    (locking raw-data
    (when-not (get @raw-data key)
      (when f
          (let [lock (future
                          ;(log "\t\thello from future " key)
                          ((first f)))]
            ;(log "SETTING FUTURE : " key)
            (swap! raw-data conj (hash-map key lock))))))
    
    ;(log "getting " key " async")
    @(get @raw-data key)
      
    ))

  
(defn process-entity[[name config]] 
  ;(log "\tprocessing " name " " (pr-str config) )

  (let [key (keyword name)
        data-cfg (first config)
        props (rest config)]
    
    (get-async (keyword name) 
      #(let [ mk (keyword (:miner data-cfg))
              ek (keyword (:entity data-cfg))
              uk (keyword (:unify data-cfg))
              miner-data (get-async mk 
                           (fn[] 
                             (Thread/sleep (rand-int 10000) )
                             {:testo mk})) ; todo: miner calling
              entity-data (get-async ek)
              fltr  (:filter data-cfg)]
                
                ; todo: unification handling
                (let [data (if mk miner-data ; first try data from miner, otherwise - we have entity
                               (:data entity-data))] 
                  ; filtration of data
                  
                  (hash-map 
                    :entity key
                    :data (reduce (fn[x y] 
                            (let [prop (access-property x (:props y))]
                              (if (:filter y)
                              prop ; filtration
                              prop))) data props)))))))


(defn- reduce-data [data props] 
  (reduce (fn[x y] 
    (let [prop (access-property x (:props y))]
      (if (:filter y)
      prop ; filtration
      prop))) data props))

; todo: refactor
(defn process-entity-new[[name config] miners] 
  ;(log "processing " name " " (pr-str config) )
  ;(log "miners=" (apply str miners) )
  
  (let [key (keyword name)
        data-cfg (first config)
        props (rest config)]
    
    (get-async (keyword name) 
      #(let [ mk (keyword (:miner data-cfg))
              ek (keyword (:entity data-cfg))
              uk (keyword (:unify data-cfg))
              miner-data (get-async mk 
                           (fn[] 
                             (monte.miners.core/f((ns-resolve 'monte.miners.impl (symbol (str "->" (.getName mk)))) {})) ; todo: retrieving cfg from miners
                             
                             )) 
              entity-data (get-async ek)
              fltr  (:filter data-cfg)]
                
                ; todo: unification handling
                (let [data (if mk miner-data ; first try data from miner, otherwise - we have entity
                               (:data entity-data))] 
                  ; filtration of data
                  
                  (hash-map 
                    :entity key
                    :data (reduce-data data props)))))))


(defn process-connections[cstr]
  (let [[s1 s2] (clojure.string/split cstr #"=")
        e1 (parse-expression s1)
        e2 (parse-expression s2)]

        ;(println "FIRST ENTITY")
        ;(println (pr-str e1))
        ;(println (pr-str e2))

         ;(println (pr-str (reduce-data (:data (get-async (keyword (:entity (first e1))))) (rest e1))))
         ;(println (pr-str (reduce-data (:data (get-async (keyword (:entity (first e2))))) (rest e2))))
       
  (let [d1 (reduce-data (:data (get-async (keyword (:entity (first e1))))) (rest e1))
        d2 (reduce-data (:data (get-async (keyword (:entity (first e2))))) (rest e2))
        
        adj (for [x (range (count d1))]
              (for [y (range (count d2))]
                (if (= (nth d1 x) (nth d2 y)) [x y] nil)
                ))]
      [(keyword (:entity (first e1)))
       (keyword (:entity (first e2)))
       (remove empty? (map (fn[x] (remove nil? x)) (filter #(not (= (list nil nil) %)) (vec adj))))]    
    )))