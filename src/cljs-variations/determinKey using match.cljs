
;;
;; determineKey using the match library rather than cond statement
;;
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

