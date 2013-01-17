(ns monte.dummies
  "Holds canned data for diploma presentation")


;;;;;;;;;
; MINERS

; dummy data for JIRA miner
(def tasks [{:id "TASK-0" 
             :title "TASK-0: initial task"
             :assigne "Someone Else <some@one.else>"}
            {:id "TASK-1"
             :title "TASK-1: do something"
             :assigne "Andrew Sernyak <ndrew.sernyak@gmail.com>"}
            {:id "TASK-2"
             :title "TASK-2: do something else"
             :assigne "Ihor Lozynsky <aigooor@gmailc.com>"}])

(def commits [{
               :rev "1"
               :task "TASK-0"
               :files ["Core.java"]
               :msg "Started with TASK-0"
               :author "ndrew.sernyak@gmail.com"
             } {
               :rev "2"
               :task "TASK-1"
               :files ["Core.java"] 
               :msg "Implementing TASK-1"
               :author "ndrew.sernyak@gmail.com"
             } {
               :task "TASK-2"
               :rev "3"
               :files ["Utils.java" "Core.java"]
               :msg "Implementing TASK-2"
               :author "aigooor@gmail.com"
             } {
               :rev "4"
               :files ["Utils.java"] 
               :msg "bugfix"
               :author "some@one.else"
              }])


(def src-analysis-data [{ :file "project.clj" } 
                        { 
                          :file "Core.java"
                          :class_name "Core"
                          :dependencies ["java.util.List" "Utils"]
                          :javadoc [{:author "Andrew" :raw "12:/** @author Andrew \n impl of TASK-1 */"}  {:author "Ihor" :raw "42:/** @author Ihor \n necessary changes for TASK-2 */"} ]    
                        } { 
                          :file "Utils.java"
                          :class_name "Utils"
                          :dependencies ["java.util.List"]
                          :javadoc [{:raw "43:/** \n impl of TASK-1 */"}]    
                        }])
