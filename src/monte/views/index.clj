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
           ))