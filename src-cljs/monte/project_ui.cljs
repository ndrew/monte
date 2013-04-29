(ns monte.project-ui  
  "Gui for creating new project"
  (:require-macros [shoreleave.remotes.macros :as fm])
  (:require [jayq.core :as jq]
            [shoreleave.remotes.http-rpc :as rpc])
  (:use [jayq.util :only [log wait]]
        [jayq.core :only [$ append ]]
        [crate.core :only [html]]))

(def dom-projects "ul.projects")
(def lbl-add "add new")

; TBD: refactor this so clicks will be handled properly


(defn new-project[e]
  (let [a  ($ (.-srcElement e))
        li (.parent a)]
    (.empty li)
    (.removeClass li "new")
    (.click li log)
    (.append li (html [:span "new project name:"]))
    (.append li (html [:input {:type "text"} "testo"]))
    (.append li (html [:button.ok "ok"]))
    (.append li (html [:button.cancel "cancel"]))))


(defn list-projects [projects]
  (.log js/console (pr-str projects))
  
  (.empty ($ dom-projects))
  (doseq [p projects] 
    (do 
      (append ($ dom-projects) 
        (html [:li 
              [:a {:href (str "/project/" (:hash p))}
                  (:name p)]
              [:span "..."]])))) ; todo: add last modified time here
  
  (let [el (html [:li {:class "new"} [:a {:href "#"} lbl-add]])]
    (.append ($ dom-projects) el)
    (.click ($ (str dom-projects " .new" )) new-project)))


(defn init-dom [cfg]
  (list 
    [:h3 "Projects"]
    [:ul.projects]))


(defn init-data [cfg]
  (list-projects 
    (get cfg :projects)))