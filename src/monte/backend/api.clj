(ns monte.backend.api
	"Monte Remote Call API"
	(:require [monte.runtime])
	(:use [noir.fetch.remotes :only [defremote]]))

(defremote get-workspace[& last-changed] 
	(do ; todo: diff
		(println "get-workspace called ")
		@monte.runtime/workspace))
; todo

;(defn load)