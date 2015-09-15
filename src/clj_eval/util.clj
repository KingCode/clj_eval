(ns clj-eval.util)

(def debug false)

(defn log [& s]
  (when debug (apply println s)))
