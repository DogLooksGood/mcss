(ns demo.combinator-demo
  (:require [mcss.core :as mcss]))

(mcss/defa red-bg
  {:background-color {:rgb [0xff 0x99 0x99]}})

(mcss/defa red-child
  ^{:combinators {"> div" {:color "red"}}}
  {:font-size "8em"})

(mcss/defstyled RedChild :div
  [red-child red-bg])

(mcss/defstyled BlueImportant :div
  ^{:combinators {"~ .important"
                  ^{:pseudo {:after {:content "'!!'"}}}
                  {:color "blue"}}}
  {})

(defn root []
  [:div
   [RedChild
    [:div "hello"]]
   [BlueImportant]
   [:div.important "World"]])
