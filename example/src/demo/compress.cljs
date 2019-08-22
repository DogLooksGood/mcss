(ns demo.compress
  (:require [mcss.core :as mcss]
            [demo.compress2 :as t]))

(mcss/defstyled StaticTag :div
  [t/o]
  {:color "red"
   :animation [[t/my-kf "1s" "infinite" "alternate"]]})

(mcss/defstyled DynamicTag :div
  [t/o]
  {:color :clr
   :border-radius t/br
   :background-color {:rgb [0xff #(:green %) 0xff]}})

(defn root []
  [:div
   [StaticTag "Hello!"]
   [DynamicTag
    {:css {:clr "white" :green 0x99}}
    "World!"]])
