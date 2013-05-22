(ns monte.status-ui  
  "Gui for Monte status page"
  (:require-macros [shoreleave.remotes.macros :as fm])
  (:require [jayq.core :as jq]
            [shoreleave.remotes.http-rpc :as rpc]
            [monte.ui :as ui])
  (:use [jayq.util :only [log wait]]
        [jayq.core :only [$ append empty ]]
        [crate.core :only [html]]))


(defn init-dom [cfg]
  ; (.log js/console (pr-str cfg))
  
  (list 
    [:h1 "Status"]
    [:div "to be done later..."]
    ))

(defn populate [update-url data]
  ; do something. later
  (.log js/console (str "update url is: " update-url)))



