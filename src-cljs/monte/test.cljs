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


(defn init-directory-choosers [] 
	(fm/remote (list-dirs) [dirs] 
			;(log root-path)	

		(log dirs)
		(. ($ ".dir-chooser") val (first dirs))
		; todo: on change

		(. ($ ".dir-chooser") autocomplete 
								(clj->js {:source ["test" "test2"]})
	)))



;; todo: <input type="file" id="file_input" webkitdirectory directory />