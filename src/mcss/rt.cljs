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
  (try
    (apply goog.string.format
           (str/replace css #"\{\{\}\}" cls)
           (map (fn [f] (f)) args))
    (catch js/Error e
      (println args)
      (throw e))))

(defn reg-style
  [cls css args protect-fn]
  (when (or goog.DEBUG (not (gobj/get styles cls)))
    (println cls css (.-name ^js protect-fn))
    (protect-fn)
    (gobj/set styles cls (format-css cls css args))))

(defn reg-custom [pname css args protect-fn]
  (when (or goog.DEBUG (not (gobj/get customs pname)))
    (protect-fn)
    (gobj/set customs pname (format-css pname css args))))

(defn build-style []
  (goog.string.format ":root{%s}%s"
                      (str/join "\n" (gobj/getValues customs))
                      (str/join "\n" (gobj/getValues styles))
                      #_(apply str (gobj/getValues customs))
                      #_(apply str (gobj/getValues styles))))

(defn get-or-create-style-elm []
  (or (.getElementById js/document style-tag-id)
      (doto (.createElement js/document "style")
        (.setAttribute "type" "text/css")
        (.setAttribute "id" style-tag-id))))
