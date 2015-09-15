(ns clj-eval.core-test
  (:require [clojure.test :refer :all]
            [clj-eval.core :refer :all]))

(deftest nil-test
  (testing "nil evals to nil"
    (is (= nil (my-eval {} nil)))))


(deftest numbers-self-test
  (testing "numbers eval to themselves"
    (is (= 1 (my-eval {} 1)))
    (is (= 2 (my-eval {} 2)))))


(deftest strings-self-test
  (testing "strings eval to themselves"
    (is (= "hello" (my-eval {} "hello")))))


(deftest symbols-env-test
  (testing "symbols eval to their value in the environment"
    (is (= 10 (my-eval {'x 10} 'x)))
    (is (= "yo!" (my-eval {'x "yo!"} 'x)))
    (is (= nil (my-eval {'x nil} 'x)))))

(deftest functions-self-test 
  (testing "functions evaluate to themselves"
    (is (= + (my-eval {} +)))
    (is (= * (my-eval {} *)))))

(deftest if-nil-test
  (testing "if statements with nil test eval else"
    (is (= 1 (my-eval {} '(if nil 0 1))))))


(deftest if-nonil-test
  (testing "if statements with non-nil test eval then"
    (is (= 0 (my-eval {'t 1 'x 0 'y 1} '(if t x y))))))


(deftest empty-do-test
  (testing "empty do statement evals to nil"
    (is (= nil (my-eval {} '(do))))))

(deftest do-1exp-test
  (testing "one-expression do evals to expression"
    (is (= 1 (my-eval {} '(do 1))))))


(deftest do-multiexp-test
  (testing "multi-expression do evals to last"
    (is (= 4 (my-eval {} '(do 1 2 3 4))))))


(deftest let-env-test 
  (testing "let statements add to environment"
    (is (= 1 (my-eval {} '(let [x 1] x))))))


(deftest let-implicit_do-test
  (testing "let statements have implicit do"
    (is (= 10 (my-eval {} '(let [x 10] "hello" x))))))


(deftest fn-argeval_thenapply-test
  (testing "function application evals the args, 
               then applies function to evaled args (use map and apply)"
    (is (= 2 (my-eval {'+ +} '(+ 1 1))))
    (is (= 5 (my-eval {'/ /} '(/ 20 4))))))


(deftest recursive-test
  (testing "ensure we're calling my-eval recusively"
    (is (= 3 (my-eval {'+ +} '(do (+ 1 2)))))
    (is (= 13 (my-eval {'+ +} '(+ (+ 1 10) 2))))))


(deftest do-evals_all-test
  (testing "do expressions evaluate all forms"
    (is (= 85 (my-eval {'deref deref 'reset! reset! 'a (atom nil) '+ +} 
                           '(do (reset! a 10) (+ (deref a) 75)))))))


