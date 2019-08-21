(ns demo.simple
  (:require [reagent.core :as reagent]
            [mcss.core :as mcss :refer
             [load-styles!]]
            [demo.perf :as perf]
            #_[demo.combinator-demo :as combinator-demo]))

(defn mount []
  (let [t (.getTime (js/Date.))]
    (load-styles!)
    (reagent/render [perf/root]
                    (.getElementById js/document "app"))
    (.log js/console
          "Cost" (- (.getTime (js/Date.)) t) "ms.")))

(defn after-load []
  (mount))

(defn main []
  (mount))
