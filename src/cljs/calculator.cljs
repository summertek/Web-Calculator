(ns my.calc
  (:require   [clojure.string :as string]
              [cljs.core.match]
	      [enfocus.core :as ef]
              [enfocus.effects :as effects]
              [enfocus.events :as events]
              [goog.dom :as gdom]
              [goog.string :as gstring])
    (:use-macros [enfocus.macros :only [deftemplate defsnippet defaction]]
                 [cljs.core.match.macros :only [match]]))

;;************************************************
;; Dev stuff
;;************************************************
(def dev-mode true)

(def accumulator ())
; set of valid keys
(def validKeys #{"0","1","2","3","4","5","6","7","8","9",".","*","/","+","-","="} )

(defn toAccumulator [operator]
    (if (= operator "")
       (set! accumulator  ())

      (let [acc (ef/from "#display" (ef/get-prop :value))]
           (set! accumulator    (list operator acc ))))
)

(defn doCalc [newaction buffer]
   (let [prev    buffer
         current (ef/from "#display" (ef/get-prop :value))]
      (cond
         (= (first prev) "*")  (let [newValue (.toString (* (second prev) current)) ]
                                   (toAccumulator newaction newValue)
                                    newValue)
         (= (first prev) "+")  (let [newValue (.toString (+ (js/Number (second prev)) (js/Number current))) ]
                                   (toAccumulator newaction newValue)
                                    newValue)
         (= (first prev) "-")  (let [newValue (.toString (- (js/Number (second prev)) (js/Number current))) ]
                                   (toAccumulator newaction newValue)
                                    newValue)
         (= (first prev) "/")  (let [newValue (.toString (/ (second prev) current)) ]
                                   (toAccumulator newaction newValue)
                                    newValue)
                                    ))
)

(defn doMultiply []
  (if (= (first accumulator) nil)
    (do
      (toAccumulator  "*")
      (ef/at "#display" (ef/content "")))

      (doCalc "*" accumulator))
      (ef/at "#display" (ef/content "")) )

(defn doAdd []
  (if (= (first accumulator) nil)
    (do
      (toAccumulator  "+")
      (ef/at "#display" (ef/content "")))

      (doCalc "+" accumulator))
      (ef/at "#display" (ef/content "")) )


(defn doSubtract []
  (if (= (first accumulator) nil)
    (do
      (toAccumulator  "-")
      (ef/at "#display" (ef/content "")))

      (doCalc "-" accumulator))
      (ef/at "#display" (ef/content "")) )

(defn doDivide []
  (if (= (first accumulator) nil)
    (do
      (toAccumulator  "/")
      (ef/at "#display" (ef/content "")))

      (doCalc "/" accumulator))
      (ef/at "#display" (ef/content "")) )

(defn doEqual []
  (if (= (first accumulator) nil)
    (do
;      (toAccumulator  "")
      (ef/at "#display" (ef/content "")))

      (ef/at "#display" (ef/content (doCalc "" accumulator))) )
)

(defn enterDecimal []
  (let [contents (ef/from "#display" (ef/get-prop :value))]
     (if  (gstring/contains contents ".") () (ef/at "#display" (ef/append "."))  )
   ) )

(defn change3 [msg]
; (js/alert msg)
  (cond
     (and (>= "9" msg) (<= "0" msg))  (ef/at "#display" (ef/append msg))
     (= "." msg) 		      (enterDecimal)
     (= "*" msg)                      (doMultiply)
     (= "+" msg)                      (doAdd)
     (= "-" msg)                      (doSubtract)
     (= "/" msg)                      (doDivide)
     (= "=" msg)                      (doEqual)
     :else (js/alert msg))
  (ef/at "#display" (ef/focus)) )

(defn clear []
  (ef/at "#display" (ef/content ""))
  (ef/at "#display" (ef/focus)) )

(defn clear_events []
  (ef/at ".clear" (events/listen :click #(clear) ))
  (ef/at "#display" (ef/focus)) )

(defn determineKey_match [code]
  (let [keypressed (js/String.fromCharCode code)]
;    (js/alert keypressed)
     (match [keypressed]
           ["0"] "0"
           ["1"] "1"
           ["2"] "2"
           ["3"] "3"
           ["4"] "4"
           ["5"] "5"
           ["6"] "6"
           ["7"] "7"
           ["8"] "8"
           ["9"] "9"
           ["."] "."
           ["+"] "+"
           ["-"] "-"
           ["/"] "/"
           ["="] "="
           ["*"] "*"
           :else "")))
;    (js/String.fromCharCode keypressed)))

(defn determineKey_cond [code]
   (ef/at "#display" (ef/focus))
   (let [char (js/String.fromCharCode code)]
     (if (contains? validKeys char) char
                                    "" ) )
    )

(defn determineKey_cond_V1 [code]
   (ef/at "#display" (ef/focus))
   (let [char (js/String.fromCharCode code)]
     (cond
       (and (>= "9" char) (<= "0" char)) char
       (= "." char) 			 char
       (= "*" char)                      char
       (= "+" char)                      char
       (= "-" char)                      char
       (= "/" char)                      char
       (= "=" char)                      char
       :else 				 char ) )
    )

(defn button_event [e]
;   (js/alert js/e.target.id )
   (change3 (ef/from (+ "#" js/e.target.id) (ef/get-text)) ) )

(defn keyed_event [e]
;   (js/alert js/e.keyCode )
   (change3 (determineKey_cond js/e.charCode) ))
;   (change3 (determineKey_match js/e.charCode) ))


(defn setup_events []
  (ef/at ".digit"    (events/listen :click button_event))
  (ef/at ".operator" (events/listen :click button_event))
  (ef/at "#final"    (events/listen :click button_event))
  (ef/at ".clear"    (events/listen :click #(clear) ))
  (ef/at "#body"     (events/listen :keypress keyed_event))
  (ef/at "#body"     (events/listen :click #(ef/at "#display"  (ef/focus) )))
  )

(defn setup []
  (setup_events)
  (ef/at "#display"  (ef/focus) ) )

(set! (.-onload js/window) setup)

