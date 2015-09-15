(ns clj-eval.core
  (:require [clj-eval.util :refer [log] :as u]))

(defn my-eval [env exp]
    (letfn [(eval-list [env [_ & args :as exp]]
            (let [ [op vargs :as e-exp] (->> (map #(my-eval env %) exp) 
                                       ((fn [v] [(first v), (vec (rest v))]))) 
                   _ (log "EVAL-LIST-exp-op-vargs:" exp op vargs)]
                (condp = op
                    'if (if (vargs 0) (vargs 1) (vargs 2))

                    'do (do (doall vargs) 
                            (last vargs))

                    'let (let [ [bindings & body] args _ (log "LET-binds-bod:" bindings body) ]
                            (my-eval (apply assoc {} 
                                            (let-resolve env bindings)) 
                                     (apply list 'do body)))

                    (if (fn? op) (apply op vargs)
                         (cons op vargs)))))

            (let-resolve [env lbexps]
                    (let [ _ (log "LETRESOLVE-bindings" lbexps)
                           res
                            (->> (partition 2 lbexps) 
                                (map (fn [[bk bexp]]
                                        [bk (my-eval env bexp)]))
                                (apply concat) 
                                vec) 
                                _ (log "LETRESOLVE-res:" res)] res)) ]
  (cond 
     (symbol? exp) (get env exp exp)
     (seq? exp) (do (log "SEQ **** env exp" env exp) (eval-list env exp))
     (coll? exp) (do (log "COLL **** env exp" env exp) (into (empty exp) (map #(my-eval env %) exp)))
     :else exp)))

;; (my-eval {'x 1 'y 2} '(* x (+ (let [y 5] y) 3)))

