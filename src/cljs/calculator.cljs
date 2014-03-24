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

;;
;; set of valid key input
;;    - used in determineKey
(def validKeys #{"0","1","2","3","4","5","6","7","8","9",".","*","/","+","-","="} )
(def functions {"+" +, "-" -, "/" /, "*" *})


;
; This function definition is setup first with one argument, then again with two arguments
;
(defn toAccumulator
   ([operator]
    (if (= operator "")
       (set! accumulator  '("" ""))
       (let [acc (ef/from "#display" (ef/get-prop :value))]
           (set! accumulator    (list operator acc ))))
         )

   ([operator value]
      (set! accumulator    (list operator value ))
      )
)

(defn doCalc [newaction buffer]
"this is where comments can live"
^{:calculator/meta-data "this is information that can be used"}
   (let [prev    buffer
         current (ef/from "#display" (ef/get-prop :value))
         newValue (.toString ((get functions (first prev)) (js/Number (second prev)) (js/Number current))) ]
               (toAccumulator newaction newValue)
               newValue)
)

(defn doEqual []
      (ef/at "#display" (ef/content (doCalc "" accumulator))))

(defn enterDecimal []
  (let [contents (ef/from "#display" (ef/get-prop :value))]
     (if  (gstring/contains contents ".") () (ef/at "#display" (ef/append "."))  )
   ) )

(defn dofunction [command]
  (if (= (first accumulator)  "")
    (do
      (toAccumulator  command)
      (ef/at "#display" (ef/content "")))

      (doCalc command accumulator))
      (ef/at "#display" (ef/content "")) )

(defn change [msg]
  (cond
     (and (>= 9 msg) (<= 0 msg))  (ef/at "#display" (ef/append msg))
     (= "." msg) 		      (enterDecimal)
     (= "*" msg)                      (dofunction msg)
     (= "+" msg)                      (dofunction msg)
     (= "-" msg)                      (dofunction msg)
     (= "/" msg)                      (dofunction msg)
     (= "=" msg)                      (doEqual)
     :else (js/alert msg))
  (ef/at "#display" (ef/focus)) )

(defn determineKey [code]
   (ef/at "#display" (ef/focus))
   (let [char (js/String.fromCharCode code)]
     (if (contains? validKeys char) char
                                    "" ) )
    )
;****************************************
;
;  Event Handlers
;
;****************************************

(defn clear_events []
  (set! accumulator  '("" ""))

  (ef/at "#display" (ef/focus)) )

(defn clear []
  (ef/at "#display" (ef/content ""))
  (ef/at "#display" (ef/focus)) )

(defn button_event [e]
   (change (ef/from (.concat "#" js/e.target.id) (ef/get-text)) ) )

(defn keyed_event [e]
   (change (determineKey js/e.charCode) ))


(defn setup_events []
  (ef/at ".digit"    (events/listen :click button_event))
  (ef/at ".operator" (events/listen :click button_event))
  (ef/at "#final"    (events/listen :click button_event))
  (ef/at "#AccClear" (events/listen :click clear_events ))
  (ef/at "#ClearAll" (events/listen :click clear ))
  (ef/at "#final"    (events/listen :click button_event))
  (ef/at "#body"     (events/listen :keypress keyed_event))
  (ef/at "#body"     (events/listen :click #(ef/at "#display"  (ef/focus) )))
  )

(defn setup []
  (setup_events)
  (ef/at "#display"  (ef/focus) ) )

(set! (.-onload js/window) setup)