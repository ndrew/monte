(ns monte.views.index "Monte '/' page"
  (:require 
  	[monte.views.common :as common])
  (:use [noir.core :only [defpage]]))

(defpage "/" []
         (common/layout
         	[:header {:id "top"}
         		[:h3 "Monte"]
         		[:ul
         			[:li [:a {:href "#"}"піу!"]]]]
           [:p "Hello World!"]
           [:hr]
           [:div {:id "workspace"}]
           [:hr]
           [:section {:id "repo"}
              [:ul
                [:li 
                  [:input {:type "text" :class "dir-chooser"}] 
                  [:button "load"]]
              ]
            
           ]
           ;"<input type='file' webkitdirectory directory multiple mozdirectory onChange='console.log(this.files)'>"
           ))