(ns demo.perf
  (:require [reagent.core :as reagent]
            [demo.some-defs :as d :refer [o c r10]]
            [mcss.core :as mcss :refer
             [defrule defstyled defkeyframes defcustom load-styles!]]
            [goog.string.format]))

(defcustom ft ["Consolas" "Input Mono" "DejaVu Sans Mono"])
(defcustom bd-1 [["thin" "solid" "#99ff99"]])
(defcustom bd-2 [["5px" "solid" "#9999ff"]])
(defcustom bd-act [["2px" "solid" "#ff9999"]])
(defcustom p80 "80%")

(defrule h100
  {:height "100vh"})

(defrule w100
  {:width "100vw"})

(defkeyframes ft-pulse
  [:from {:color {:hsl [210 p80 p80]}}]
  [:to   {:color {:rgb [0 0 0]}}])

(defstyled Root :div
  [h100 w100]
  {:display     "flex"
   :font-family ft
   :flex-wrap   "wrap"})

(defstyled Grid :div
  [d/o d/c d/r10]
  ^{:media  {:medium {:border bd-2}}
    :pseudo {:hover {:border       bd-act
                     :border-color {:rgb [#(- 255 (:red %)) 30 30]}}}}
  {:border           bd-1
   :width            :width
   :box-sizing       "border-box"
   :animation        [[ft-pulse "2s" "infinite" "alternate"]]
   :background-color {:rgb [:red 128 128]}
   :active? {:font-weight "bold"
             :color "white"
             :font-size "1.5rem"}})

(defn foo [{:keys [active?]}]
  [:div (str active?)])

(defn root []
  (let [active* (reagent/atom nil)]
    (fn []
      (let [idx @active*]
        [Root
         (for [i (range 255)]
           ^{:key i}
           [Grid {:on-click #(reset! active* i)
                  :css {:active? (= i idx)
                        :width "3rem"
                        :red i}}
            (str i)])]))))
