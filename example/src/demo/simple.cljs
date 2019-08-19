(ns demo.simple
  (:require [reagent.core :as reagent]
            [mcss.core :refer [defrule defstyled defkeyframes defcustom load-styles!]]
            [goog.string.format]))

(defn rgb [r g b]
  (goog.string.format "rgb(%d,%d,%d)" r g b))

(defcustom ft ["Consolas" "Input Mono" "DejaVu Sans Mono"])
(defcustom bd-1 [["thin" "solid" "#99ff99"]])
(defcustom bd-2 [["5px" "solid" "#9999ff"]])
(defcustom bd-act [["2px" "solid" "#ff9999"]])

(defrule ".h100"
  {:height "100vh"})

(defrule ".w100"
  {:width "100vw"})

(defrule ".o"
  {:border-raidus "50%"})

(defrule ".r10"
  {:transform (rotate "10deg")})

(defrule ".c"
  {:display         "flex"
   :justify-content "center"
   :align-items     "center"})

(defkeyframes ft-pulse
  [:from {:color "white"}]
  [:to   {:color "black"}])

(defstyled Root :div.h100.w100
  {:display     "flex"
   :font-family (cssvar ft)
   :flex-wrap   "wrap"})

(defstyled Grid :div.o.r10.c
  ^{:media {:medium {:border (cssvar bd-2)}}
    :pseudo {:hover {:border (cssvar bd-act)
                     :border-color #(rgb (- 255 (:red %)) 30 30)}}}
  {:border (cssvar bd-1)
   :width :width
   :box-sizing "border-box"
   :animation [[ft-pulse "2s" "infinite" "alternate"]]
   :background-color #(rgb (:red %) 128 128)})

(defn root []
  [Root
   (for [i (range 255)]
     ^{:key i}
     [Grid {:red i
            :width "3rem"
            :on-click #(js/alert (str "Click at:" i))}
      (str i)])])

(defn mount []
  (let [t (.getTime (js/Date.))]
    (load-styles!)
    (reagent/render [root] (.getElementById js/document "app"))
    (.log js/console
          "Cost" (- (.getTime (js/Date.)) t) "ms.")))

(defn after-load []
  (mount))

(defn main []
  (mount))
