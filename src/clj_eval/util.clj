(ns clj-eval.util)

(def debug nil)

(defn log [& s]
  (when debug (apply println s)))
