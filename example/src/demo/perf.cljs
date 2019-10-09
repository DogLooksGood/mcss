(ns demo.perf
  (:require [reagent.core :as reagent]
            [demo.some-defs :as d :refer [o c r10]]
            [mcss.core :as mcss :refer
             [defa defstyled defkeyframes defcustom load-styles!]]))

(defcustom ft ["Consolas" "Input Mono" "DejaVu Sans Mono"])
(defcustom bd-1 [["thin" "solid" "#99ff99"]])
(defcustom bd-2 [["5px" "solid" "#9999ff"]])
(defcustom bd-act [["2px" "solid" "#ff9999"]])
(defcustom p80 "80%")
(defcustom useless "99%")

(defa h100
  {:height "100vh"})

(defa w100
  {:width "100vw"})

(defkeyframes ft-pulse99
  [:from {:color "#999999"}]
  [:to {:color "#999999"}])

(defa w99
  {:width "99vw"})

(defcustom max-rotate "360deg")

(defkeyframes ft-pulse
  [:from {:transform {:rotate "0deg"}}]
  [:to {:transform {:rotate (max-rotate)}}])

(defstyled Root :div
  [h100 w100]
  {:display     "flex"
   :font-family (ft)
   :flex-wrap   "wrap"})

(defstyled GridBox :div
  {:width           :size
   :height          :size
   :display         "flex"
   :justify-content "center"
   :align-items     "center"})

(defcustom bg-ft "1.8rem")

(defstyled Grid :div
  [d/o d/c d/r10]
  ^{:media  {:medium {:border (bd-2)}}
    :pseudo {:hover {:border       (bd-act)
                     :border-color {:rgb [30 30 #(- 255 (:red %))]}}}}
  {:border           (bd-1)
   :width            "90%"
   :height           "90%"
   :box-sizing       "border-box"
   max-rotate        :rotate
   :animation        [[ft-pulse "2s" "infinite" "alternate"]]
   :background-color {:rgb [128 128 :red]}
   :bo?              {:font-weight "bold"
                      :color       "white"
                      :font-size   (bg-ft)}})

(defn root []
  (let [active*      (reagent/atom nil)
        nums         (range 1000)
        on-click-fns (map (fn [i] #(reset! active* i)) nums)]
    (fn []
      (let [idx @active*]
        [Root
         (for [i nums]
           ^{:key i}
           [GridBox {:css {:size "3rem"}}
            [Grid {:on-click (nth on-click-fns i)
                   :css      {:bo?    (= i idx)
                              :width  "3rem"
                              :rotate (str i "deg")
                              :red    (mod i 255)}}
             (str i)]])]))))
