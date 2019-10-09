(ns demo.some-defs
  (:require [mcss.core :as mcss :refer
             [defa defstyled defkeyframes defcustom]]))

(defcustom r "8deg")

(defa o
  {:border-raidus "50%"})

(defa r10
  {:transform {:rotate "10deg"}})

(defa r20
  {:transform {:rotate "20deg"}})

(defa r30
  {:transform {:rotate "30deg"}})

(defa r40
  {:transform {:rotate "40deg"}})

(defa r50
  {:transform {:rotate "50deg"}})

(defa r60
  {:transform {:rotate "60deg"}})

(defa c
  {:display         "flex"
   :justify-content "center"
   :align-items     "center"})
