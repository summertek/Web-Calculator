;;
;;  determineKey using cond and no list
;;

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

