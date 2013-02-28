(ns monte.views.ui-tests "Monte '/settings' page"
  (:require 
    [monte.views.common :as common]
    [monte.runtime :as runtime])
  (:use 
    [hiccup.element :only [javascript-tag]]))

(defn ui-tests-view[]
  (common/layout nil
    [:h1 "Here'll be dragons"]
    )
  )