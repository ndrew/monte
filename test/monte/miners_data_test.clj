(ns monte.miners-data-test
  (:use clojure.test)
  (:require 
    [criterium.core :as c]
    [clojure.core.reducers :as r]
    [monte.miners.data :refer :all]))


(def range-length 1000000)

(deftest fold-into-vec-test
  
  (is (= [0 1 2 3 4]
         (fold-into-vec (take 5 (range)))))

  (println "\n * * * performance of PARALLEL folding to vector: * * *\n")
  
  (time
    (c/quick-bench
      (dotimes [i 10]
          (is (= range-length (count (fold-into-vec (take range-length (range)))))))))

    
  (println "\n * * * performance of STANDARD folding to vector: * * *\n")
  
  (time
    (c/quick-bench
      (dotimes [i 10]
          (is (= range-length (count (vec (take range-length (range)))))))))
)

