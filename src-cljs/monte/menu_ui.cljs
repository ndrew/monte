(ns monte.menu-ui  
  "Gui initial menu"
  (:require-macros [shoreleave.remotes.macros :as fm])
  (:require [jayq.core :as jq]
            [shoreleave.remotes.http-rpc :as rpc]
            [monte.ui :as ui]
            [monte.utils :as utils])
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
    
    ; tbd: add form here
    
    (doseq [i items]
      (.append li (html i)))
    (let [ok      (html [:button.ok {:type "button"} "ok"])
          cancel  (html [:button.cancel {:type "button"} "cancel"])
          clear-form #(let [btn ($ (.-srcElement %))
                            prnt  (.parent btn)
                            li (ui/new-li-el)]
                        (ui/add-new-btn li (partial new-proj-handler cfg))
                        (.replaceWith prnt li))
          ok-handler #(let [btn ($ (.-srcElement %))
                            prnt  (.parent btn)
                            arr (.makeArray js/jQuery (.children prnt))
                            values (areduce arr i ret {} 
                                            (let [item (aget arr i)
                                                  nm (.attr ($ item) "name")] ; the same approach as for forms  
                                              (if nm
                                                (assoc ret nm (.val ($ item)))
                                                ret)))]                             
                        (f values)
                        (clear-form %))] 
      (ui/add-clickable-el ok     li ok-handler)
      (ui/add-clickable-el cancel li clear-form))))
    

;;;;


(defn list-projects [projects events]
  (empty ($ dom))
  (let [{item-render :item-render
         on-click :item-click-listener
         on-new-item :new-item-listener} events]
    (doseq [p projects] 
      (ui/add-clickable-el (ui/li-el (item-render p)) dom on-click))
    
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
  (list-projects 
    (get data :projects)
    (merge {:item-click-listener (constantly true) ; by default use url instead of js listener provided
            :new-item-listener   (partial new-proj-handler (:new-item-cfg events {:items []
                                                                                  :f #(.error js/console "No new handler provided")}) ) 
            :item-render         #(.error js/console "No item renderer function provided")}
           (dissoc events
                   :new-item-cfg))))



