(ns monte.ui  
  "Monte user interface"
  (:require-macros [shoreleave.remotes.macros :as fm])
  (:require [jayq.core :as jq]
            [shoreleave.remotes.http-rpc :as rpc])
  (:use [jayq.util :only [log wait]]
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

;; vis stuff
(def graph (atom nil))

(def vis-data-entities ["fffuuu!" "barrrr" "bazzzzz" "ffffuuuuzzzz"]) ;todo

(def vis-data-connections [])

(def wnd-height (atom 0))

(def wnd-width (atom 0))

(def renderer (atom nil))

(def layouter (atom nil))

(def repeat-handle (atom 0))

(def fetch-interval 1500)

(def proj (atom {}))

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


;;;;;;;;;;;;;;;;;
; data handling


(defn update-vis-bounds[]
  (let [legend-width (.width ($ "#legend"))
        w-margin 45
        canvas-top (.-top (.offset ($ "#canvas")))
        h-margin 15]
    (reset! wnd-width  (- (.-innerWidth js/window) (+ legend-width w-margin)))
    (reset! wnd-height (- (.-innerHeight js/window) (+ canvas-top h-margin)))))


(defn get-graph[] 
  (if-not @graph 
    (do
      (reset! graph (js/Graph.))
      ; todo: 
      ;(doseq [a vis-data-entities]
      ;  (.addNode @graph a
      ;                  (js-map{:render render-dummy})))
      @graph
    )
    @graph
    ))


(defn infinite-loop [ms func]
  (js/setInterval 
    (fn[] 
      (cond 
        (= true @error) (js/clearInterval @repeat-handle))
        :else (func)
      ) ms))


(defn tick []
  "return current timespamp"
  (. (new js/Date) getTime))


(defn- status[& body]
  "nice status string"
  (log (reduce str (first body) (rest body)))
  (.text ($ ".status") (reduce str (first body) (rest body))))



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



(defn redraw-vis[]
  (update-vis-bounds)
  (if-not @renderer
    (reset! renderer (js/Graph.Renderer.Raphael. "canvas" (get-graph) @wnd-width @wnd-height)))
   
  (aset @renderer "width" @wnd-width)
  (aset @renderer "height" @wnd-height)
  (.height ($ "#canvas") @wnd-height)
  (.width ($ "#canvas") @wnd-width)
      
  (if-not @layouter 
    (reset! layouter (js/Graph.Layout.Spring. (get-graph))))
                   
  (.layout @layouter)
  (.draw @renderer))


(defn select-project-view [link-id] 
  (let [view-id (str link-id "_view")
        links ($ "#viewport article nav a")
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
        (redraw-vis))))





;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; ui handlers for
   
(defn update-project-ui [project]
  "does ui update on project selected"
  (select-project-view "#miner")
  (.text ($ "#viewport article h1") (:name project))
  (list-vars (:vars project))
  (list-miners (:miners project))      
  (status "Loaded project " (:name project)))
   
   
(defn update-workspace-ui [workspace]
  "does ui update on workspace changed"

  (log "workspace updated")
  (log (pr-str workspace))
  (.text ($ :#debug) (pr-str workspace)) 
  (when (and intro-view 
             (not (nil? (:projects workspace)))
    (list-projects (:projects workspace))))
  
  (when project-view
    (when-not (nil? (:miners workspace))
      (reset! miner-schemas (:miners workspace))) ; store all miners 
                 
    (when-not (nil? (:current workspace))        
      (let [proj (:current workspace)]
        (update-project-ui proj))))) 



(defn ui-init []
  "does the ui initialization"
  
  (.resize ($ js/window) 
  (fn[e]
      (update-vis-bounds)
      (if @renderer
       (do 
         ; todo use setInterval for smoother update
          (redraw-vis))))))

;;;;;;;;;;;;;;;;;;;;;;;
; ajax calls' handlers


(defn set-project [project-id]
  "notifies backend that project with id=project-id had been selected"
  (fm/rpc (set-project project-id) [p]
    (update-project-ui p)
    (reset! proj p)
    (reset! latest-update (tick))))


(defn refresh[& last-updated]
  "pings backend for changes"
  (fm/rpc (get-workspace (first last-updated)) [workspace] 
  
  (when-not (nil? workspace)
    (update-workspace-ui workspace)
    (when-not (:current workspace)
      (reset! proj {}))
    
    (when (:data workspace)
      (status "received miner-data")
      
      ;(reset! graph nil)
      
      (get-graph) ; load graph
      
      (doseq [a (:data workspace)]
        
          (let [k (first (keys a))
                data (:data (get a k))]
       
          (log (str "processing " (pr-str k))) ; add to legend + create a render func
          ; (log (pr-str data))
        
          (doseq [d data]            
            (.addNode @graph (:id d)
                       ;(js-map{:render render-dummy})
                       (js-map {})
                       ))))
      (redraw-vis)
    
    (reset! latest-update (tick))))))


(defn run-miners[]
  "launches data mining in backend"
  (fm/rpc (run-miners (:hash @proj)) []))


(defn load-data[]
  "requests backend for data"
  (if project-view 
      (set-project project-hash)
      (refresh)))



;;;;;;;;;;;;;;;;;;;
;; main
(jq/document-ready 
  (fn [] 
    (ui-init)
    (.click ($ "#redraw") (fn[e] (redraw-vis)))
    (.click ($ "#run_miners") (fn[e] (run-miners)))

    (load-data)
    (reset! repeat-handle 
	          (infinite-loop fetch-interval 
              (fn [] 
					      (refresh @latest-update))))))