(ns monte.logger-test
  "tests for logger"
  (:use clojure.test
        clansi.core
        [monte.logger :only [dbg err]]))

(alter-var-root #'monte.logger/*debug* (constantly true))


(comment

(deftest dbg-test
  "tests if dbg logs messages to console"
  (let [output (with-out-str
          (dbg "1st level")
          (dbg "1st level" (map #(dbg %) ["2nd level" "2nd level"])))]
    (is (.startsWith output "DBG:"))))

(deftest debug-flag-test
  "test that dbg supresses debug if *debug* set to false"
  (binding [monte.logger/*debug* false]
    (let [output (with-out-str (dbg "test"))]
      (is (= "" output)))))


(deftest err-test
  "test monte.loger/err func"
  (let [output (with-out-str
          (err "ERROR!")
          (err (Exception. "Exception test")))]
    (is (.startsWith output "ERR: "))))
)

;(deftest color
;  (alter-var-root #'clansi.core/*use-ansi* (constantly false))
;    (style-test-page)
;)
