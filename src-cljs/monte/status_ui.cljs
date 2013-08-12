(ns monte.status-ui  
  "Gui for Monte status page"
  (:require-macros [shoreleave.remotes.macros :as fm])
  (:require [jayq.core :as jq]
            [shoreleave.remotes.http-rpc :as rpc]
            [monte.ui :as ui]
            [monte.utils :as utils]
            [strokes :refer [d3]]
            )
  (:use [jayq.util :only [log wait]]
        [jayq.core :only [$ append empty attr]]
        [crate.core :only [html]]))


(def error (atom false))
(def repeat-handle (atom 0))
(def latest-update (atom 0)) ; timestamp of latest update

(def infinite-loop (partial 
                     utils/infinite-loop repeat-handle error))

(def ^:dynamic *auto-refresh* false) ; defalt true is set for debugging before launcher 


(defn tick []
  "return current timespamp"
  (. (new js/Date) getTime))


(defn set-status [status]
  (.log js/console (pr-str status)))


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
    [:div.status]
    [:div#mem-graph]))


(defn- init-d3 []  
  (strokes/bootstrap)
  ; should return a function for drawing
  
  
  (let [m [10 10 10 10] ; margins top left bottom right
        w (- 200 (m 1) (m 3))
        h (- 100 (m 0) (m 2))
        svg (-> d3 (.select "#mem-graph") (.append "svg")
                (.attr {:width  (+ w (m 1) (m 3))
                        :height (+ h (m 0) (m 2))})
                (.append "g")
                (.attr {:transform (str "translate(" (m 3) "," (m 0) ")")}))
        *x* (atom 0)
        *cache* (atom {})
        ]
    ; todo: add partial
    (fn [mem-data] 
      (let [{total :total} mem-data
            ya (-> d3 .-scale 
                 (.linear)
                 (.domain [0 total]); tbd:
                 (.range [h (m 0)]))
            svg-circle-fn (fn [nm attr]
                            (let [el (-> svg 
                                (.selectAll nm)
                                (.data (vec [
                                        ;mem-data
                                        10 20 30 40 50
                                        ])))]
                              (-> el
                                  (.enter)
                                  (.append "circle")
                                  (.attr (merge 
                                           {:cx #(do (+ @*x* % 100))
                                            :r 1
                                            :fill "black"
                                            } attr)))
                              
                              ;(.log js/console el)
                              
                              ; tbd: deletion
                              (-> el
                                  (.filter #(do
                                              (.log js/console (str "filtering " % (> % 25)))
                                              (> % 25) ; true
                                              ))
                                  (.data [])
                                  (.exit)
                                  (.remove))
                              
                              
                              )
                            
                            )]
        #_(svg-circle-fn "g.free" {
                                 :cy #(ya 
                                        ;(:free %)
                                        %)
                                 :fill "red"
                                 })
        (svg-circle-fn "g.used" {
                                 :cy #(ya
                                         ;(:used %)
                                         %)
                                 :fill "blue"
                                 })
        ;(-> svg 
        ;  (.selectAll "g.free")
        ;  (.data [mem-data])
        ;  (.exit)
        ;  (.remove))
        
          (let [x @*x*
                new-x (+ x 10)]
            (reset! *x* (if (> w new-x) 
                          new-x 0))
            x)
        
      
          ))))

(defn populate [update-url data]
  
;http://bl.ocks.org/mbostock/1256572
  
  (.log js/console (str "update url is: " update-url))
  
  (let [draw-f (init-d3)
        handler (fn[& [callback]] 
                  (.log js/console (tick))
                  (refresh @latest-update 
                           #(do
                              (draw-f (:memory (:monte %)))
                              (reset! latest-update (tick))
                              (if callback)
                                (callback))))]
    (ui/clickable "#get-status-btn"
                  (fn[e]
                    (attr ($ "#get-status-btn") {:disabled true})
                    (handler #(attr ($ "#get-status-btn") {:disabled false}))))
  
    ; updating awaits
    (when *auto-refresh*
      (infinite-loop 5000 handler))))