(ns slopegraph.main
  (:require [strokes :refer [d3]]))

;; simple bijection visualization

(def X (sorted-map        
  "KEY1" "value1"
  "KEY2" "value2"
  "KEY2.SUBKEY1" "subvalue1"))


(def Y (sorted-map        
  "NKEY1" "value1"
  "NKEY2" "value2"
  "NKEY2.SUBKEY1" "subvalue1"))


(def X-Y-adjucency
  [["KEY1" "NKEY1"]
   ["KEY2" "NKEY2.SUBKEY1"]])

; temporary
(strokes/bootstrap)

(def m [50 180 50 40]) ; margins top left bottom right

(def w (- 960 (m 1) (m 3)))
(def h (- 500  (m 0) (m 2)))


; todo: proper x and y funcs

(defn linear-scale[v] 
  (-> d3 .-scale 
      (.linear)
      (.domain [0 (count v) ])
        (.range [(m 0) h])))


(def svg (-> d3 (.select "#slopegraph") (.append "svg")
    (.attr {:width  (+ w (m 1) (m 3))
            :height (+ h (m 0) (m 2))})
  (.append "g")
    (.attr {:transform (str "translate(" (m 3) "," (m 0) ")")})))


; text

(def header-X (-> svg 
                (.append "svg:text")
                  (.attr {:x (m 1)
                          :y 0
                          :text-anchor "end"
                          :opacity .5})
                  (.text "X")))

(def header-Y (-> svg 
                (.append "svg:text")
                  (.attr {:x (- w (m 3))
                          :y 0
                          :text-anchor "start"
                          :opacity .5})
                  (.text "Y")))



;(.log js/console (pr-str (vec (take (count X) (range)))))
;(.log js/console (pr-str y-X))



(let [x-ids (keys X)
      y-ids (keys Y)
      ya (linear-scale x-ids)
      yb (linear-scale y-ids)
      X-indexes (reduce merge (map-indexed #(hash-map %2 %1 ) x-ids))
      Y-indexes (reduce merge (map-indexed #(hash-map %2 %1 ) y-ids))]

  (-> svg 
    (.selectAll "g.X")
    (.data (vec x-ids))
    (.enter)
    (.append "svg:text")
    (.attr {
            :x (m 1)
            :y #(ya (X-indexes %))
            :dy ".35em"
            :font-size 10
            :text-anchor "end"
            })
    (.text 
      #(str % "(" (X %) ")")))
  
  (-> svg 
    (.selectAll "g.Y")
    (.data (vec y-ids))
    (.enter)
    (.append "svg:text")
    (.attr {
            :x (- w (m 3))
            :y #(ya (Y-indexes %))
            :dy ".35em"
            :font-size 10
            :text-anchor "left"
            })
    (.text 
      #(str % "(" (Y %) ")")))

  
  (-> svg 
    (.selectAll ".slopes")
    (.data X-Y-adjucency)
    (.enter)
    (.append "svg:line")
    (.attr {
            :x1 (+ (m 1) 15)
            :x2 (- w (m 3) 15)
            :y1 (fn[k] 
                  ;(.log js/console (pr-str (first k)))
                  (ya (X-indexes (first k))))
            :y2 (fn[k] 
                  ;(.log js/console (pr-str (second k)))
                  (yb (Y-indexes (second k))))
            :opacity .6
            :stroke "black"
            })))


