(ns monte.backend.api
	"Monte Remote Call API"
	(:use [noir.fetch.remotes :only [defremote]]))

(defremote foo[] 
	(do 
		(println "FFFUUUU!")
		"bar"))