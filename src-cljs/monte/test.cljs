(ns monte.test
	(:require 
		[fetch.remotes :as remotes]
		[jayq.core :as jq])
	(:use [jayq.util :only [log wait]])
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
	(fm/remote (get-workspace last-updated) [super-repo] 
			(log super-repo))
		;(def last-updated (. (new js/Date) getTime)
		;(log last-updated))
	) 



(defn repeat [ms func]
	(js/setInterval 
		(fn[] 
			(cond 
				(= true @error) (js/clearInterval @repeat-handle))
				:else (func)
			) ms)
)

(jq/document-ready 
	(fn [] 
		(log "hello")
		(reset! repeat-handle 
				(repeat 1000 (fn [] 
					(refresh (tick))
				)))
		;(js/alert @repeat-handle)
		))