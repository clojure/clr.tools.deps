(ns clojure.tools.deps.script.parse-test
  (:require
    [clojure.test :refer :all]
    [clojure.tools.deps.script.parse :as parse]))

(deftest test-parse-config
  (is (= {:paths ["x"]} (parse/parse-config "{:paths [\"x\"]}")))
  (is (= "deps.edn" (parse/parse-config "deps.edn"))))