(ns monte.runtime)

(def workspace (atom {}))
(def last-changed (atom (System/currentTimeMillis)))

(add-watch workspace 
	:watch-change (fn [key _workspace old-val new-val] 
                       		(reset! last-changed (System/currentTimeMillis))
                       		(println (str "workspace changed: key= " key "; old=" old-val "; new=" new-val))))