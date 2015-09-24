(ns clj-eval.util)

(def debug nil)

#_(defn log [& s]
  (when debug (apply println s)))

(defmacro log [& s]
  (when debug `(println ~@s)))
