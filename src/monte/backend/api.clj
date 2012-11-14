(ns monte.backend.api
	"Monte Remote Call API"
	(:require [monte.runtime])
	(:use [noir.fetch.remotes :only [defremote]]))

(defremote get-workspace[& last-changed] 
	(do ; todo: diff
		(println "get-workspace " last-changed)
		
		(comment
			(cond
			(nil? last-changed) 
			:else 
				;@monte.runtime/workspace
				@monte.runtime/workspace
				(println "else")
				)
			)
		nil
	)
; todo

;(defn load)