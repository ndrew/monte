(ns monte.project-ui  
  "Gui for creating new project"
  (:require-macros [shoreleave.remotes.macros :as fm])
  (:require [jayq.core :as jq]
            [shoreleave.remotes.http-rpc :as rpc])
  (:use [jayq.util :only [log wait]]
        [jayq.core :only [$ append empty]]
        [crate.core :only [html]]))

(def dom-projects "ul.projects")
(def lbl-add "add new")

; TBD: refactor this so clicks will be handled properly


(defn new-project[e]
  #_(let [a  ($ (.-srcElement e))
        li (.parent a)]
    (.detach li)
    (.removeClass li "new")
    
    (.click li log)
    (.append li (html [:span "new project name:"]))
    (.append li (html [:input {:type "text"} "testo"]))
    (.append li (html [:button.ok "ok"]))
    (.append li (html [:button.cancel "cancel"]))))


(defn load-project-handler[& e]
  (js/alert "піу!")
  true)

(defn list-projects [projects]
  (.log js/console (pr-str projects))
  
  (empty ($ dom-projects))
  
  (doseq [p projects] 
    (let [pr-url (str "/project/" (:hash p))
          item   [:li 
                    [:a {:href pr-url
                         :onclick (fn[x] (js/alert "FFFFFF"))} (:name p)]
                  [:span "..."]]
          dom     (html item)] 
      (.click ($ dom) load-project-handler)
      (append ($ dom-projects) dom))) ; todo: add last modified time here
  
  #_(let [el (html [:li {:class "new"} [:a {:href "#"} lbl-add]])]
    (.append ($ dom-projects) el)
    (.click ($ (str dom-projects " .new" )) new-project)))


(defn init-dom [cfg]
  (list 
    [:h3 "Projects"]
    [:ul.projects]))


(defn populate [data]
  (list-projects 
    (get data :projects)))

