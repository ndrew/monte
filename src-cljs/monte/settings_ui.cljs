(ns monte.settings-ui  
  "Gui for settings view"
  (:require-macros [shoreleave.remotes.macros :as fm])
  (:require [jayq.core :as jq]
            [shoreleave.remotes.http-rpc :as rpc])
  (:use [jayq.util :only [log wait]]
        [jayq.core :only [$ append]]
        [crate.core :only [html]]))


(defn init []
  
  (js/alert "FOOO!")

  )
