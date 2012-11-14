(ns monte.backend.api
	"Monte Remote Call API"
	(:use [noir.fetch.remotes :only [defremote]]))

(defremote get-workspace[& last-changed] 
	(do 
		(println "FFFUUUU!")
		"bar"))
; todo

;(defn load)