(ns monte.ui
  (:require 
    [fetch.remotes :as remotes]
	[jayq.core :as jq])
  (:use [jayq.util :only [log wait clj->js]]
	[jayq.core :only [$ append]])
  (:require-macros [fetch.macros :as fm]))


(defn tick
  "return current timespamp"
  []
  (. (new js/Date) getTime))


;;;;;; refactoring ;;;;;;;


; <!> HACK:
; 	these variables will be set on server while page will be generated
(def intro-view false)
(def project-view false)
(def project-hash 0) ; project-hash

; timestamp of latest update
(def latest-update (atom 0))

(defn load-projects [projects]
  (.empty ($ "ul.projects"))
  (doseq [p projects] 
    (do
      ; (log ($ :#projects))
      
      (.append ($ "ul.projects") 
               (str "<li>"
                    "<a href='/project/" (:hash p) "'>" 
                    	(:name p) 
                    "</a>"
                    "<span></span>"
                    "</li>"))
      
      (log (pr-str p)
       
         )))
  	(.append ($ "ul.projects") "<li class='new'><a href='#'>add new</a></li>")
  )
	

;;; ui-update stuff

(defn workspace-updated [workspace]
  (log (pr-str workspace))
  (.text ($ :#debug) (pr-str workspace))
  
  (reset! latest-update (tick))
  
  (when (and intro-view 
             (not (nil? (:projects workspace)))
    (load-projects (:projects workspace))))
  
  (when(and project-view 
            (not (nil? (:current workspace))))
    ; todo:
)) 

;;;

(def error (atom false))

;;;; looping stuff ;;;;

(def repeat-handle (atom 0))



(defn refresh[& last-updated]
  (fm/remote (get-workspace (first last-updated)) [workspace] 
	(when-not 
      (nil? workspace)
	  (workspace-updated workspace)))
  
  )


(defn infinite-loop [ms func]
	(js/setInterval 
		(fn[] 
			(cond 
				(= true @error) (js/clearInterval @repeat-handle))
				:else (func)
			) ms))



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


(defn ui-init []
  
  (when project-view 
    (fm/remote (set-project project-hash) [project]
        (log project)
        (.text ($ "#viewport article h1") (:name project))
        (select-project-view "#miner")
        ))

)

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