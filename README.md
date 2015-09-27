# clj_eval

A basic clojure eval function, solution to the exercise http://www.lispcast.com/the-most-important-idea-in-computer-science. 

This version should evaluate all manners of combining do/if/let forms, provided the env maps provides actual functions to all "free" symbols in the form. Note that ns-resolve and variants are not available in clojurescript and thus for portability not used within, which means that all symbols referring to functions not defined in the eval'ed form must be provided in `env`:

(my-eval {'x 1 '+ +} '(let \[y \2] (+ x y)))
;; yields 3 

## Usage
Paste the content of src/clj_eval/core_nolog.txt in the input form of the URL above and see (hopefully) all tests pass.

Or, run tests in this project; or use clj-eval.core/my-eval at the REPL. 
FIXME

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
