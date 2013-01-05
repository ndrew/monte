(ns monte.runtime)

(def last-updated (atom (System/currentTimeMillis)))

(def current-project (atom {}))

(def workspace 
      (atom 
        {:current {}}))

(add-watch workspace 
	:watch-change(fn [key _workspace old-val new-val] 
                     (reset! last-updated
                       (System/currentTimeMillis))
                     (println (str "workspace changed: key= " key "; old=" old-val "; new=" new-val))))

(defn set-project [project]
  (reset! current-project {:name project})
  (println @current-project)
  )

(defn workspace-diff [& timestamp]
  ; tbd
  @workspace)


(defn list-projects []
  [{:name "Monte"}
   {:name "MyFolder"}
   {:name "Metropolis"}
   {:name "KMC Booking"}
   {:name "Diploma"}
   ])


(defn full-refresh []
  ;(println "full-refresh")
  {:projects (list-projects)
   :workspace @workspace}) 


(defn partial-refresh [timestamp]
  (cond (empty? @current-project)
   	    nil
  :else (workspace-diff timestamp))
)