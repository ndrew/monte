(ns monte.core-test
  (:use clojure.test
        clojure.pprint
        [clojure.string :only [split]]
        monte.core))

(defmacro with-private-fns [[ns fns] & tests]
  "Refers private fns from ns and runs tests in context."
  `(let ~(reduce #(conj %1 %2 `(ns-resolve '~ns '~%2)) [] fns)
     ~@tests))


(with-private-fns [monte.core [extract-filters extract-miners extract-props extract-entities]]
  (deftest parsing-entity-dsl-stuff
    (testing " parsing of dsl notation."

      ; filters     
      (is (= (extract-filters "entity")             nil))
      (is (= (extract-filters "entity.property")        nil))
      (is (= (extract-filters "entity{:filter}")        {:filter ":filter"}))
      (is (= (extract-filters "(miner)")            nil))
      (is (= (extract-filters "(miner).property")         nil))
      (is (= (extract-filters "(miner).property{:filter :foo}") {:filter ":filter :foo"}))
      ; miners
      (is (= (extract-miners "entity")              nil))
      (is (= (extract-miners "entity.property")     nil))
      (is (= (extract-miners "entity{:filter}")     nil))
      (is (= (extract-miners "(miner)")             {:miner "miner"}))
      (is (= (extract-miners "(miner).property")    {:miner "miner"}))
      (is (= (extract-miners "(miner).property{:filter :foo}")  {:miner "miner"}))
      
      
      ; unification
      (is (= (extract-miners "(unify)")                nil))
      (is (= (extract-miners "(unify tasks.asignee classes.javadoc.author commits.author)")         
                      {:unify "tasks.asignee classes.javadoc.author commits.author"}))
      ; properties
      (is (= (extract-props "entity")                 nil))
      (is (= (extract-props "entity{:filter}")        nil))
      (is (= (extract-props "(miner)")                nil))
      (is (= (extract-props "(miner){:filter}")       nil))

      (is (= (extract-props "(miner).property")             {:props "property"}))
      (is (= (extract-props "(miner).property{:filter}")    {:props "property"}))
      (is (= (extract-props "entity.property")              {:props "property"}))
      (is (= (extract-props "entity.property{:filter}")     {:props "property"}))

      (is (= (extract-props "(miner).nested.property")          {:props "nested.property"}))
      (is (= (extract-props "(miner).nested.property{:filter}") {:props "nested.property"}))
      (is (= (extract-props "entity.nested.property")           {:props "nested.property"}))
      (is (= (extract-props "entity.nested.property{:filter}")  {:props "nested.property"}))
    ; entities      
      (is (= (extract-entities "entity")                  {:entity "entity"}))
      (is (= (extract-entities "entity.property")         {:entity "entity"}))
      (is (= (extract-entities "entity{:filter}")         {:entity "entity"}))
      (is (= (extract-entities "(miner)")                         nil))
      (is (= (extract-entities "(miner).property")                nil))
      (is (= (extract-entities "(miner).property{:filter :foo}")  nil))
      ; parsing right parts
      (is (= (parse-expression "entity")                    [{:entity "entity"}]))
      (is (= (parse-expression "entity.property")           [{:entity "entity"} {:props "property"}]))
      (is (= (parse-expression "entity{:filter}")           [{:entity "entity" :filter ":filter"}]))
      (is (= (parse-expression "(miner)")                   [{:miner "miner"}]))
      (is (= (parse-expression "(miner).property")          [{:miner "miner"} {:props "property"}]))
      (is (= (parse-expression "(miner).property{:filter :foo}")  [{:miner "miner"} {:props "property" :filter ":filter :foo"}]))
      ; parsing both parts
      (is (= (parse-entity "domain=entity")                         [:domain [{:entity "entity"}]]))
      (is (= (parse-entity "domain=entity.property")                [:domain [{:entity "entity"} {:props "property"}]]))
      (is (= (parse-entity "domain=entity{:filter}")                [:domain [{:entity "entity" :filter ":filter"}]]))
      (is (= (parse-entity "domain=(miner)")                        [:domain [{:miner "miner"}]]))
      (is (= (parse-entity "domain=(miner).property")               [:domain [{:miner "miner"} {:props "property"}]]))
      (is (= (parse-entity "domain=(miner).property{:filter :foo}") [:domain [{:miner "miner"} {:props "property" :filter ":filter :foo"}]]))
    )))
  
  
(deftest accessor-test 

  (is (= (access-property {} "key") nil))
  (is (= (access-property {:key :foo} "key") :foo))
  (is (= (access-property [{:key :bar} {:key :baz}] "key") [:bar :baz]))
  (is (= (access-property [{:nested {:key :foo}} {:nested :baz}] "nested.key") [:foo nil])))


(def dummy-entities ;(:entities scheme))
  
     [
     "t1=t28.testo"
     "t2=(code_miner1)"
     "t3=(code_miner)"
     "t4=(code_miner1)"
     "t5=t14.testo"
     "t6=(code_miner1)"
     "t7=(code_miner)"
     "t8=(code_miner)"
     "t9=(code_miner)"
     "t10=(code_miner)"
     "t11=(code_miner)"
     "t12=(code_miner2)"
     "t13=(code_miner3)"
     "t14=(code_miner4)"
     "t15=(code_miner)"
     "t21=(code_miner7)"
     "t22=(code_miner1)"
     "t23=(code_miner)"
     "t24=(code_miner1)"
     "t25=t15.testo"
     "t26=(code_miner1)"
     "t27=(code_miner)"
     "t28=(code_miner)"
     "t29=(code_miner)"
     
     ])
          

(log "start processing " (reduce #(str %1 "\n" %2) dummy-entities))

; todo: validation?

;(comment ; uncomment me!
(def result (doall(pmap process-entity 
  (map parse-entity dummy-entities))))

(log "result is" (binding [*print-right-margin* 7] (pprint result)))
;)

(log "done")


