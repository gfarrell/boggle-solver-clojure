(ns boggle.core-test
  (:require [clojure.test :refer :all]
            [boggle.core :refer :all]))

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
            (is (and
                    (= [[\A 9 3] [\A 4 7]] (find-in-charlist cl \A))
                    (= [[\B 2 1]] (find-in-charlist cl \B)))))))

(deftest test-valid-paths
    (testing "valid-paths filters out all paths with repetition"
        (let [paths [[[\A 0 1] [\B 0 2] [\C 0 3]] [[\A 0 1] [\B 0 2] [\A 0 1]]]]
            (is (= (valid-paths paths) [[[\A 0 1] [\B 0 2] [\C 0 3]]])))))

(deftest test-find-word
    (testing "find-word finds all possible occurences of a word in a charlist"
        (let [cl [[\A 0 0] [\B 0 1] [\C 0 2]
                 [\D 1 0] [\A 1 1] [\E 1 2]
                 [\F 2 0] [\G 2 1] [\H 2 2]]
              result (find-word cl "ABE")]
            (is (and (= (count result) 2)
                     (subseq? result [[[\A 0 0] [\B 0 1] [\E 1 2]]
                                      [[\A 1 1] [\B 0 1] [\E 1 2]]])))))
    (testing "find-word doesn't repeat a grid-cell in a word"
        (let [cl [[\A 0 0] [\B 0 1] [\C 0 2]
                  [\D 1 0] [\E 1 1] [\F 1 2]]
              result (find-word cl "ABAD")]
            (is (empty? result)))))

(deftest test-find-all-words
    (testing "find-all-words"
        (let [wl ["HELLO" "WORLD" "WORE"]
              cl [[\H 0 0] [\W 0 1] [\O 0 2]
                  [\E 1 0] [\L 1 1] [\R 1 2]
                  [\L 2 0] [\O 2 1] [\D 2 2]]
              result (find-all-words cl wl)]
            (testing "correctly pulls out all the occurences of a word in a board"
                (is (= (count (get result "HELLO")) 3))
                (is (subseq? (get result "HELLO")
                             [[[\H 0 0] [\E 1 0] [\L 2 0] [\L 1 1] [\O 2 1]]
                              [[\H 0 0] [\E 1 0] [\L 1 1] [\L 2 0] [\O 2 1]]
                              [[\H 0 0] [\E 1 0] [\L 2 0] [\L 1 1] [\O 0 2]]])))
            (testing "correctly pulls out a single-occurence word in a board"
                (is (= (count (get result "WORLD")) 1))
                (is (subseq? (get result "WORLD")
                             [[[\W 0 1] [\O 0 2] [\R 1 2] [\L 1 1] [\D 2 2]]])))
            (testing "does not include unmatched words"
                (is (not (contains? result "WORE"))))
            (testing "creates a map of <word> -> <paths in the board> for all matches"
                (is (= (count result) 2))))))

(deftest test-board-to-cl
    (testing "board-to-cl"
        (let [board [[\A \B \C]
                     [\D \E \F]
                     [\G \H \I]]]
            (testing "correctly parses a board into a charlist"
                (is (= [[\A 0 0] [\B 0 1] [\C 0 2]
                        [\D 1 0] [\E 1 1] [\F 1 2]
                        [\G 2 0] [\H 2 1] [\I 2 2]]
                       (board-to-cl board)))))))
