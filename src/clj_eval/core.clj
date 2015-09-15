(ns clj-eval.core
  (:require [clj-eval.util :refer [log] :as u]))

(log "Cleanliness is next to Godliness")

(defn my-eval [env exp]
    (let [eval-list (fn [[sym & args :as exp]]
            (let [ vargs (vec args) _ (log "EVAL-LIST:" exp)]
                (condp = sym
                    'if (if (vargs 0) (vargs 1) (vargs 2))

                    'do (do (doall args) 
                            (last args))

                    'let (let [ [bindings & body] args _ (log "LET-binds-bod:" bindings body) ]
                            (my-eval (apply assoc {} bindings) (apply list 'do body)))

                    (if (fn? sym) (apply sym args)
                         exp))))
           ;;process only binding right-values for a let-form,
           ;;and regular my-eval otherwise
           special-eval (fn [env exp]
                          (if (coll? exp)
                            (let [ [op bindings & body] exp 
                                   _ (log "SPECIAL-op-binds-body" op bindings body)]
                              (if (= 'let op)
                                (let [res
                                 (->> (partition 2 bindings) 
                                      (map (fn [[bk bexp]]
                                             [bk (my-eval env bexp)]))
                                      (apply concat) 
                                      vec 
                                      list
                                      (#(concat % body))
                                      (cons 'let )) 
                                      _(log "SPECIAL-res:" res)] res)
                                (let [res2
                                 (my-eval env exp)
                                      _ (log "SPECIAL-res, inner else:" res2)] res2)
                                                   )))
                            (my-eval env exp)) ]
  (cond 
     (symbol? exp) (get env exp exp)
     (seq? exp) (do (log "SEQ **** env exp" env exp) (eval-list (map #(special-eval env %) exp)))
     (coll? exp) (do (log "COLL **** env exp" env exp) (into (empty exp) (map #(my-eval env %) exp)))
     :else exp)))

;; (my-eval {'x 1 'y 2} '(* x (+ (let [y 5] y) 3)))

