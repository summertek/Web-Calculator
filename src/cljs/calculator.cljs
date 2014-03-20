;; seems to work well, only thing is that it doesn't zoom to fit screen, and doesn't disable double-tap
;; and other gestures for zooming and stuff, so using it like a normal calculator will end up zooming in and out
(ns
^{:author "Peter Schmidt"
  :doc "a simple set of functions to do math on a web page"}
  com.my.calc
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
(enable-console-print!)


;;
;;  sadly - global variables
;;        - adcd cd javaditional sadness these used as mutable
;;
(def accumulator '("" "") )
(def enteredValue "")

;;
;; set of valid key input
;;    - used in determineKey
(def validKeys #{"0","1","2","3","4","5","6","7","8","9",".","*","/","+","-","="} )


;
; This function definition is setup first with one argument, then again with two arguments
;
(defn toAccumulator
   ([operator]
   (println "toaccumulator single")
    (if (= operator "")
       (set! accumulator  '("" ""))

      (let [acc (ef/from "#display" (ef/get-prop :value))]
           (set! accumulator    (list operator acc ))))
      (println accumulator)     )
   ([operator value]
     (println "toaccumulator two")
      (set! accumulator    (list operator value ))
      (println accumulator))
      )
;    (if (= operator "")
;       (set! accumulator  value)
;
;      (let [acc (ef/from "#display" (ef/get-prop :value))]
;           (set! accumulator    (list operator acc ))))) )

(defn doCalc
"this is where comments can live"
^{:calculator/meta-data "this is information that can be used"}
        [newaction buffer]
   (let [prev    buffer
         current (ef/from "#display" (ef/get-prop :value))]
      (println "docalc")

      (println (first prev))
      (println (rest prev))
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
         (= (first prev) "")  (let [newValue (.toString  (second prev)) ]
                                 (println "the blank")

                                 (println (second prev)  )
                                 (println current)
                                   (toAccumulator " " newValue)
                                    newValue)
                                    ))
)

(defn doMultiply []
  (if (= (first accumulator)  "")
    (do
      (toAccumulator  "*")
      (ef/at "#display" (ef/content "")))

      (doCalc "*" accumulator))
      (ef/at "#display" (ef/content "")) )

(defn doAdd []
     (println "doAdd")
     (println accumulator)
     (println (first accumulator))
  (if (= (first accumulator) "")
    (do
      (toAccumulator  "+")
      (ef/at "#display" (ef/content "")))

      (doCalc "+" accumulator))
      (ef/at "#display" (ef/content "")) )


(defn doSubtract []
  (if (= (first accumulator)  "")
    (do
      (toAccumulator  "-")
      (ef/at "#display" (ef/content "")))

      (doCalc "-" accumulator))
      (ef/at "#display" (ef/content "")) )

(defn doDivide []
  (if (= (first accumulator)  "")
    (do
      (toAccumulator  "/")
      (ef/at "#display" (ef/content "")))

      (doCalc "/" accumulator))
      (ef/at "#display" (ef/content "")) )

(defn doEqual []
     (println "doEqual")
      (ef/at "#display" (ef/content (doCalc "" accumulator))))

(defn enterDecimal []
  (let [contents (ef/from "#display" (ef/get-prop :value))]
     (if  (gstring/contains contents ".") () (ef/at "#display" (ef/append "."))  )
   ) )

(defn change [msg]
; (js/alert msg)
  (cond
     (and (>= 9 msg) (<= 0 msg))  (ef/at "#display" (ef/append msg))
     (= "." msg) 		      (enterDecimal)
     (= "*" msg)                      (doMultiply)
     (= "+" msg)                      (doAdd)
     (= "-" msg)                      (doSubtract)
     (= "/" msg)                      (doDivide)
     (= "=" msg)                      (doEqual)
     :else (js/alert msg))
  (ef/at "#display" (ef/focus)) )

(defn clear []
;  (set! accumulator  '("" ""))

  (ef/at "#display" (ef/content ""))
  (ef/at "#display" (ef/focus)) )

(defn clear_events []
  (set! accumulator  '("" ""))

  (ef/at ".clear" (events/listen :click #(clear) ))
  (ef/at "#display" (ef/focus)) )

(defn determineKey [code]
   (ef/at "#display" (ef/focus))
   (let [char (js/String.fromCharCode code)]
     (if (contains? validKeys char) char
                                    "" ) )
    )

(defn button_event [e]
;   (js/alert js/e.target.id )
   (change (ef/from (.concat "#" js/e.target.id) (ef/get-text)) ) )

(defn keyed_event [e]
;   (js/alert js/e.keyCode )
   (change (determineKey js/e.charCode) ))


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

