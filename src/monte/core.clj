(ns monte.core
	"Monte core stuff"
  (:use [clojure.string :only [split]]))

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
              }
            ]
  
  })

(def scheme {
  :entities ["tasks=(jira_miner).task{:regex #'regex'}"
             "classes=(code_miner)"
             "dummy-classes=(code_miner){:foo 'Bar'}"
             ; подумати про кластеризацію, в ідеалі — автоматом
             "test-cases=classes.class_name{:ends 'Test'}"
             "commits=(git-miner)"
             "users=(unify tasks.asignee classes.javadoc.author commits.author)"]

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


(defn- extract-props[s]
  (let [[a b c] (re-find props-regex s)]
		(when-not (nil? a)
      {:props (if (nil? b) c b)})))


(defn- extract-entities[s]
  (let [e (clojure.string/replace s not-entity-regex "")]
  	(when-not (empty? e)
      {:entity e})))


(defn parse-expression[s]
  (let [keys (split s  #"\.")
        data-f #(reduce merge ((juxt extract-miners extract-entities extract-filters) %))
        prop-f #(reduce merge ((juxt extract-props extract-filters) %))]
    (reduce 
        #(conj %1 (prop-f (str "." %2)))
         (vector(data-f (first keys))) (rest keys))))


(defn parse-entity[s]
  (when-not (nil? s)
    (let [[z a b](re-find #"(.+)=(.+)" s)]
      (when-not (empty? z)
      	(vector (keyword a) (parse-expression b))))))


