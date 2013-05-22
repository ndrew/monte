(ns monte.main  
  "Frontend entry point"
  (:require-macros [shoreleave.remotes.macros :as fm])
  (:require [cljs.reader :as reader]
            [jayq.core :as jq]
            [crate.core :as crate]
            [shoreleave.remotes.http-rpc :as rpc]
            [monte.menu-ui :as menu-ui]
            [monte.project-ui :as proj-ui]
            [monte.errors-ui :as err-ui]
            [monte.settings-ui :as stngs-ui]
            [monte.status-ui :as status-ui])
  (:use [jayq.util :only [log wait]]
        [jayq.core :only [$ append detach]]
        [crate.core :only [html]]))

(def def-init-data {:view :index})

(defn- get-init-data[] 
  (if-let [edn (.-MonteInitCfg js/window)]
    (reader/read-string edn)
    def-init-data))


(defn dbg[& m]
  (append ($ "#debug") 
            (html [:pre m ])))


(defn- fill-viewport[dom] 
  (doseq [d dom]
          (append ($ "#viewport")
                  (html d))))

(defn init-ui![]
  "initializes RIA with data passed with html"
  
  
  ; do some global adjustments
  (let [init-data (get-init-data) 
        {view :view 
         cfg :cfg ; actually this is initial data
         update-url :update-url} init-data]

    (dbg (pr-str init-data))
    
    (cond 
      (= :index-page view) 
        (do 
          (fill-viewport (menu-ui/init-dom cfg))
          (menu-ui/populate cfg)
          )
      
      (= :project-page view)
        (do 
          (fill-viewport (proj-ui/init-dom cfg))
          (menu-ui/populate cfg))
      
      (= :settings-page view)
        (do
          (fill-viewport (stngs-ui/init-dom cfg))
          (stngs-ui/populate cfg))
       
      (= :status-page view)
        (do
          (fill-viewport (status-ui/init-dom cfg))
          (status-ui/populate update-url cfg))
            
      (= :error-page view)
        (fill-viewport (err-ui/init-dom cfg))
        
      :else 
        (fill-viewport (err-ui/init-dom {:error "WTF!"
                                         :details "Server sent incorrect initial data."})))))

(->> init-ui! 
     jq/document-ready)