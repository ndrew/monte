(ns miners-exec-viz.core
  (:use-macros
    [dommy.macros :only [sel sel1]])
  (:require
    [dommy.utils :as utils]
    [dommy.core :as dommy]))

(defn log[x]
  (.log js/console (pr-str x)))

(def W 18)
(def H 10)

(defn- gen-matrix[w h]
  (map (fn[x] 
         (range 1 (inc w))) (range 1 (inc h))))

(defn- to-pix[i]
  (str i "px"))

(defn- tile-indent[i]
  (* i (- 50 25)))


(defn- row-indent[i]
  {:style 
   {:margin-left (to-pix (tile-indent i))}})

(defn pos-style
  "calculates the css top and left coords according to x y"
  [x y]
  {:left (to-pix 
           (+ 
             (* (dec x) 51) ; width of a tile
             (* 25 (dec y)) ; skew width
             12))            ; padding
   
   :top (to-pix (+ 
                  (- (* W 23)
                     (* (dec y) 36))
                   7)) 
   })

(defn- box[x y]
  [:.block 
   {:style 
    (merge {:position "absolute"}
           (pos-style x y))}
   (str x " " y)])

(defn- mascot[x y]
[:.mascot 
   {:style 
    (merge {:position "absolute"}
           (pos-style x y))}
   (str x " " y)])


(def grid (reverse (map-indexed 
            (fn [i x] (into [:ul (row-indent i)]
                            (map (fn[x] [:li.block (str (+ x (* i 10))) ]) x)))
            (gen-matrix W H))))

(dommy/append! (sel1 :#bg) grid)

(dommy/append! (sel1 :#scene) (box 1 1))
(dommy/append! (sel1 :#scene) (box 2 2))
(dommy/append! (sel1 :#scene) (box 9 9))
(dommy/append! (sel1 :#scene) (box 17 9))

(dommy/append! (sel1 :#scene) (mascot 10 5))


