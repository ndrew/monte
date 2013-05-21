(ns monte.project-ui  
  "Gui for creating new project"
  (:require-macros [shoreleave.remotes.macros :as fm])
  (:require [jayq.core :as jq]
            [shoreleave.remotes.http-rpc :as rpc])
  (:use [jayq.util :only [log wait]]
        [jayq.core :only [$ append empty ]]
        [crate.core :only [html]]))

(def dom-projects "ul.projects")

(def lbl-add "add new")

(def new-li [:li {:class "new"}])


(defn add-clickable-el[el to handler]
  (.appendTo (.click ($ el) handler)
             ($ to)))

(defn add-new-btn[li handler] 
  (add-clickable-el (html [:a {:href "#"} lbl-add]) li handler))
  
 
(defn new-proj-handler[e]
  (let [a  ($ (.-srcElement e))
        li (.parent a)]
    (.detach a)
    (.removeClass li "new")

    (.append li (html [:span "new project name:"]))
    (.append li (html [:input {:type "text"} "testo"]))
    (let [ok      (html [:button.ok "ok"])
          cancel  (html [:button.cancel "cancel"])
          ok-handler #(do (js/alert "tbd"))
          cancel-handler #(let [btn ($ (.-srcElement %))
                                prnt  (.parent btn)
                                li ($ (html new-li))]
                      
                            (add-new-btn li new-proj-handler)
                            (.replaceWith prnt li))] 
        
        (add-clickable-el ok     li ok-handler)
        (add-clickable-el cancel li cancel-handler))))


(defn load-proj-handler[& e]
  (js/alert "піу!")
  true)

(defn list-projects [projects]
  (.log js/console (pr-str projects))
  
  (empty ($ dom-projects))
  (doseq [p projects] 
    (let [pr-url (str "/project/" (:hash p))
          item   [:li 
                    [:a {:href pr-url} (:name p)]
                  [:span "..."]]
          dom     (html item)] 
    
      (add-clickable-el dom dom-projects load-proj-handler))) 
      ; todo: add last modified time here
  
  (let [li (.appendTo ($ (html new-li))
                dom-projects)]
    
    (.log js/console li)
                            
    
    (add-new-btn li new-proj-handler)))


(defn init-dom [cfg]
  (list 
    [:h3 "Projects"]
    [:ul.projects]))


(defn populate [data]
  (list-projects 
    (get data :projects)))

