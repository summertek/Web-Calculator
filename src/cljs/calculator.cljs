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

(def accumulator nil)
(def outStandingOperator nil)

(defn doMultiply []
  (set! accumulator          (ef/from "#display" (ef/get-prop :value)))
  (set! outStandingOperator  "*")

  (ef/at "#display" (ef/content ""))

  (js/alert "time to multiply")
   )

(defn doAdd []
  (js/alert "time to Add")
   )

(defn doSubtract []
  (js/alert "time to Subtract")
   )

(defn doDivide []
  (js/alert "time to Divide")
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
     :else (js/alert msg))
  (ef/at "#display" (ef/focus)) )

(defn clear []
  (ef/at "#display" (ef/content ""))
  (ef/at "#display" (ef/focus)) )

(defn whoclicked []
   (js/alert "whoclicked")
   (let [a (this-as window (.event.toElement.id.toString window))]
        (js/alert a)
        (+ "#" a)))

(defn digit_events []
  (ef/at ".digit" (events/listen :click #(change3 (ef/from (whoclicked) (ef/get-text)) )))
  (ef/at "#display" (ef/focus)) )

(defn clear_events []
  (ef/at ".clear" (events/listen :click #(clear) ))
  (ef/at "#display" (ef/focus)) )

(defn key_event [code]
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

(defn an_event [e]
;   (js/alert js/e.target.id )
   (change3 (ef/from (+ "#" js/e.target.id) (ef/get-text)) ) )

(defn keyed_event [e]
;   (js/alert js/e.keyCode )
   (change3 (key_event js/e.charCode) ))


(defn setup_events []
  (ef/at ".digit"    (events/listen :click an_event))
;  (ef/at ".digit"    (events/listen :click #(change3 (ef/from (whoclicked) (ef/get-text)) )))
;  (ef/at ".digit"    (events/listen :touchstart #(change3 (ef/from (whoclicked) (ef/get-text)) )))
  (ef/at ".operator" (events/listen :click an_event))
  (ef/at ".clear"    (events/listen :click #(clear) ))
  (ef/at "#body"     (events/listen :keypress keyed_event))
  (ef/at "#body"     (events/listen :click #(ef/at "#display"  (ef/focus) )))
  )

(defn setup []
  (setup_events)
  (ef/at "#display"  (ef/focus) ) )

(set! (.-onload js/window) setup)

