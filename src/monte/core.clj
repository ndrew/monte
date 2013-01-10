(ns monte.core
	"Monte core stuff"
	(:use [clojure.test]))

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
  :entities [
             ["tasks=(jira_miner).task{:regex #'regex'}"]
             ["classes=(code_miner)"]
             ; подумати про кластеризацію, в ідеалі — автоматом
             ["test-cases=classes.class_name{:ends 'Test'}"] 
             ["commits=(git-miner)"]
             ["users=(unify tasks.asignee classes.javadoc.author commits.author)"]]

  :connections [["classes.javadoc{:contain tasks.id}"]
                ["classes.dependencies{:contain classes.class_name}"]
                ["commit.files{:contain classes.file}"]
                ["commit.msg{:regex '{regex here}' tasks.id}"]
                
                ["classes.javadoc.raw{:contain users}"]
                ["users{:contain commits.author}"]
                ["users{:contain tasks.assignee}"]
                ]
})



(def props-regex #"(\..*)[\{]|\..*$")
(def filters-regex #"[\{](.+)[\}]")
(def miners-regex #"\((.+)\)")
(def not-entity-regex #"\(.+\)|\..+")


(defn- extract-filters[s]
  (let [[_ fltr] (re-find filters-regex s)]
    (when-not (nil? fltr)
      {:filter fltr} )))

 
;(extract-filters "classes=(code_miner)")
;(extract-filters "test-cases=classes.class_name{:ends 'Test'}")
  
(defn- extract-miners[s] 
  (let [[_ miner] (re-find miners-regex s)]
    (when-not (nil? miner)
      (if (not (.startsWith miner "unify"))
      {:miner miner} {:unify (clojure.string/replace miner #"unify\s" "")}))))


(defn- extract-props[s]
  (let [[a b] (re-find props-regex s)]
		(when-not (nil? a)
      {:props (if (nil? b) a b)})))


(defn- extract-entities[s]
  (let [e (clojure.string/replace s not-entity-regex "")]
  	(when-not (empty? e)
      {:entity e})))

(defn parse-expression[s] 
  (reduce merge ((juxt 
                  	extract-filters
                  	extract-miners	
                  	extract-props
                  	extract-entities
                  ) s)))


(defn parse-entity[s]
  (let [[_ a b](re-find #"(.+)=(.+)" s)]
    { (keyword a) (parse-expression b) }))


(extract-filters "tasks=(jira_miner).task{:regex #'regex'}")
(extract-filters "classes=(code_miner)")
(extract-filters "test-cases=classes.class_name{:ends 'Test'}")

(extract-miners "(jira_miner).task{:regex #'regex'}")
(extract-miners "(unify unify tasks.asignee classes.javadoc.author commits.author)")

(extract-props "")
(extract-props "(test)")
(extract-props "users")
(extract-props "(test).foo.bar.baz")
(extract-props "(test).foo.bar.baz{:contain ffff}")


(extract-entities "(classes)")
(extract-entities"(classes).baz")
(extract-entities "classes.baz")
(extract-entities "classes")
(extract-entities "classes.faz.baz{:f a}")

(parse-expression "(code_miner)")
(parse-expression "(code_miner){:contain fff}")
(parse-expression "(code_miner).baz")
(parse-expression "(code_miner).bar{:contain fff}")
(parse-expression "classes.class_name")
(parse-expression "classes.class_name{:contain fff}")


(parse-entity "classes=(code_miner)")