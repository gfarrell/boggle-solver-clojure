(ns boggle-clojure.core-test
  (:require [clojure.test :refer :all]
            [boggle-clojure.core :refer :all]))

(deftest test-square?
    (testing "square? returns true for a square board"
        (let [square-board [[1 2 3] [4 5 6] [7 8 9]]]
            (is (= true (square? square-board)))))
    (testing "square? returns false for an even, non-square board"
        (let [board [[1 2 3] [4 5 6]]]
            (is (= false (square? board)))))
    (testing "square? returns false for an uneven board"
        (let [board [[1 2 3] [4 5] [7 8 9]]]
            (is (= false (square? board))))))

(deftest test-word-to-string
    (testing "word-to-string converts a list of chars (a word) to a string representation"
        (let [word [[\H 0 1] [\E 0 2] [\L 0 3] [\L 0 4] [\O 0 5]]]
            (is (= "HELLO" (apply word-to-string word))))))

(deftest test-all
    (testing "all returns true if all elements are truthy"
        (is (= true (all [true true true true]))))
    (testing "all returns false if any elements are falsey"
        (is (= false (all [true false true false])))))

(deftest test-none
    (testing "none returns true if all elements are falsey"
        (is (= true (none [false false false false]))))
    (testing "none returns false if any elements are truthy"
        (is (= false (none [true false true false])))))

(deftest test-subseq?
    (let [gen-seq (fn [n] (take n (repeatedly #(rand-int 42))))]
        (testing "subseq? returns true if the super contains all elements of the sub"
            (let [sup (gen-seq 10) sub (take 4 (drop 2 sup))]
                (is (= true (subseq? sub sup)))))
        (testing "subseq? returns false if the sub contains any elements not in the super"
            (let [sup (gen-seq 10) not-sub (map inc sup)]
                (is (= false (subseq? not-sub sup)))))))

(deftest test-adjacent?
    (testing "adjacent? returns true for two adjacent words"
        (is (= true (adjacent? [\A 1 1] [\B 1 2]))))
    (testing "adjacent? returns false for two non-adjacent words"
        (is (= false (adjacent? [\A 1 1] [\B 2 3])))))

(deftest test-adjacents
    (testing "adjacents returns a list of adjacent letters not including the original"
        (let
            [cl [[\A 0 0] [\B 0 1] [\C 0 2] [\D 1 0] [\E 1 1] [\F 1 2] [\G 2 0] [\H 2 1] [\I 2 2]]]
            (let [result (into [] (adjacents [\B 0 1] cl))]
                (is (and
                        (= true (subseq? result [[\A 0 0] [\C 0 2] [\D 1 0] [\E 1 1] [\F 1 2]]))
                        (= (count result) 5)))))))

(deftest test-find-in-charlist
    (testing "find-in-charlist finds a character in a charlist"
        (let [cl [[\A 9 3] [\B 2 1] [\C 8 6] [\A 4 7]]]
        (= [[\A 9 3] [\A 4 7]] (find-in-charlist cl \A)))))
