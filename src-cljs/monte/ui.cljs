(ns monte.ui
  (:require 
    [fetch.remotes :as remotes]
	[jayq.core :as jq])
  (:use [jayq.util :only [log wait clj->js]]
	[jayq.core :only [$ append]])
  (:require-macros [fetch.macros :as fm]))

(defn ui-init []
  ; nothing here. yet
  )

(defn load-projects [projects]
  
  ;(log ($ "ul.projects"))
  
  (.empty ($ "ul.projects"))
  (doseq [p projects] 
    (do
      ; (log ($ :#projects))
      
      (.append ($ "ul.projects") 
               (str "<li>"
                    "<a href='/project/" (:name p) "'>" 
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
    
  (if (not (nil? (:projects workspace)))
    (load-projects (:projects workspace))
    )) 
  

;;;

(def error (atom false))

;;;; looping stuff ;;;;

(def repeat-handle (atom 0))

(defn tick[]
  (. (new js/Date) getTime))


(defn refresh[& last-updated]
  (fm/remote (get-workspace last-updated) [workspace] 
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


;;;;;;;;;;;;;;;;;;;
;; main

(defn list-projects[] 

  (jq/document-ready 
  (fn [] 
	;(init-directory-choosers)
	  (ui-init)
	  (refresh) ; full refresh
		
	  (let [poll-interval 2000]
	  (reset! repeat-handle 
	          (infinite-loop poll-interval 
                            (fn [] 
					            (refresh (tick))))))))
    
  )

(defn select-view [link-id] 
  (let [view-id (str link-id "_view")
        links ($ "#viewport article a")
        views ($ "#viewport div")]
    (log (str "view selected " view-id))
    
    (.hide views)
    (.attr links "href" "#")
	(.click links  
      (fn[e]
        (let [el (.-srcElement e)
              id (str "#" (.-id el))]
          (when-not (clojure.string/blank? (.-href (.-srcElement e)))
           	(select-view id)))))
    
    (.removeAttr ($ link-id) "href")
    (.toggle ($ view-id))))

(defn project-details [proj] 
  (jq/document-ready 
  (fn [] 
	;(init-directory-choosers)
	  (ui-init)

	  ;(log ($ :#viewport :article :a))

	  (select-view "#miner")

	  (refresh (tick)) ; full refresh
	  (let [poll-interval 2000]
	  (reset! repeat-handle 
	          (infinite-loop poll-interval 
                            (fn [] 
					            (refresh (tick))))))))
  )