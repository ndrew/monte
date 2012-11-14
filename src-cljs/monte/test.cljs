(ns monte.test
	(:require 
		[fetch.remotes :as remotes]
		[jayq.core :as jq])
  	(:require-macros [fetch.macros :as fm]))


(defn init-raphael []
	(def paper (js/Raphael 10 100 500 500))
	(def circle (. paper (circle 50 50 10)))
	(. circle (attr "fill" "#f00"))
)

(jq/document-ready 
	(fn [] 
		(fm/remote (get-workspace) [super-repo] 
			(js/alert super-repo))

		;(js/alert "JQuery")
		;(fm/remote (foo) [result] (js/alert result))
))