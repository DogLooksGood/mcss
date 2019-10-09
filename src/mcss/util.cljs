(ns mcss.util
  (:require [mcss.rt]
            [goog.object :as gobj]))

(defn log-root []
  (.log js/console mcss.rt/customs))

(defn log-style []
  (.log js/console mcss.rt/styles))

(defn query-style [key]
  (gobj/get mcss.rt/styles key))

