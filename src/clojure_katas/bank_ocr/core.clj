(ns clojure-katas.bank-ocr.core
  (:require [clojure.java.io :as io]
            [clojure.string  :as str]
            [clojure.edn     :as edn]))

(defn get-digit
  "Calculates hash code of the string representation of the digit
  and returns its number counterpart.
  e.g. \" ||_ _ ||\" -> 428057244 -> 0 "
  [symbol]
  (condp = (hash symbol)
    428057244    0 ;; " ||_ _ ||"
    -2112871288  1 ;; "       ||"
    -1265233664  2 ;; "  |___ | "
    1750794302   3 ;; "   ___ ||"
    1881650666   4 ;; " |  _  ||"
    -1328394837  5 ;; " | ___  |"
    1545341174   6 ;; " ||___  |"
    -168837286   7 ;; "   _   ||"
    1466429834   8 ;; " ||___ ||"
    911575349    9 ;; " | ___ ||"
    "?")) ;; illegible number

(defn split-vertically
  "Splits sequence of 3 strings vertically returning a single sequence of
  stringified columns, e.g.
  [\" _ \"
   \"| |\"
   \"|_|\"] -> '(\" ||\", \"_ _\", \" ||\") "
  [entry]
  (->> (map seq entry)
       (apply map str)))

(defn columns->single-symbol
  "Takes a sequence of columns, reads them in groups of 3 (width of a digit),
  and returns a sequence of stringified symbols, e.g.
  '(\" ||\" \"_ _\" \" ||\" \"   \" \"_  \" \" ||\") -> '(\" ||_ _ ||\" \"   _   ||\")"
  [columns-seq]
  (->> (partition 3 columns-seq)
       (map #(apply str %))))

(defn parse-entry
  "Gets a sequence of 3 strings where each string represents single line, e.g.
  [\" _  _  _  _  _  _  _  _  _\"
   \"| || || || || || || || || |\"
   \"|_||_||_||_||_||_||_||_||_|\"]
  Each digit is 3 chars wide and 3 lines high. Processes lines vertically and returns
  a sequence of strings, where each string is a symbolic representation of a digit, e.g.
  \" ||_  _ ||\""
  [entry]
  (-> (split-vertically entry)
      (columns->single-symbol)))

(defn process-entry
  "Gets a sequence of 4 strings (an entry) where each string represents a line.
   Parses those lines into digits."
  [lines]
  (->> (parse-entry (butlast lines)) ;; drop last (empty) line
       (map get-digit)
       (apply str)))

(defn scan
  "Reads the file in, 4 lines at a time, parsing them into account number."
  [url]
  (with-open [rdr (io/reader url)]
    (doall (->> (line-seq rdr)
                (partition 4)
                (map process-entry)))))

;; User Story

(defn all-numbers?
  "Checks is all items are numbers. Returns nil if they're not,
  otherwise returns that sequence."
  [num-seq]
  (when (every? number? num-seq)
    num-seq))

(defn str->seq
  "Converts account number string to a sequence of numbers"
  [s]
  (->> (seq s)
       (map #(-> % str (edn/read-string)))  ;; character literal -> string -> int
       (all-numbers?)))

(defn reverse-and-multiply
  "Reverses the sequence and multiplies each item by its idx + 1."
  [acc-seq]
  (->> (reverse acc-seq)
       (map-indexed (fn [idx item] (* (inc idx) item)))))

(defn sum-and-mod
  "Returns a modulus of 11 and a sum of digits in account number."
  [acc-seq]
  (-> (reduce + acc-seq)
      (mod 11)))

(defn valid-checksum?
  "Gets a sequence of digits and returns true if all items are numbers
  and it passes checksum validation, otherwise returns false.
  Validation is calculates as follows:
   account number:  3  4  5  8  8  2  8  6  5
   position names:  d9 d8 d7 d6 d5 d4 d3 d2 d1

   checksum calculation:
   ((1*d1) + (2*d2) + (3*d3) + ... + (9*d9)) mod 11 == 0"
  [account-number]
  (when-let [number-seq (str->seq account-number)]
    (->> number-seq
         reverse-and-multiply
         (sum-and-mod)
         (= 0))))

;; User story 3

(defn annotate
  "In the case of a wrong checksum, or illegible number,
  this is noted in a second column indicating status."
  [s]
  (cond
   (re-find #"\?" s) (str s " ILL")
   (not (valid-checksum? s)) (str s " ERR")
   "default" s))

(defn parse-and-annotate [entry]
  (-> (process-entry entry)
      annotate))

(defn scan-and-write-report [url]
  (with-open [rdr (io/reader url)]
    (with-open [wrt (io/writer (str url "_annotated.txt"))]
      (doseq [entry (partition 4 (line-seq rdr))]
        (.write wrt (str (parse-and-annotate entry) "\n"))))))
