(ns mcss.core
  (:require-macros [mcss.core])
  (:require [mcss.rt :as rt]))

(defn load-styles! []
  (let [style-str (rt/build-style)
        head (.-head js/document)
        style-elm (rt/get-or-create-style-elm)]
    (set! (.-innerHTML style-elm) style-str)
    (.append head style-elm)))
