(ns monte.project-test
  "tests for project handling stuff" 
  (:use clojure.test
        monte.miners.impl
        monte.runtime
        [monte.logger :only [dbg err]])
  (:require [monte.miners.core :as miners]
            [monte.project :as p]
            [clojure.java.io :refer :all]
            [me.raynes.fs :as fs]))

; https://gist.github.com/samaaron/1398198
(defn mk-tmp-dir!
  "Creates a unique temporary directory on the filesystem. Typically in /tmp on
  *NIX systems. Returns a File object pointing to the new directory. Raises an
  exception if the directory couldn't be created after 10000 tries."
  []
  (let [base-dir (file (System/getProperty "java.io.tmpdir"))
        base-name (str (System/currentTimeMillis) "-" (long (rand 1000000000)) "-")
        tmp-base (str base-dir "/" base-name)
        max-attempts 100]
    (loop [num-attempts 1]
      (if (= num-attempts max-attempts)
        (throw (Exception. (str "Failed to create temporary directory after " max-attempts " attempts.")))
        (let [tmp-dir-name (str tmp-base num-attempts)
              tmp-dir (file tmp-dir-name)]
          (if (.mkdir tmp-dir)
            tmp-dir
            (recur (inc num-attempts))))))))


(deftest run-dummy-tests
  (binding [p/*project-dir* (mk-tmp-dir!)]
    (let [proj-cfg (p/get-project "test")]
      (is (= "test" (:name proj-cfg)))
      (println (pr-str proj-cfg))
      
      (fs/delete-dir p/*project-dir*))))