(ns demo.simple
  (:require [reagent.core :as reagent]
            [mcss.core :as mcss :refer [load-styles!]]
            [demo.perf :refer [root]]
            #_[demo.condition :refer [root]]
            #_[demo.compress :refer [root]]
            #_[demo.combinator-demo :refer [root]]))

(defn mount []
  (load-styles!)
  (.log js/console
        "Cost"
        (- (.getTime (js/Date.)) (.-startTimestamp js/window))
        "ms")
  (reagent/render [root] (.getElementById js/document "app")))

(defn after-load []
  (mount))

(defn main []
  (mount))
