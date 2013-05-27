(ns monte.status-ui  
  "Gui for Monte status page"
  (:require-macros [shoreleave.remotes.macros :as fm])
  (:require [jayq.core :as jq]
            [shoreleave.remotes.http-rpc :as rpc]
            [monte.ui :as ui]
            [monte.utils :as utils])
  (:use [jayq.util :only [log wait]]
        [jayq.core :only [$ append empty attr]]
        [crate.core :only [html]]))


(def error (atom false))
(def repeat-handle (atom 0))
(def latest-update (atom 0)) ; timestamp of latest update

(def infinite-loop (partial 
                     utils/infinite-loop repeat-handle error))

(defn tick []
  "return current timespamp"
  (. (new js/Date) getTime))


(defn set-status [status]
  (.log js/console (pr-str status)))


; tbd — more generic method of doing such requests
(defn refresh[last-updated callback]
  "pings backend for changes"
  (fm/rpc (get-app-status last-updated) [status] 
          (when-not (nil? status)
            (set-status status)
            (callback status))))


(defn init-dom [cfg]
  (list 
    [:h1 "Status"]
    [:button#get-status-btn "get status"]
    [:div.status]))


(defn populate [update-url data]
  ; do something. later
  (.log js/console (str "update url is: " update-url))
  (ui/clickable "#get-status-btn"
                (fn[e]
                  (attr ($ "#get-status-btn") {:disabled true})
                  (refresh 0 #(attr ($ "#get-status-btn") {:disabled false}))))
  
  (let [handler (fn[] 
                  (.log js/console (tick))
                  (refresh @latest-update #(reset! latest-update (tick))))]
    ; updating awaits
    (infinite-loop 5000 handler)
    ))