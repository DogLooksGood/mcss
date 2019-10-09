(ns mcss.atomic
  (:require [mcss.core :refer [defa defcustom]]))

;;; Flexbox

(defa flex                {:display "flex"})
(defa inline-flex         {:display "inline-flex"})

;;; flex-direction

(defa flex-row            {:flex-direction "row"})
(defa flex-column         {:flex-direction "column"})
(defa flex-column-reverse {:flex-direction "column-reverse"})
(defa flex-row-reverse    {:flex-direction "row-reverse"})


(defa column {:display "flex" :flex-direction "column"})
(defa row    {:display "flex" :flex-direction "row"})
(defa center {:justify-content "center" :align-items "center"})

;;; flex-wrap

(defa flex-wrap           {:flex-wrap "wrap"})
(defa flex-nowrap         {:flex-wrap "nowrap"})
(defa flex-wrap-reverse   {:flex-wrap "wrap-reverse"})

;;; align-items
(defa items-start         {:align-items "flex-start"})
(defa items-end           {:align-items "flex-end"})
(defa items-center        {:align-items "center"})
(defa items-baseline      {:align-items "flex-baseline"})
(defa items-stretch       {:align-items "flex-stretch"})

;;; justify-content

(defa justify-start       {:justify-content "flex-start"})
(defa justify-end         {:justify-content "flex-end"})
(defa justify-center      {:justify-content "center"})
(defa justify-between     {:justify-content "space-between"})
(defa justify-around      {:justify-content "space-around"})



;;; Border

(defa ba {:border-style "solid" :border-width "thin"})
(defa bt {:border-style "solid" :border-width 0 :border-top-width "thin"})
(defa br {:border-style "solid" :border-width 0 :border-right-width "thin"})
(defa bb {:border-style "solid" :border-width 0 :border-bottom-width "thin"})
(defa bl {:border-style "solid" :border-width 0 :border-left-width "thin"})
(defa bn {:border-style "solid" :border-width 0})



;;; Border Widths

(defa bw0 {:border-width 0})
(defa bw1 {:border-width ".125rem"})
(defa bw2 {:border-width ".25rem"})
(defa bw3 {:border-width ".5rem"})
(defa bw4 {:border-width "1rem"})
(defa bw5 {:border-width "2rem"})



;;; Border Radius

(defa br0       {:border-radius 0})
(defa br1       {:border-radius ".125rem"})
(defa br2       {:border-radius ".25rem"})
(defa br3       {:border-radius ".5rem"})
(defa br4       {:border-radius "1rem"})
(defa br-50     {:border-radius "50%"})
(defa br-100    {:border-radius "100%"})
(defa br-pull   {:border-radius "9999px"})
(defa br-bottom {:border-top-left-radius 0
                 :border-top-right-radius 0})
(defa br-top    {:border-bottom-left-radius 0
                 :border-bottom-right-radius 0})
(defa br-right  {:border-top-left-radius 0
                 :border-bottom-left-radius 0})
(defa br-left   {:border-top-right-radius    0
                 :border-bottom-right-radius 0})



;;; Height

(defa h1 {:height "1rem"})
(defa h2 {:height "2rem"})
(defa h3 {:height "4rem"})
(defa h4 {:height "8rem"})
(defa h5 {:height "16rem"})

(defa h-25 {:height "25%"})
(defa h-50 {:height "50%"})
(defa h-75 {:height "75%"})
(defa h-100 {:height "100%"})

(defa vh-25 {:height "25vh"})
(defa vh-50 {:height "50vh"})
(defa vh-75 {:height "75vh"})
(defa vh-100 {:height "100vh"})

(defa min-vh-100 {:min-height "100vh"})

(defa h-auto {:height "auto"})
(defa h-inherit {:height "inherit"})



;;; Width

(defa w1 {:width "1rem"})
(defa w2 {:width "2rem"})
(defa w3 {:width "4rem"})
(defa w4 {:width "8rem"})
(defa w5 {:width "16rem"})

(defa w-10 {:width "10%"})
(defa w-20 {:width "20%"})
(defa w-25 {:width "25%"})
(defa w-30 {:width "30%"})
(defa w-33 {:width "33%"})
(defa w-34 {:width "34%"})
(defa w-40 {:width "40%"})
(defa w-50 {:width "50%"})
(defa w-60 {:width "60%"})
(defa w-70 {:width "70%"})
(defa w-75 {:width "75%"})
(defa w-80 {:width "80%"})
(defa w-90 {:width "90%"})
(defa w-100 {:width "100%"})

(defa w-third {:width {:clac ["100%" "/" 3]}})
(defa w-two-thirds {:width {:clac ["100%" "/" 1.5]}})
(defa w-auto {:width "auto"})



;;; Font Size

(defa f-6 {:font-size "6rem"})
(defa f-5 {:font-size "5rem"})

(defa f1 {:font-size "3rem"})
(defa f2 {:font-size "2.25rem"})
(defa f3 {:font-size "1.5rem"})
(defa f4 {:font-size "1.25rem"})
(defa f5 {:font-size "1rem"})
(defa f6 {:font-size ".875rem"})
(defa f7 {:font-size ".75rem"})



;;; Spacing

(defcustom spacing-0 0)
(defcustom spacing-1 ".25rem")
(defcustom spacing-2 ".5rem")
(defcustom spacing-3 "1rem")
(defcustom spacing-4 "2rem")
(defcustom spacing-5 "4rem")
(defcustom spacing-6 "8rem")
(defcustom spacing-7 "16rem")

(defa pa0 {:padding (spacing-0)})
(defa pa1 {:padding (spacing-1)})
(defa pa2 {:padding (spacing-2)})
(defa pa3 {:padding (spacing-3)})
(defa pa4 {:padding (spacing-4)})
(defa pa5 {:padding (spacing-5)})
(defa pa6 {:padding (spacing-6)})
(defa pa7 {:padding (spacing-7)})

(defa pl0 {:padding-left (spacing-0)})
(defa pl1 {:padding-left (spacing-1)})
(defa pl2 {:padding-left (spacing-2)})
(defa pl3 {:padding-left (spacing-3)})
(defa pl4 {:padding-left (spacing-4)})
(defa pl5 {:padding-left (spacing-5)})
(defa pl6 {:padding-left (spacing-6)})
(defa pl7 {:padding-left (spacing-7)})

(defa pr0 {:padding-right (spacing-0)})
(defa pr1 {:padding-right (spacing-1)})
(defa pr2 {:padding-right (spacing-2)})
(defa pr3 {:padding-right (spacing-3)})
(defa pr4 {:padding-right (spacing-4)})
(defa pr5 {:padding-right (spacing-5)})
(defa pr6 {:padding-right (spacing-6)})
(defa pr7 {:padding-right (spacing-7)})

(defa pb0 {:padding-bottom (spacing-0)})
(defa pb1 {:padding-bottom (spacing-1)})
(defa pb2 {:padding-bottom (spacing-2)})
(defa pb3 {:padding-bottom (spacing-3)})
(defa pb4 {:padding-bottom (spacing-4)})
(defa pb5 {:padding-bottom (spacing-5)})
(defa pb6 {:padding-bottom (spacing-6)})
(defa pb7 {:padding-bottom (spacing-7)})

(defa pt0 {:padding-top (spacing-0)})
(defa pt1 {:padding-top (spacing-1)})
(defa pt2 {:padding-top (spacing-2)})
(defa pt3 {:padding-top (spacing-3)})
(defa pt4 {:padding-top (spacing-4)})
(defa pt5 {:padding-top (spacing-5)})
(defa pt6 {:padding-top (spacing-6)})
(defa pt7 {:padding-top (spacing-7)})

(defa pv0 {:padding-top (spacing-0) :padding-bottom (spacing-0)})
(defa pv1 {:padding-top (spacing-1) :padding-bottom (spacing-1)})
(defa pv2 {:padding-top (spacing-2) :padding-bottom (spacing-2)})
(defa pv3 {:padding-top (spacing-3) :padding-bottom (spacing-3)})
(defa pv4 {:padding-top (spacing-4) :padding-bottom (spacing-4)})
(defa pv5 {:padding-top (spacing-5) :padding-bottom (spacing-5)})
(defa pv6 {:padding-top (spacing-6) :padding-bottom (spacing-6)})
(defa pv7 {:padding-top (spacing-7) :padding-bottom (spacing-7)})

(defa ph0 {:padding-left (spacing-0) :padding-right (spacing-0)})
(defa ph1 {:padding-left (spacing-1) :padding-right (spacing-1)})
(defa ph2 {:padding-left (spacing-2) :padding-right (spacing-2)})
(defa ph3 {:padding-left (spacing-3) :padding-right (spacing-3)})
(defa ph4 {:padding-left (spacing-4) :padding-right (spacing-4)})
(defa ph5 {:padding-left (spacing-5) :padding-right (spacing-5)})
(defa ph6 {:padding-left (spacing-6) :padding-right (spacing-6)})
(defa ph7 {:padding-left (spacing-7) :padding-right (spacing-7)})

(defa ma0 {:margin (spacing-0)})
(defa ma1 {:margin (spacing-1)})
(defa ma2 {:margin (spacing-2)})
(defa ma3 {:margin (spacing-3)})
(defa ma4 {:margin (spacing-4)})
(defa ma5 {:margin (spacing-5)})
(defa ma6 {:margin (spacing-6)})
(defa ma7 {:margin (spacing-7)})

(defa ml0 {:margin-left (spacing-0)})
(defa ml1 {:margin-left (spacing-1)})
(defa ml2 {:margin-left (spacing-2)})
(defa ml3 {:margin-left (spacing-3)})
(defa ml4 {:margin-left (spacing-4)})
(defa ml5 {:margin-left (spacing-5)})
(defa ml6 {:margin-left (spacing-6)})
(defa ml7 {:margin-left (spacing-7)})

(defa mr0 {:margin-right (spacing-0)})
(defa mr1 {:margin-right (spacing-1)})
(defa mr2 {:margin-right (spacing-2)})
(defa mr3 {:margin-right (spacing-3)})
(defa mr4 {:margin-right (spacing-4)})
(defa mr5 {:margin-right (spacing-5)})
(defa mr6 {:margin-right (spacing-6)})
(defa mr7 {:margin-right (spacing-7)})

(defa mb0 {:margin-bottom (spacing-0)})
(defa mb1 {:margin-bottom (spacing-1)})
(defa mb2 {:margin-bottom (spacing-2)})
(defa mb3 {:margin-bottom (spacing-3)})
(defa mb4 {:margin-bottom (spacing-4)})
(defa mb5 {:margin-bottom (spacing-5)})
(defa mb6 {:margin-bottom (spacing-6)})
(defa mb7 {:margin-bottom (spacing-7)})

(defa mt0 {:margin-top (spacing-0)})
(defa mt1 {:margin-top (spacing-1)})
(defa mt2 {:margin-top (spacing-2)})
(defa mt3 {:margin-top (spacing-3)})
(defa mt4 {:margin-top (spacing-4)})
(defa mt5 {:margin-top (spacing-5)})
(defa mt6 {:margin-top (spacing-6)})
(defa mt7 {:margin-top (spacing-7)})

(defa mv0 {:margin-top (spacing-0) :margin-bottom (spacing-0)})
(defa mv1 {:margin-top (spacing-1) :margin-bottom (spacing-1)})
(defa mv2 {:margin-top (spacing-2) :margin-bottom (spacing-2)})
(defa mv3 {:margin-top (spacing-3) :margin-bottom (spacing-3)})
(defa mv4 {:margin-top (spacing-4) :margin-bottom (spacing-4)})
(defa mv5 {:margin-top (spacing-5) :margin-bottom (spacing-5)})
(defa mv6 {:margin-top (spacing-6) :margin-bottom (spacing-6)})
(defa mv7 {:margin-top (spacing-7) :margin-bottom (spacing-7)})

(defa mh0 {:margin-left (spacing-0) :margin-right (spacing-0)})
(defa mh1 {:margin-left (spacing-1) :margin-right (spacing-1)})
(defa mh2 {:margin-left (spacing-2) :margin-right (spacing-2)})
(defa mh3 {:margin-left (spacing-3) :margin-right (spacing-3)})
(defa mh4 {:margin-left (spacing-4) :margin-right (spacing-4)})
(defa mh5 {:margin-left (spacing-5) :margin-right (spacing-5)})
(defa mh6 {:margin-left (spacing-6) :margin-right (spacing-6)})
(defa mh7 {:margin-left (spacing-7) :margin-right (spacing-7)})



;;; Position

(defa static {:position "static"})
(defa relative {:position "relative"})
(defa absolute {:position "absolute"})
(defa fixed {:position "fixed"})



;;; Z-index

(defa z-0 {:z-index 0})
(defa z-1 {:z-index 1})
(defa z-2 {:z-index 2})
(defa z-3 {:z-index 3})
(defa z-4 {:z-index 4})
(defa z-5 {:z-index 5})
(defa z-999 {:z-index 999})
(defa z-9999 {:z-index 9999})
(defa z-max {:z-index 2147483647})

(defa z-inherit {:z-index "inherit"})
(defa z-initial {:z-index "initial"})
(defa z-unset {:z-index "unset"})
