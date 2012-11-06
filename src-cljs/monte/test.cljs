(ns monte.test
	(:require [fetch.remotes :as remotes])
  	(:require-macros [fetch.macros :as fm]))

;	(:require [fetch.remotes :as remotes]
;            [crate.core :as crate]
;            [jayq.core :as jq])
;	(:use-macros [crate.macros :only [defpartial]])
;  	(:require-macros [fetch.macros :as fm])


(js/alert "Hello world!") 

(fm/remote (foo) [result]
	(js/alert result))