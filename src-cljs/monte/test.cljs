(ns monte.test
	(:require 
		[fetch.remotes :as remotes]
		[jayq.core :as jq])
	(:use [jayq.util :only [log wait clj->js]]
		  [jayq.core :only [$]])
  	(:require-macros [fetch.macros :as fm]))


(defn init-raphael []
	(def paper (js/Raphael 10 100 500 500))
	(def circle (. paper (circle 50 50 10)))
	(. circle (attr "fill" "#f00"))
)


(def error (atom false))
(def repeat-handle (atom 0))


(defn tick[]
	(. (new js/Date) getTime))

(defn refresh[last-updated]
	(log "tick")
	(fm/remote (get-workspace last-updated) [workspace] 
			
			(log workspace)
			(if-not (nil? workspace) 
				(do
					(.text ($ :#workspace) (pr-str workspace))
				)))
		;(def last-updated (. (new js/Date) getTime)
		;(log last-updated))
	) 


(defn infinite-loop [ms func]
	(js/setInterval 
		(fn[] 
			(cond 
				(= true @error) (js/clearInterval @repeat-handle))
				:else (func)
			) ms)
)


(defn init-directory-choosers [] 
	(fm/remote (list-dirs) [dirs] 
			;(log root-path)	

		(log dirs)
		(. ($ ".dir-chooser") val (first dirs))
		; todo: on change

		(. ($ ".dir-chooser") autocomplete 
								(clj->js {:source ["test" "test2"]})
	)))

(jq/document-ready 
	(fn [] 

		(init-directory-choosers)
		

		(let [poll-interval 5000]
		(log "hello")
		(reset! repeat-handle 
				(infinite-loop poll-interval (fn [] 
					(refresh (tick))))))
))


;; todo: <input type="file" id="file_input" webkitdirectory directory />