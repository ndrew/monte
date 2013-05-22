(ns monte.menu-ui  
  "Gui initial menu"
  (:require-macros [shoreleave.remotes.macros :as fm])
  (:require [jayq.core :as jq]
            [shoreleave.remotes.http-rpc :as rpc]
            [monte.ui :as ui])
  (:use [jayq.util :only [log wait]]
        [jayq.core :only [$ append empty ]]
        [crate.core :only [html]]))

(def dom-projects "ul.projects")

(defn new-proj-handler[e]
  (let [a  ($ (.-srcElement e))
        li (.parent a)]
    (.detach a)
    (.removeClass li "new")

    (.append li (html [:span {:class "clear"} "Name:"]))
    (.append li (html [:input {:type "text"} "testo"]))

    (let [ok      (html [:button.ok "ok"])
          cancel  (html [:button.cancel "cancel"])
          
          cancel-handler #(let [btn ($ (.-srcElement %))
                                prnt  (.parent btn)
                                li (ui/li-el)]
                      
                            (ui/add-new-btn li new-proj-handler)
                            (.replaceWith prnt li))
          
          ok-handler #(do (js/alert "tbd")
                          (cancel-handler %))] 
        
        (ui/add-clickable-el ok     li ok-handler)
        (ui/add-clickable-el cancel li cancel-handler))))


(defn load-proj-handler[& e]
  (js/alert "піу!")
  true)

(defn from-time [t] 
  (if (= 0 t)
    "never"
    (.fromNow (js/moment t))))

(defn list-projects [projects]
  (.log js/console (pr-str projects))
  
  (empty ($ dom-projects))
  (doseq [p projects] 
    (let [pr-url (str "/project/" (:hash p))
          item   [:li [:a {:href pr-url} (:name p)]
                  [:span (str " (" (from-time 
                                     (:last-opened p 0)) ")")]]
          dom     (html item)] 
    
    (ui/add-clickable-el dom dom-projects load-proj-handler))) 
  
  (let [li (.appendTo 
             (ui/li-el)
             dom-projects)]
    
    (ui/add-new-btn li new-proj-handler)))


(defn init-dom [cfg]
  (list 
    [:div.inline
     [:h3 "Projects"]
     [:ul.projects]]
    [:div#intro 
     [:h1 "Monte (alpha 0.1)"]
     "Монті – утилітка для програмістів, що будує інтерактивні візуалізації із коду програм й, можливо, з іншої супутньої інформації: тестів, систем контролю версій, баг-трекерів, логів, тощо."]))


(defn populate [data]
  (list-projects 
    (get data :projects)))



