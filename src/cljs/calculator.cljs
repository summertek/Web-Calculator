(ns my.calc
  (:require   [clojure.string :as string]
	      [enfocus.core :as ef]
              [enfocus.effects :as effects]
              [enfocus.events :as events]
              [goog.dom :as gdom])
    (:use-macros [enfocus.macros :only [deftemplate defsnippet defaction]]))

 ;;************************************************
;; Dev stuff
;;************************************************
(def dev-mode true)

(defaction change3 [msg]
  "#display" (ef/append msg))

(defn whoclicked []
   (let [a (this-as window (.event.toElement.id.toString window))]
        (js/alert a)
        (string/replace-first a "Num" "#Num")))

(defaction setup []
  ".digit" (events/listen :click #(change3 (ef/from (whoclicked) (ef/get-text)) )))

(set! (.-onload js/window) setup)

