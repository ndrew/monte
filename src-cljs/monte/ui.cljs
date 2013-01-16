(ns monte.ui  
  "Monte user interface"
  (:require-macros [shoreleave.remotes.macros :as fm])
  (:require [jayq.core :as jq]
            [shoreleave.remotes.http-rpc :as rpc])
  (:use [jayq.util :only [log wait clj->js]]
        [jayq.core :only [$ append]]
        [crate.core :only [html]]))


(def error (atom false)) ; todo: make use of it

; <!> HACK:
; 	these variables will be set on server while page will be generated
(def intro-view false)
(def project-view false)
(def project-hash 0) ; project-hash

(def latest-update (atom 0)) ; timestamp of latest update

(def dom-projects "ul.projects")
(def dom-miners "#miner_table tbody")
(def dom-vars "#var_table tbody")

(def lbl-add "add new")

(def miner-schemas (atom []))

(defn tick
  "return current timespamp"
  []
  (. (new js/Date) getTime))


(defn list-projects [projects]
  (.empty ($ dom-projects))
  (doseq [p projects] 
    (do 
      (.append ($ dom-projects) 
        (html [:li 
              [:a {:href (str "/project/" (:hash p))}
                  (:name p)]
              [:span "..."]])))) ; todo: add last modified time here
  
  	  (.append ($ dom-projects) 
             (html [:li {:class "new"} [:a {:href "#"} lbl-add]])))
	

(defn list-miners[miners]
  (.empty ($ dom-miners))
  (log (str "MINERS:" (pr-str miners)))
  (doseq [m miners]
    (let [[name id cfg] m]
      (.append ($ dom-miners) (html
        [:tr [:td name] ; todo: name editing
             [:td id]   ; todo: miner type displaying
              ;  dark magic to get unicode chars work — &#9660; todo: implement toggling
             [:td 
                [:a {:href "#"}
                   (.htmlToDocumentFragment goog.dom "&#9664;")]]]))
      (.append ($ dom-miners) (html
        [:tr {:style "display: none;"}
             [:td {:colspan "3"} 
              [:pre (pr-str cfg)]]]))))
  
  (.click ($ (str dom-miners " tr td:nth-child(3) a")) 
    (fn[e]
      (let [el ($ (.-srcElement e))
            details-tr (.next (.closest el "tr"))]
          (.toggle details-tr)
          ;(log (.htmlToDocumentFragment goog.dom "&#9660;"))
          (.text el (if (= (.text el) "\u25C0") "\u25bc" "\u25C0"))
          false)))

  ; todo: add miner functionality
  (.append ($ dom-miners) (html [:tr [:td {:class "new" :colspan "3"} [:a {:href "#"} lbl-add] ]])))


(defn list-vars[vars]
  (.empty ($ dom-vars))
  (log (str "VARS:" (pr-str vars)))
  (doseq [v vars]
    (let [[name id value] v]
      
      (.append ($ dom-vars) 
               (html
                 [:tr [:td name] ; todo: name editing
                      [:td id]   ; todo: miner type displaying
                      ; todo: implement toggling
                      [:td value] 
                    ]))
      
      ))
  
  ; todo: add var functionality
  (.append ($ dom-vars) (html [:tr [:td {:class "new" :colspan "3"} [:a {:href "#"} lbl-add] ]])))

;; vis stuff
(def graph (atom nil))

(def vis-data-entities ["fffuuu!" "barrrr" "bazzzzz" "ffffuuuuzzzz"])
(def vis-data-connections [])


(defn js-map
  "makes a javascript map from a clojure one"
  [cljmap]
  (let [out (js-obj)]
    (doall (map #(aset out (name (first %)) (second %)) cljmap))
    out))



(defn render-dummy[r n] 
  (let [[x y] (.-point n)
        id (. n -id)]
          (.push (.set r) (.text r x y id))))



(defn get-graph[] 
  (if-not @graph 
    (do
      (reset! graph (js/Graph.))
      ; todo: 
      (doseq [a vis-data-entities]
        (.addNode @graph a
                        (js-map{:render render-dummy})))
      @graph
    )
    @graph
    ))

(def wnd-height (atom 400))
(def wnd-width (atom 400))

(def renderer (atom nil))

(defn redraw-vis[]
  (let [layouter (js/Graph.Layout.Spring. (get-graph))]
      
        ;(js/Graph.Renderer.Raphael. "canvas" (get-graph) 100 100)
      
      
      (if-not @renderer
        (reset! renderer (js/Graph.Renderer.Raphael. "canvas" (get-graph) @wnd-width @wnd-height)))
         
      (.layout layouter)
      (.draw @renderer)
      
    ))


(defn select-project-view [link-id] 
  (let [view-id (str link-id "_view")
        links ($ "#viewport article a")
        views ($ "#viewport article .view")]
    (log (str "view selected " view-id))
    
    (.attr links "href" "#")
    (.click links  
      (fn[e]
        (let [el (.-srcElement e)
              id (str "#" (.-id el))]
          (when-not (clojure.string/blank? (.-href (.-srcElement e)))
            (select-project-view id)))))

    (.hide views)

    (.removeAttr ($ link-id) "href")
    (.toggle ($ view-id))
    
    (when (= "#visualization_view" (str view-id))
        ;(.empty ($ "#canvas"));
        (reset! wnd-width  (- (.-innerWidth js/window) 250))
        (reset! wnd-height (- (.-innerHeight js/window) 250))
        
        ; todo: set miners
        (redraw-vis)
         
      )
    ))



(defn set-project [proj]
  (select-project-view "#miner"))

(defn- status[& body] ; for testing purposes
  (.text ($ ".status") (reduce str body)))

       
(defn workspace-updated [workspace]
  (log "workspace updated")
  (log (pr-str workspace))
  (.text ($ :#debug) (pr-str workspace))
  
  (reset! latest-update (tick))
  
  (when (and intro-view 
             (not (nil? (:projects workspace)))
    (list-projects (:projects workspace))))
  
  (when project-view
    (when-not (nil? (:miners workspace))
      (reset! miner-schemas (:miners workspace))) ; store all miners              
    (when-not (nil? (:current workspace))        
      (let [proj (:current workspace)]
        (.text ($ "#viewport article h1") (:name proj))
        (list-vars (:vars proj))
        (list-miners (:miners proj)))  
      
        (status "Loaded project")
        (.click ($ "#redraw") 
                (fn[e]
                  (log "redrawing")
                  (redraw-vis)
                ))
    )
  )
) 


;;;; looping stuff ;;;;
(def repeat-handle (atom 0))

(defn refresh[& last-updated]
  (fm/rpc (get-workspace (first last-updated)) [workspace] 
	(when-not 
      (nil? workspace)
	  (workspace-updated workspace))))


(defn infinite-loop [ms func]
	(js/setInterval 
		(fn[] 
			(cond 
				(= true @error) (js/clearInterval @repeat-handle))
				:else (func)
			) ms))


(defn ui-init []
  (when project-view 
    (fm/rpc (set-project project-hash) [proj]
        (set-project proj))))

;;;;;;;;;;;;;;;;;;;
;; main
(jq/document-ready 
  (fn [] 
      (ui-init)
	  (refresh) ; full refresh
	  (let [poll-interval 2000]
	  (reset! repeat-handle 
	          (infinite-loop poll-interval 
                            (fn [] 
					            (refresh @latest-update)))))))