# clojure-katas

Clojure solutions to various katas.


## 1. Bank OCR

[Description] (http://code.joejag.com/coding-dojo/bank-ocr/)

You can either run tests (preferrable) by typing in a terminal: `lein
test` or using your favourite IDE

or

you can start a REPL (in a terminal: `lein repl`, or from Emacs: open a
clj/cljs file in the project, then do `M-x cider-jack-in`) and do the
following:

```
(use 'clojure-katas.bank-ocr.core)

;; User Story 1
(scan "resources/bank_ocr/user_story_1.txt")

;; User Story 2

(valid-checksum? "711111111")

;; User Story 3
(scan-and-write-report "resources/bank_ocr/user_story_3.txt")

```
Sample files are located in `resources/bank_ocr/" directory.


More katas to be added.

## License

Copyright © 2015 Anna Pawlicka

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
