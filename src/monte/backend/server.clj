(ns monte.backend.server
  "Monte server routines"
  (:use compojure.core
  		  [monte.views.common :as common]
        [monte.logger :only [dbg err]]
        [monte.runtime :only [get-runtime-data]]
  	    [ring.adapter.jetty :only [run-jetty]]
        sandbar.stateful-session
  )
  (:require [monte.views.index :as view]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [cemerick.shoreleave.rpc :as rpc]))


(defn data-for-ui [runtime]
  "filters runtime"
  ; tbd
  @runtime)


(defn update-session[data]
  (when-not (session-get :runtime)
    ; init runtime
    (session-put! :user :default)
    (session-put! :runtime (atom (get-runtime-data))))    

  (doseq [kv data]
    (session-put! (key kv) (val kv))))


(defn index-page-handler[] 
  (update-session {:current-view :index-page})

  (let [title "Monte"
        view-id  (session-get :current-view :index-page)
        init-cfg (data-for-ui (session-get :runtime))]
  
    (common/gen-html view-id title init-cfg)))
 
 
(defn project-page-handler[hash]
  (update-session {:current-view :project-page
                   :hash hash})
  
  (let [data (data-for-ui (session-get :runtime))
        title (:name data "Project")
        view-id  (session-get :current-view :init-cfg-page)
        ; filter data for 
        init-cfg (:projects data)]
    
    (println (pr-str 
               
               (filter #(do
                          (println (:hash %))
                          
                          true) init-cfg)
               
               ))
    (common/gen-html view-id title init-cfg)))
  
 
(defn not-found-page-handler[]
  (let [title "404!"
        view-id  :error-page
        init-cfg {:error 404
                  :details "I guess it is not the page you are looking for."}]
    (common/gen-html view-id title init-cfg))) 
 
 
(defn settings-page-handler[]
  (let [title "Monte settings"
        view-id  :settings-page
        init-cfg {:foo :bar}
        ; tbd
        ]
    (common/gen-html view-id title init-cfg))) 
  
 
 
(defroutes monte-routes
  ; page routes
  (GET "/"              []     (index-page-handler))
  (GET "/project/:hash" [hash] (project-page-handler hash))
  (GET "/settings/"     []     (settings-page-handler))
  
  
  ; api routes 
  (GET "/api" [] (do 
                   (dbg "TEST<br>")
                   "API"))
  
  (route/resources "/" {:root "public"})
  (route/not-found (not-found-page-handler)))
 
 
(def monte-routing (-> #'monte-routes
                       wrap-stateful-session
                       rpc/wrap-rpc
                       handler/site))
 
  
(defn start [port]
  (try  
  	(dbg "Starting server..")	
  	(run-jetty #'monte-routing {:port port})
  	(catch Exception e 
      (err e))))