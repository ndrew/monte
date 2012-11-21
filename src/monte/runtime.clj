(ns monte.runtime)

(def workspace (atom {}))
(def last-updated (atom (System/currentTimeMillis)))

(add-watch workspace 
	:watch-change (fn [key _workspace old-val new-val] 
                       		(reset! last-updated
                       	 (System/currentTimeMillis))
                       		(println (str "workspace changed: key= " key "; old=" old-val "; new=" new-val))))

(defn workspace-diff [& timestamp]
	; todo: add
	@workspace
)