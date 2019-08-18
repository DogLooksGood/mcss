(ns mcss.rt
  (:require [clojure.string :as str]))

(def style-tag-id "mcss-style-target-id")

(defonce customs* (atom {}))
(defonce styles* (atom {}))

(defn inject-style! [cls css]
  (swap! styles* assoc cls css))

(defn inject-custom! [k css]
  (swap! customs* assoc k css))

(defn build-style []
  (str ":root{"
       (str/join "\n" (vals @customs*))
       "}\n"
       (str/join "\n" (vals @styles*))))

(defn get-or-create-style-elm []
  (or (.getElementById js/document style-tag-id)
      (doto (.createElement js/document "style")
        (.setAttribute "type" "text/css")
        (.setAttribute "id" style-tag-id))))
