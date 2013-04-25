(ns monte.miners-data-test
  (:use clojure.test)
  (:require 
    [criterium.core :as c]
    [clojure.core.reducers :as r]
    [monte.miners.data :refer :all]))


(def range-length 1000000)

(def ^:dynamic *test-performance* false)

(defn- fold-into-vec-performance [] 
  (println "\n * * * performance of PARALLEL folding to vector: * * *\n")
  
  (time
    (c/quick-bench
      (dotimes [i 10]
          (is (= range-length (count (fold-into-vec (take range-length (range)))))))))

    
  (println "\n * * * performance of STANDARD folding to vector: * * *\n")
  
  (time
    (c/quick-bench
      (dotimes [i 10]
          (is (= range-length (count (vec (take range-length (range))))))))))


(deftest fold-into-vec-test
  (is (= [0 1 2 3 4]
         (fold-into-vec (take 5 (range)))))

  (if *test-performance*
    (fold-into-vec-performance)))


(deftest merger-by-key-test
  (let [def-merger (merger-by-key :k)
        custom-merger (merger-by-key :k 
                                     (fn [item] 
                                       item))] 
    (is (fn? def-merger))
    (is (= {:testo [{}]} (def-merger {} {:k :testo})))
    (is (= {:testo [{}], :pesto [{}]} (def-merger {:testo [{}]} {:k :pesto})))
    (is (= {:testo [{}], :pesto [{},{}]} (def-merger {:testo [{}], :pesto [{}]} {:k :pesto})))

    
    (is (fn? custom-merger))
    (is (= {:testo [{:k :testo}]}                       (custom-merger {} {:k :testo})))
    (is (= {:testo [{:k :testo}], :pesto [{:k :pesto}]} (custom-merger {:testo [{:k :testo}]} {:k :pesto})))
    (is (= {:testo [{:k :testo}], :pesto [{:k :pesto},
                                          {:k :pesto}]} (custom-merger {:testo [{:k :testo}], :pesto [{:k :pesto}]} {:k :pesto})))))


(deftest agreggator-by-key-test
  (let [af (aggregator-by-key :k
                              +
                              )]
    (is (fn? af))
    
    (is (= {:k 10} (af {} {:k 10})))
    
    (is (= {:k 25} (af {:k 10} {:k 15})))
    
    
))