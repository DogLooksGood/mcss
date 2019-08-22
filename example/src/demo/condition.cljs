(ns demo.condition
  (:require [mcss.core :as mcss]))

(mcss/defstyled MyDiv :div
  {:color "green"
   :background-color "blue"
   :active? {:color "red"
             :background-color "yellow"}})

(defn root []
  [:div
   [MyDiv "foo"]
   [MyDiv {:css {:active? true}} "bar"]])
