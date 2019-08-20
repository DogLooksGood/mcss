(ns demo.some-defs
  (:require [mcss.core :as mcss :refer
             [defrule defstyled defkeyframes defcustom]]))

(defcustom r "8deg")

(defrule o
  {:border-raidus "50%"})

(defrule r10
  {:transform {:rotate "10deg"}})

(defrule r20
  {:transform {:rotate "20deg"}})

(defrule r30
  {:transform {:rotate "30deg"}})

(defrule r40
  {:transform {:rotate "40deg"}})

(defrule r50
  {:transform {:rotate "50deg"}})

(defrule r60
  {:transform {:rotate "60deg"}})

(defrule c
  {:display         "flex"
   :justify-content "center"
   :align-items     "center"})
