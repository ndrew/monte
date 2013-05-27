(ns monte.utils
  "Monte convenience utils"
  (:require [cljs.reader :as reader]
            [jayq.core :as jq]
            [crate.core :as crate]
            [shoreleave.remotes.http-rpc :as rpc])
  (:use [jayq.util :only [log wait]]
        [jayq.core :only [$ append]]
        [crate.core :only [html]]))


(defn from-time [t] 
  (if (= 0 t)
    "never"
    (.fromNow (js/moment t))))
