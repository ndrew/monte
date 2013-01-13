(ns monte.core-test
  (:use clojure.test
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
      (is (= (parse-expression "entity")                    {:entity "entity"}))
      (is (= (parse-expression "entity.property")           {:entity "entity" :props "property"}))
      (is (= (parse-expression "entity{:filter}")           {:entity "entity" :filter ":filter"}))
      (is (= (parse-expression "(miner)")                   {:miner "miner"}))
      (is (= (parse-expression "(miner).property")          {:miner "miner" :props "property"}))
      (is (= (parse-expression "(miner).property{:filter :foo}")  {:miner "miner" :props "property" :filter ":filter :foo"}))
      ; parsing both parts
      (is (= (parse-entity "domain=entity")                         [:domain {:entity "entity"}]))
      (is (= (parse-entity "domain=entity.property")                [:domain {:entity "entity" :props "property"}]))
      (is (= (parse-entity "domain=entity{:filter}")                [:domain {:entity "entity" :filter ":filter"}]))
      (is (= (parse-entity "domain=(miner)")                        [:domain {:miner "miner"}]))
      (is (= (parse-entity "domain=(miner).property")               [:domain {:miner "miner" :props "property"}]))
      (is (= (parse-entity "domain=(miner).property{:filter :foo}") [:domain {:miner "miner" :props "property" :filter ":filter :foo"}]))
    )))
  
  
  

    
(def raw-data (atom {}))
  
(defmacro log[& body]
  `(println (str ~@body " at " (System/currentTimeMillis) )))


(defn get-async[key f]
  (when key
    (if-let [d (locking raw-data (get @raw-data key))] ; synchronized get does the trick
      @d
      (let [lock (future
                      ;(log (pr-str @raw-data))
                        (log "\t\thello from future " key)
                        (f))]
        (locking raw-data
          (reset! raw-data (conj @raw-data (hash-map key lock))) 
        )
        @lock
        ))))

  
(defn process-entity[[name cfg]] 
  (log "\tprocessing " name " " (pr-str cfg) )
  
  (let [key (keyword name)
        _miner-key (keyword (:miner cfg))
        _entity-key (keyword (:entity cfg))
        m (get-async _miner-key (fn[] (Thread/sleep 1000) :MINER))
        e (get-async _entity-key (fn[] :ENTITY))
        ]
    (log (str name "\t\tgot data " (pr-str m)))
    (hash-map key m)))
      
          
(def dummy-entities ;(:entities scheme))
  
  ;(comment
     ["t1=(code_miner)"
     "t2=(code_miner1)"
     "t3=(code_miner)"
     "t4=(code_miner1)"
     "t5=(code_miner)"
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
     ])
          

(log "start processing " (reduce #(str %1 "\n" %2) dummy-entities))

(def result (doall(pmap process-entity 
  (map parse-entity dummy-entities))))

(log "result is" (pr-str result))

(log (pr-str @raw-data))

;(log (pr-str @data1))
;(println (get @data1 :code_miner))

(log "done")


