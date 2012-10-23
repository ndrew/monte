(ns monte.views.index "Monte '/' page"
  (:require [monte.views.common :as common])
  (:use [noir.core :only [defpage]]))

(defpage "/" []
         (common/layout
           [:p "Hello World!"]))