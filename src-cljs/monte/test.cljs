(ns monte.test
	(:require 
		[fetch.remotes :as remotes]
		[jayq.core :as jq]
		[goog.dom :as dom]
	    [goog.object :as goog-object]
	    [goog.events.Event :as goog-event]
	   	[goog.events.EventType :as goog-event-type]
	   	[goog.ui.ColorButton :as color-button]
	   	[goog.ui.Tab :as gtab]
	   	[goog.ui.TabBar :as gtabb])
  	(:require-macros [fetch.macros :as fm]
  		))

;	(:require [fetch.remotes :as remotes]
;            [crate.core :as crate]
;            [jayq.core :as jq])
;	(:use-macros [crate.macros :only [defpartial]])
;  	(:require-macros [fetch.macros :as fm])

(def tabbar (goog.ui.TabBar.))


(jq/document-ready (fn [] 
	;(js/alert tabbar)
	;(.decorate tabbar (.getElement goog.dom "top"))

	(def paper (js/Raphael 10 100 500 500))
	(def circle (. paper (circle 50 50 10)))
	(. circle (attr "fill" "#f00"))

	;(js/alert "JQuery")
	;(fm/remote (foo) [result] (js/alert result))
))



; (js/alert js/document) 





; (fm/remote (foo) [result]
; (js/alert result))