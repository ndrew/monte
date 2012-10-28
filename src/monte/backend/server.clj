(ns monte.backend.server
	"Monte noir server"
	(:use noir.core)
  	(:require [noir.server :as server]
  			  [noir.shoreleave.rpc :as rpc]))


;<?> figure how this works
;
;(rpc/activate-remotes!)
;(rpc/remote-ns 'monte.api :as "api")


(server/load-views "src/monte/views/")

(defn start [port]
	(server/start port))

(defn stop [instance]
	(server/stop instance))

(def handler (server/gen-handler {	:mode :dev
									:ns 'monte.views}))