(ns mcss.specs
  (:require [clojure.spec.alpha :as s]))

(s/def ::component symbol?)

(s/def ::tag keyword?)

(s/def ::atomics (s/coll-of symbol? :kind vector?))

(s/def ::style map?)

(s/def ::defstyled
  (s/cat :c ::component
         :tag ::tag
         :atomics (s/? ::atomics)
         :style (s/? ::style)))
