(ns monte.menu-ui  
  "Gui initial menu"
  (:require-macros [shoreleave.remotes.macros :as fm])
  (:require [jayq.core :as jq]
            [shoreleave.remotes.http-rpc :as rpc]
            [monte.ui :as ui])
  (:use [jayq.util :only [log wait]]
        [jayq.core :only [$ append empty ]]
        [crate.core :only [html]]))

(def dom "ul.projects")


;;; handlers 


(defn new-proj-handler[cfg e]
  (let [{f :f
         items :items} cfg
        a  ($ (.-srcElement e))
        li (.parent a)]
    
    (.detach a)
    (.removeClass li "new")

    (doseq [i items]
      (.append li (html i)))
    
    ;(.append li (html [:span {:class "clear"} "Name:"]))
    ;(.append li (html [:input {:type "text"} "testo"]))

    (let [ok      (html [:button.ok "ok"])
          cancel  (html [:button.cancel "cancel"])
          
          cancel-handler #(let [btn ($ (.-srcElement %))
                                prnt  (.parent btn)
                                li (ui/new-li-el)]
                      
                            (ui/add-new-btn li (partial new-proj-handler cfg))
                            (.replaceWith prnt li))
          
          ok-handler #(do (f %)
                          (cancel-handler %))] 
        
        (ui/add-clickable-el ok     li ok-handler)
        (ui/add-clickable-el cancel li cancel-handler))))

;;;;


(defn list-projects [projects events]
  (empty ($ dom))
  (let [{item-cfg :item-cfg
         on-click :item-click-listener
         on-new-item :new-item-listener} events]
    (doseq [p projects] 
      (ui/add-clickable-el (ui/li-el (item-cfg p)) dom on-click))
    
    (ui/add-new-btn 
      (.appendTo (ui/new-li-el) dom) on-new-item)))


(defn init-dom [cfg]
  (list 
    [:div.inline
     [:h3 "Projects"]
     [:ul.projects]]
    [:div#intro 
     [:h1 "Monte (alpha 0.1)"]
     "Монті – утилітка для програмістів, що будує інтерактивні візуалізації із коду програм й, можливо, з іншої супутньої інформації: тестів, систем контролю версій, баг-трекерів, логів, тощо."]))



(defn populate [data events]
  ;(.log js/console (pr-str projects))

  (list-projects 
    (get data :projects)
    (merge {:item-click-listener (constantly true) ; by default use url instead of js listener provided
            :new-item-listener   (partial new-proj-handler {:items [[:span {:class "clear"} "Name:"]
                                                                    [:input {:type "text"} "Testo"]]
                                                            :f #(do (.log js/console "tbd"))})
            :item-cfg            #(.error js/console "No item renderer function provided")}
           events)))



