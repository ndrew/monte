(ns monte.core-test
  (:use clojure.test
        monte.core))

(defmacro with-private-fns [[ns fns] & tests]
  "Refers private fns from ns and runs tests in context."
  `(let ~(reduce #(conj %1 %2 `(ns-resolve '~ns '~%2)) [] fns)
     ~@tests))


(with-private-fns [monte.core [extract-filters]]
  (deftest parsing-entity-dsl
  	(testing " parsing of dsl notation."
      (is (= (extract-filters "no-filter") nil))
      (is (= (extract-filters "(dummy_miner).task{:filter :all}") {:filter ":filter :all"}))
    )
  
  ))
  
