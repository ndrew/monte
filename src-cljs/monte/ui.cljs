(ns monte.ui
  
  (:require [fetch.remotes :as remotes]
	          [jayq.core :as jq])
  (:use [jayq.util :only [log wait clj->js]]
        [jayq.core :only [$ append]]
        [crate.core :only [html]])
  (:require-macros [fetch.macros :as fm]))


(def error (atom false)) ; todo: make use of it

; <!> HACK:
; 	these variables will be set on server while page will be generated
(def intro-view false)
(def project-view false)
(def project-hash 0) ; project-hash

; timestamp of latest update
(def latest-update (atom 0))

; todo: move here all jquery selectors
(def dom-projects "ul.projects")
(def dom-miners "#miner_table tbody")
(def dom-vars "#var_table tbody")


(def lbl-add "add new")

(def miner-schemas (atom []))

(defn tick
  "return current timespamp"
  []
  (. (new js/Date) getTime))


(defn list-projects [projects]
  (.empty ($ dom-projects))
  (doseq [p projects] 
    (do 
      (.append ($ dom-projects) 
        (html [:li 
              [:a {:href (str "/project/" (:hash p))}
                  (:name p)]
              [:span "..."]]))
      
      (log (pr-str p))))
  
  	(.append ($ dom-projects) 
             (html [:li {:class "new"} [:a {:href "#"} lbl-add]])))
	

(defn list-miners[miners]
  (.empty ($ dom-miners))
  (log (pr-str miners))
  (doseq [m miners]
    (let [[name id cfg] m]
      
      (comment(.append ($ dom-miners) 
               (html
                 [:tr [:td name] ; todo: name editing
                      [:td id]   ; todo: miner type displaying
                      ; todo: implement toggling
                      [:td (.htmlToDocumentFragment goog.dom "&#9660;")] ; dark magic to get unicode chars work
                    ]))))
      
      )
  
  ; todo: add miner functionality
  (.append ($ dom-miners) (html [:tr [:td {:class "new" :colspan "3"} [:a {:href "#"} lbl-add] ]])))


(defn list-vars[vars]
  (.empty ($ dom-vars))
  (log (pr-str vars))
  (doseq [v vars]
    (let [[name id value] v]
      
      (.append ($ dom-vars) 
               (html
                 [:tr [:td name] ; todo: name editing
                      [:td id]   ; todo: miner type displaying
                      ; todo: implement toggling
                      [:td value] 
                    ]))
      
      ))
  
  ; todo: add var functionality
  (.append ($ dom-vars) (html [:tr [:td {:class "new" :colspan "3"} [:a {:href "#"} lbl-add] ]])))



(defn select-project-view [link-id] 
  (let [view-id (str link-id "_view")
        links ($ "#viewport article a")
        views ($ "#viewport article .view")]
    (log (str "view selected " view-id))
    
    (.attr links "href" "#")
  (.click links  
      (fn[e]
        (let [el (.-srcElement e)
              id (str "#" (.-id el))]
          (when-not (clojure.string/blank? (.-href (.-srcElement e)))
            (select-project-view id)))))

    (.hide views)

    (.removeAttr ($ link-id) "href")
    (.toggle ($ view-id))))



(defn set-project [proj]
  (select-project-view "#miner"))

;;; ui-update stuff

(defn workspace-updated [workspace]
  (log "workspace updated")
  (log (pr-str workspace))
  (.text ($ :#debug) (pr-str workspace))
  
  (reset! latest-update (tick))
  
  (when (and intro-view 
             (not (nil? (:projects workspace)))
    (list-projects (:projects workspace))))
  
  (when project-view
     
    (when-not (nil? (:miners workspace))
      (reset! miner-schemas (:miners workspace))) ; store all miners
              
    (when-not (nil? (:current workspace))        
      (let [proj (:current workspace)]
        (.text ($ "#viewport article h1") (:name proj))
        (list-vars (:vars proj)))
        ; list-miners    
    )
  )
) 


;;;; looping stuff ;;;;
(def repeat-handle (atom 0))

(defn refresh[& last-updated]
  (fm/remote (get-workspace (first last-updated)) [workspace] 
	(when-not 
      (nil? workspace)
	  (workspace-updated workspace))))


(defn infinite-loop [ms func]
	(js/setInterval 
		(fn[] 
			(cond 
				(= true @error) (js/clearInterval @repeat-handle))
				:else (func)
			) ms))


(defn ui-init []
  (when project-view 
    (fm/remote (set-project project-hash) [proj]
        (set-project proj))))

;;;;;;;;;;;;;;;;;;;
;; main
(jq/document-ready 
  (fn [] 
      (ui-init)
	  (refresh) ; full refresh
	  (let [poll-interval 2000]
	  (reset! repeat-handle 
	          (infinite-loop poll-interval 
                            (fn [] 
					            (refresh @latest-update)))))))