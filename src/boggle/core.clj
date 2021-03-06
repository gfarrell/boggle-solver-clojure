(ns boggle.core
    (:use [clojure.math.numeric-tower :only [abs]]
          [clojure.java.io :as io])
    (:require [clojure.string :as string])
    (:gen-class))

(def dict (io/resource "ospd.txt"))

; A boggle board is a square NxN matrix of characters
; As a matrix, the first element is the rows, the second is the column M[R][C]
; A word consists of a non-repetitive string of adjacent characters
; By non-repetitive, we mean that a cell cannot be re-used within a single word
; Words have a minimum length of three

; naïve strategy
; cycle over wordlist; for each word, try to construct it in the board
; if word is found in the board, add it to the success list

(def trial-board
    [["H" "F" "E" "R"]
     ["E" "A" "O" "Y"]
     ["O" "Y" "V" "I"]
     ["A" "R" "E" "I"]])

(defn all
    "ensures all elements of a seq are truthy"
    [ls]
    (reduce (fn [res cur] (if (not cur) false res)) true ls))

(defn none
    "ensures none of the elements of a seq are truthy"
    [ls]
    (reduce (fn [res cur] (if cur false res)) true ls))

(defn subseq?
    "tests if one sequence is a subset of another"
    [sub sup]
    (empty? (filter (fn [el] (not (some #{ el } sup))) sub)))

(defn valid-word?
    "checks if a word is valid"
    [word]
    (and (>= (count word) 3) (nil? (re-seq #"(?i)q([^u])" word))))

(defn word-to-tiles
; currently unused as breaks word-finding algorithm for some reason...
    "converts a string (word) to a series of tiles (letters)"
    [word]
    (map #(if (= \Q %) "QU" (str %)) (string/replace (string/upper-case word) "QU" "Q")))

(defn wrow "get the row position of a letter" [letter] (get letter 1))
(defn wcol "get the column position of a letter" [letter] (get letter 2))

(defn adjacent?
    "returns whether two letters are adjacent in the grid"
    [a b]
    (and (>= 1 (abs (- (wrow a) (wrow b)))) (>= 1 (abs (- (wcol a) (wcol b))))))

(defn adjacents
    "gets a list of adjacent letters from a charlist given a letter"
    [letter charlist]
    (filter (complement (partial = letter)) (filter (partial adjacent? letter) charlist)))

(defn is-letter
    "checks if a tile [<str> <int> <int>] has a str value as given"
    [letter test-char]
    (= (str test-char) (str (get letter 0))))

(defn find-in-charlist
    "finds a character in a charlist, returning the letter occurrences"
    [charlist test-char]
    (filter #(is-letter % test-char) charlist))

(defn next-paths
    "adds a letter to the relevant paths, producing a new list of paths"
    [paths chars]
    (if (empty? paths)
        []
        (reduce (fn [new-paths path]
                    (into new-paths (map #(conj path %) (adjacents (last path) chars))))
                [] paths)))

(defn valid-paths
    "returns only valid paths from a pathlist"
    [paths]
    (filter #(= (count (set %)) (count %)) paths))

; split word into chars
; for each char, if it's in the charlist, add it to the list of paths
; for each char, check presence and then check adjancency to any paths
(defn find-word
    "finds all the possible ways to construct a word from a charlist"
    [charlist word]
    (loop [i 1
           paths (map #(vector %) (find-in-charlist charlist (get word 0)))]
        (if (empty? paths)
            nil
            (let [test-char (get word i)
                  occurrences (find-in-charlist charlist test-char)]
                (if (nil? test-char)
                    (valid-paths paths)
                    (if (empty? occurrences)
                        nil
                        (recur (inc i) (next-paths paths occurrences))))))))

; now to interact with actual words
(defn load-words
    "loads words from the dictionary"
    ([] (load-words dict))
    ([dict-file] (map string/upper-case (filter valid-word? (string/split-lines (slurp dict-file))))))

(defn find-all-words
    "finds all the dictionary words present in a boggle board described by a charlist"
    [charlist wordlist]
    (reduce (fn [found word] (let [paths (find-word charlist word)]
                                 (if (not (empty? paths))
                                     (into found { word paths })
                                     found)))
            {} wordlist))

(defn board-to-cl
    "converts a board to a charlist"
    [board]
    (loop [i 0 charlist []]
        (let [row (get board i)]
            (if (nil? row)
                charlist
                (recur (inc i)
                       (into charlist (reduce (fn [idxd cell] (conj idxd [(str cell) i (count idxd)]))
                                              []
                                              row)))))))

(defn load-board
    "loads a board from a string input (e.g. from a slurped file)"
    [board]
    (into [] (map #(string/split % #"\s") (string/split-lines board))))

(defn -main
  "reads a boggle board (space/line-separated) from a file and produces a list of words"
  [filename]
  (let
      [result (find-all-words (board-to-cl (load-board (slurp filename))) (load-words))]
      (do (doall (map (fn [[word paths]] (println word ": " paths)) result))
          (println (count result) "words found!"))))
