(ns clojure-katas.bank-ocr.core-test
  (:require [clojure.test :refer :all]
            [clojure-katas.bank-ocr.core :refer :all]
            [clojure.string :as str]))

(deftest get-digit-test
  (testing "Testing getting digit."
    (is (= 0 (get-digit " ||_ _ ||")))
    (is (= "?" (get-digit " ||_ _ || |")))
    (is (= "?" (get-digit "")))))

(deftest split-vertically-test
  (testing "Testing splitting vertically."
    (is (= '(" ||" "_ _" " ||" " ||" "_ _" " ||" " ||" "_ _" " ||" " ||" "_ _" " ||" " ||" "_ _" " ||"
             " ||" "_ _" " ||" " ||" "_ _" " ||" " ||" "_ _" " ||" " ||" "_ _")
           (split-vertically [" _  _  _  _  _  _  _  _  _"
                              "| || || || || || || || || |"
                              "|_||_||_||_||_||_||_||_||_|"])))
    (is (= '("   " "___" " ||")
         (split-vertically [" _ "
                            " _|"
                            " _|"])))))

(deftest columns->single-symbol-test
  (testing "Testing turning columns into digits."
    (is (= '(" ||_ _ ||")
           (columns->single-symbol '(" ||" "_ _" " ||"))))
    (is (= '(" ||_ _ ||" "   _   ||")
           (columns->single-symbol '(" ||" "_ _" " ||" "   " "_  " " ||"))))))

(deftest parse-entry-test
  (testing "Testing parsing of entries."
    (is (= '(" ||_ _ ||" "    ||___")
         (parse-entry [" _   _ "
                       "| | |_|"
                       "|_| |_|"])))))

(deftest process-entry-test
  (testing "Testing processing entry into digit."
    (is (= "8" (process-entry [" _ "
                               "|_|"
                               "|_|"
                               "   "])))))

(deftest scan-test
  (testing "Testing scanning"
    (is (= (list "000000000" "111111111" "222222222"
                 "333333333" "444444444" "555555555"
                 "666666666" "777777777" "888888888"
                 "999999999" "123456789")
           (scan "resources/bank_ocr/user_story_1.txt")))
    (is (= "000000000" (-> (str/split-lines " _  _  _  _  _  _  _  _  _ \n| || || || || || || || || |\n|_||_||_||_||_||_||_||_||_|\n                           \n")
                           process-entry)))))

(deftest all-numbers?-test
  (testing "Testing whether all items are numbers"
    (is (not (all-numbers? [1 2 "?" 3 6])))
    (is (= [1 2 3] (all-numbers? [1 2 3])))))

(deftest str->seq-test
  (testing "Convert account number string to a sequence of numbers."
    (is (= [7 1 1 1 1 1 1 1 1] (str->seq "711111111")))))


(deftest reverse-and-multiply-test
  (testing "Reversing and multiplying sequence of numbers."
    (is (= [1 2 3 4 5 6 7 8 63] (reverse-and-multiply [7 1 1 1 1 1 1 1 1])))))

(deftest sum-and-mod-test
  (testing "Testing adding all items together and calulating modulus of 11 and the sum."
    (is (= 0 (sum-and-mod [1 2 3 4 5 6 7 8 63])))))

(deftest valid-checksum-test
  (testing "Testing checksum validation"
    (is (valid-checksum? "711111111"))
    (is (valid-checksum? "123456789"))
    (is (valid-checksum? "490867715"))
    (is (not (valid-checksum? "888888888")))
    (is (not (valid-checksum? "490067715")))
    (is (not (valid-checksum? "012345678")))
    (is (not (valid-checksum? "0?1233565")))))

(deftest annotate-test
  (testing "Testing annotation"
    (is (= "457508000" (annotate "457508000")))
    (is (= "664371495 ERR" (annotate "664371495")))
    (is (= "86110??36 ILL" (annotate "86110??36")))))

(deftest parse-and-annotate-test
  (testing "Testing parse-and-annotate."
    (is (= "000000051"
           (parse-and-annotate
            (str/split-lines " _  _  _  _  _  _  _  _    \n| || || || || || || ||_   |\n|_||_||_||_||_||_||_| _|  |\n                           "))))
    (is (= "49006771? ILL"
           (parse-and-annotate
            (str/split-lines "    _  _  _  _  _  _     _ \n|_||_|| || ||_   |  |  | _ \n  | _||_||_||_|  |  |  | _|\n                           "))))
    (is (= "1234?678? ILL"
           (parse-and-annotate
            (str/split-lines "    _  _     _  _  _  _  _ \n  | _| _||_| _ |_   ||_||_|\n  ||_  _|  | _||_|  ||_| _ \n                           "))))))

(deftest user-story-3-test
  (testing "Testing user story 3"
    (is (= "000000051\n49006771? ILL\n1234?678? ILL\n" (do (scan-and-write-report "resources/bank_ocr/user_story_3.txt")
                                                           (slurp "resources/bank_ocr/user_story_3.txt_annotated.txt"))))))
