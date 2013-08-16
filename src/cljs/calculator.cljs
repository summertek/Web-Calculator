(ns my.calc
  (:require   [enfocus.core :as ef]
              [enfocus.effects :as effects]
              [enfocus.events :as events]
              [goog.dom :as gdom])
    (:use-macros [enfocus.macros :only [deftemplate defsnippet defaction]]))

 ;;************************************************
;; Dev stuff
;;************************************************
(def dev-mode true)

(defaction sub-demo []
  "#button2" (ef/substitute "I replaced the button"))

(defaction sub-demo2 [msg]
  "#button2" (ef/substitute msg))

(defaction change3 [msg]
  "#button2" (ef/content msg))

(defaction change [target msg]
  target (ef/content msg))

(defaction change1 []
   "#button2" #(ef/content "I have been clicked"))

(defn change2 []
  (ef/at js/document ["#button1"]  #(ef/content   "I have been clicked")))

(defn hello [button msg]
   (gdom/setTextContent (gdom/getElement button) "I have been clicked")
   (js/alert (gdom/getTextContent (gdom/getElement "button1") )))

(defaction setup2 []
  "#button2" (events/listen :click #(sub-demo2 "I have been clicked")))

(defaction setup []
  "#button2" (events/listen :click #(change3 "I have been clicked")))

(defaction setupn1 []
  "#button2" (events/listen :click change1))

(defaction setupworks []
  "#button1" (events/listen :click #(hello "button2" "hello there")))

(set! (.-onload js/window) setup)

