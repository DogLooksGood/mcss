(ns mcss.rt
  (:require [clojure.string :as str]
            [goog.string.format]
            [goog.object :as gobj]))

(def style-tag-id "mcss-style-target-id")

(defonce customs #js {})
(defonce styles #js {})

;;; This is used to provent DCE for some functions
(def counter 0)

(defn format-css [cls css args]
  (apply goog.string.format
         (str/replace css #"\$\$" cls)
         (map (fn [f] (f)) args)))

(defn reg-style
  [cls css args protect-fn]
  (when (or goog.DEBUG (not (gobj/get styles cls)))
    (protect-fn)
    (gobj/set styles cls (format-css cls css args))))

(defn reg-custom [pname css args protect-fn]
  (when (or goog.DEBUG (not (gobj/get customs pname)))
    (protect-fn)
    (gobj/set customs pname (format-css pname css args))))

(defn build-style []
  (goog.string.format ":root{%s}%s"
                      (apply str (gobj/getValues customs))
                      (apply str (gobj/getValues styles))))

(defn get-or-create-style-elm []
  (or (.getElementById js/document style-tag-id)
      (doto (.createElement js/document "style")
        (.setAttribute "type" "text/css")
        (.setAttribute "id" style-tag-id))))
