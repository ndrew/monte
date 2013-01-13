(ns monte.backend.server
  "Monte server routines"
  (:use compojure.core
  		monte.views.index
  		monte.views.project
  		monte.backend.api
  	   [ring.adapter.jetty :only [run-jetty]])
  
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [cemerick.shoreleave.rpc :as rpc]))

 
(defroutes monte-routes
  (GET "/" [] (intro-view))
  (GET "/project/:hash" [hash] (project-view hash))
  (route/files "/" {:root "resources/public"})
  (route/not-found "<h1>Page not found</h1>"))
 
 
(def monte-routing (-> #'monte-routes
                       rpc/wrap-rpc
                       handler/site))
 
  
(defn start [port]
  (try  
  	(print "Starting server..")	
  	(run-jetty #'monte-routing {:port port})
  	(catch Exception e 
      (do
         (println "ERROR! Stacktrace:" )
         (.printStackTrace e)))))