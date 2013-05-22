(ns monte.settings-ui  
  "Gui for settings view"
  (:require-macros [shoreleave.remotes.macros :as fm])
  (:require [jayq.core :as jq]
            [shoreleave.remotes.http-rpc :as rpc])
  (:use [jayq.util :only [log wait]]
        [jayq.core :only [$ append]]
        [crate.core :only [html]]))


(defn list-settings[d]
  ; I do nothing
  )


(defn init-dom [cfg]
  (list 
    [:h3 "Settings"]
    [:ul.settings]))


(defn populate [data]
  (list-settings 
    (get data :settings)))

