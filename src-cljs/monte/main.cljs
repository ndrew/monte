(ns monte.main  
  "Frontend entry point"
(:require-macros [shoreleave.remotes.macros :as fm])
  (:require [cljs.reader :as reader]
            [jayq.core :as jq]
            [crate.core :as crate]
            [shoreleave.remotes.http-rpc :as rpc]
            [monte.project-ui])
  (:use [jayq.util :only [log wait]]
        [jayq.core :only [$ append]]
        [crate.core :only [html]]))

(def def-init-data {:view :index})


(defn- get-init-data[] 
  (if-let [edn (.-MonteInitCfg js/window)]
    (reader/read-string edn)
    def-init-data))


(defn init-ui![]
  "initializes RIA with data passed in html"

  (let [{view :view cfg :cfg} (get-init-data)]
    ; do some global adjustments
    ; initialize views
    (cond
      (= :index-page view) (let [dom (monte.project-ui/init-dom cfg)]
                             (doseq [d dom]
                               (append ($ "#viewport") (html d)))
                              
                             (monte.project-ui/init-data cfg))
      
      :else (js/alert "Wrong way!"))))

(->> init-ui! 
      jq/document-ready)