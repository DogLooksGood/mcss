(ns demo.compress2
  (:require [mcss.core :as mcss]))

(mcss/defkeyframes my-kf
  [:from {:background-color "blue"}]
  [:to {:background-color "red"}])

(mcss/defcustom br "5%")

(mcss/defa o
  {:border-radius br})
