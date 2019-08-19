(ns mcss.defaults
  "Define defaults for MCSS.")

(def default-vendors
  "Default vendors setup."
  {:flex-direction  [:webkit :moz]
   :flex-grow       [:webkit :moz]
   :flex-wrap       [:webkit :moz]
   :justify-content [:webkit :moz]
   :align-items     [:webkit :moz]
   :align-content   [:webkit :moz]
   :transition      [:webkit :moz]
   :animation       [:webkit :moz]
   :box-shadow      [:webkit :moz]
   :box-sizing      [:webkit :moz]
   :border-radius   [:webkit :moz]
   :align-self      [:webkit :moz]
   :overflow-scroll [:webkit :moz]
   :keyframes       [:webkit :moz]})

(def default-media
  "Default media query break points."
  {:not-small {:screen "only" :min-width "30em"}
   :medium    {:screen "only" :min-width "30em" :max-width "60em"}
   :large     {:screen "only" :min-width "60em"}})
