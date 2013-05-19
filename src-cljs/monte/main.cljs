(ns monte.main  
  "Frontend entry point"
  (:require-macros [shoreleave.remotes.macros :as fm])
  (:require [cljs.reader :as reader]
            [jayq.core :as jq]
            [crate.core :as crate]
            [shoreleave.remotes.http-rpc :as rpc]
            [monte.project-ui :as pr-ui]
            [monte.errors-ui :as err-ui])
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
  (let [{view :view cfg :cfg} (get-init-data)]

    (dbg (str "view:  " 
              (pr-str view)
              "\ncfg:"
              (pr-str cfg)))
    
    (cond 
      (= :index-page view) 
        (do 
          (fill-viewport (pr-ui/init-dom cfg))
          (pr-ui/populate cfg)
          )
      
      (= :project-page view)
        (do 
          ; do nothing. yet
          )
      
      (= :error-page view)
        (fill-viewport (err-ui/init-dom cfg))
        
      :else 
        (fill-viewport (err-ui/init-dom {:error "WTF!"
                                         :details "Server sent incorrect initial data."})))))

(->> init-ui! 
     jq/document-ready)