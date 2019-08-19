(ns mcss.core
  "
  1. Pre-compiled style, fast css injection.
  2. Seamless dynamic style via css variable.

  Define styled component which we use in hiccup.

  For static style:

  (defstyled my-btn :button
    {:color \"blue\"})

  Usage:
  [my-btn]

  Equivalent:

  (inject-style! ...)
  (def my-btn :button.my-btn)

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

  For dynamic style:

  (defstyled my-btn :button
    {:background-color :bg-color
     :color #(:clr %)})

  Usage:
  [my-btn {:clr \"red\", :on-click f}]

  Equivalent:

  (inject-style! ...)
  (defn my-btn [props & children]
    (into [:button.my-btn (merge props {:style ...})]
          children))
  "
  (:require [clojure.string :as str]
            [cljs.analyzer.api :as aa]))



;;; Configuration

(def ^:dynamic
  *vendors*
  "Default vendors setup, can be configured by `set-vendors!`."
  {:flex-direction  [:webkit :moz]
   :flex-grow       [:webkit :moz]
   :flex-wrap       [:webkit :moz]
   :justify-content [:webkit :moz]
   :align-items     [:webkit :moz]
   :align-content   [:webkit :moz]
   :transition      [:webkit :moz]
   :animation       [:webkit :moz]
   :box-shadow      [:webkit :moz]
   :box-sizing      [:webkit :moz]
   :border-radius   [:webkit :moz]
   :align-self      [:webkit :moz]
   :overflow-scroll [:webkit :moz]
   :keyframes       [:webkit :moz :o]})

(def ^:dynamic
  *media*
  "Default media query break points, can be configured by `set-media!`."
  {:not-small "only screen and (min-width:30em)"
   :medium    "only screen and (min-width:30em) and (max-width:60em)"
   :large     "only screen and (min-width:60em)"})



;;; Errors
;;; TODO add more errors

(defn- property-fn-arity-error [arity]
  (throw (ex-info "Property function only support 0 or 1 arity."
                  {:arity arity})))

(defn- invalid-property-value-error [v]
  (throw (ex-info (str (prn-str v) " is not allowed here, to use dynamic style provide a function as property value.")
                  {:v v})))

(defn- no-fallback-error [p]
  (throw (ex-info "No fallback value for property" {:property p})))



;;; Transform

(defn- ->sanitize-symbol-name [s {:keys [env] :as _opts}]
  (if env
    (if (namespace s)
      (-> (aa/resolve env s)
          :name
          str
          (str/replace #"\." "_")
          (str/replace #"/" "__"))
      (let [nname (name (get-in env [:ns :name]))]
        (str (str/replace nname #"\." "_")
             "__"
             (name s))))
    (str (str/replace *ns* #"\." "_")
         "__"
         (name s))))

(defn- ->css-selector
  [cls]
  (name cls))

(defn- ->css-property
  [p opts]
  (cond
    (symbol? p)
    (let [s (->sanitize-symbol-name p opts)]
      (str "--" s))

    (keyword? p)
    (name p)

    :else p))

(defn- ->css-value [v vars* opts]
  (cond
    ;; string -> common css value.
    ;; number -> common css value.
    (or (string? v) (number? v))
    v

    ;; Vector is a list separate by comma,
    ;; Vector of vector is a list separate by space.
    (vector? v)
    (->> v
         (map #(->css-value % vars* (assoc opts :inside-vec? true)))
         (str/join (if (:inside-vec? opts) " " ",")))

    ;; Keyword will be convert to css variable.
    (keyword? v)
    (let [s (symbol (name v))
          cssvar (format "var(--%s)" s)
          pair [v s]]
      (swap! vars* conj pair)
      cssvar)

    ;; Refer to a custom valiable or keyframes
    (symbol? v)
    (->sanitize-symbol-name v opts)

    ;; Function or symbol(refer to function) used to extract data
    (and (seq? v) (#{'fn 'fn*} (first v)))
    (let [s (or (get @vars* v) (gensym "cssvar__"))
          cssvar (format "var(--%s)" s)
          pair [v s]]
      (swap! vars* conj pair)
      cssvar)

    ;; CSS function call
    (list? v)
    (cond (= 'cssvar (first v)) (format "var(--%s)" (->sanitize-symbol-name (second v) opts))
          :else (format "%s(%s)" (first v) (str/join "," (map name (next v)))))

    :else
    (invalid-property-value-error v)))

(defn- ->css-media [media]
  (->> (for [[k v] media]
         (case k
           :screen           (format "%s %s" (name v) (name k))
           :min-width        (format "(%s:%s)" (name k) (name v))
           :max-width        (format "(%s:%s)" (name k) (name v))
           :min-device-width (format "(%s:%s)" (name k) (name v))
           :max-device-width (format "(%s:%s)" (name k) (name v))))
       (str/join " and ")))

(defn- ->css-stmt [p v vars* opts]
  (format "%s:%s;" (->css-property p opts) (->css-value v vars* opts)))



;;; Compiler


(defn- style-merge [s1 s2]
  (cond (map? s2) (merge-with style-merge s1 s2)
        s2 s2
        :else s1))

(defn- compile-source
  [{:keys [selector body media-key]} vars* opts]
  (let [css-stmts (map (fn [[k v]] (->css-stmt k v vars* opts)) body)
        css-sel (->css-selector selector)
        media (get *media* media-key)]
    (if media
      (format "@media %s{%s{%s}}" media css-sel (apply str css-stmts))
      (format "%s{%s}" css-sel (apply str css-stmts)))))

(defn- expand-media [base]
  (let [media (:media base)
        base (dissoc base :media)]
    (for [[media-key v] (cons nil media)
          :let
          [p (:pseudo (meta v))]]
      (-> base
          (assoc :media-key media-key)
          (update :body style-merge v)
          (update :pseudo style-merge p)))))

(defn- expand-pseudo [{:keys [cls body pseudo vendors media-key]}]
  (cons {:selector (str "." (name cls))
         :body body
         :vendors vendors
         :media-key media-key}
        (for [[p v] pseudo]
          {:selector (str "." (name cls) ":" (name p))
           :body v
           :vendors vendors
           :media-key media-key})))

(defn- convert-question-mark [{:keys [body] :as source}]
  (let [lst (->> body
                 (keep (fn [[k v]]
                         (when (str/ends-with? (name k) "?")
                           (->> (for [[prop value] v]
                                  [prop `(fn* [props#]
                                              (if (~k props#)
                                                ~value
                                                ~(or (get body prop)
                                                     "")))])
                                (into {}))))))
        s (->> body
               (filter #(not (str/ends-with? (name (key %)) "?")))
               (into {}))]
    (assoc source :body (apply merge s lst))))

(defn- convert-vendors [{:keys [body vendors] :as source}]
  (let [new-body (->> body
                      (mapcat
                       (fn [[p v]]
                         (if-let [vendor (get vendors p)]
                           (into [[p v]]
                                 (map #(vector (str "-" (name %) "-" (name p)) v)
                                      vendor))
                           [[p v]])))
                      (into {}))]
    (assoc source :body new-body)))

(defn- compile-css
  "Compile Clojure style, return CSS string and a mapping of CSS variables."
  [cls style opts]
  (let [{:keys [vendors media pseudo]} (meta style)
        base {:body style :media media :pseudo pseudo :vendors (merge *vendors* vendors)
              :cls cls}
        vars* (atom {})
        css (->> base
                 (expand-media)
                 (mapcat expand-pseudo)
                 (map convert-question-mark)
                 (map convert-vendors)
                 (map #(compile-source % vars* opts)))]
    [(str/join "\n" css)
     @vars*]))

(defn- compile-keyframes
  "Compile Clojure keyframe style, return CSS string.
  CSS variable are not supported here."
  [kf keyframes opts]
  (let [base-list (for [[k style] keyframes]
                    {:body style :vendors *vendors* :selector (name k)})
        css       (->> base-list
                       (map convert-vendors)
                       (map #(compile-source % (atom {}) opts)))

        kf-vendors (get *vendors* :keyframes)]
    (apply str
           (format "@keyframes %s{%s}" kf (apply str css))
           (map #(format "@-%s-keyframes %s{%s}" (name %) kf (apply str css))
                kf-vendors))))



;;; Output Generator

(defn- gen-dynamic-component [c cls css new-tag vars]
  (let [props-sym (gensym "props__")
        bind-vec (->> vars
                      (mapcat (fn [[expr v]]
                                (if (keyword? expr)
                                  [v (list expr props-sym)]
                                  (let [arity-cnt (count (second expr))]
                                    (case arity-cnt
                                      0 [v (list expr)]
                                      1 [v (list expr props-sym)]
                                      (property-fn-arity-error arity-cnt))))))
                      vec)
        style  (->> vars
                    (map (fn [[_expr v]] [(str "--" v) v]))
                    (into {}))]
    `(do
       (mcss.rt/inject-style! ~cls ~css)
       (defn ~c
         [~props-sym & children#]
         (let ~bind-vec
           (into [~new-tag (merge ~props-sym {:style ~style})]
                 children#))))))

(defn- gen-static-component [c cls css new-tag]
  `(do
     (mcss.rt/inject-style! ~cls ~css)
     (def ~c ~new-tag)))

(defn- gen-keyframes [kf css]
  `(do (mcss.rt/inject-style! ~kf ~css)
       (def ~(symbol kf) nil)))

(defn- gen-custom [k css]
  `(do (mcss.rt/inject-custom! ~k ~css)
       (def ~(symbol k) nil)))

(defn- gen-style [k css]
  `(mcss.rt/inject-style! ~k ~css))



;;; Public APIs

(defmacro defstyled
  "Define a styled hiccup component. "
  [c tag style]
  (let [opts    {:env &env}
        nname      (str *ns*)
        cls        (str/replace (str nname "_" c) #"\." "-")
        [css vars] (compile-css cls style opts)
        new-tag    (keyword (str (name tag) "." cls))]
    (if (seq vars)
      ;; Dynamic
      (gen-dynamic-component c cls css new-tag vars)
      ;; Static
      (gen-static-component c cls css new-tag))))

(defmacro defkeyframes
  "Define a keyframes."
  [kf & frames]
  (let [opts {:env &env}
        kf (->sanitize-symbol-name kf opts)
        css (compile-keyframes kf frames opts)]
    (gen-keyframes kf css)))

(defmacro defcustom
  "Define a custom variable."
  [c v]
  (let [opts {:env &env}

        c (str "--" (->sanitize-symbol-name c opts))
        v (->css-value v (atom {}) opts)]
    (gen-custom c (format "%s:%s;" c v))))

(defmacro defrule
  "Define a simple class based style."
  [sel style]
  (let [opts {:env &env}
        cls (subs sel 1)
        [css] (compile-css cls style opts)]
    (gen-style cls css)))



;;; Initializer

(defmacro set-vendors! [vendors]
  (alter-var-root #'*vendors* (fn [_] vendors)))

(defmacro set-media! [media]
  (let [media (->> (for [[k v] media]
                     [k (->css-media v)])
                   (into {}))]
    (alter-var-root #'*media* (fn [_] media))))

(comment

  (defkeyframes kf
    ["0%" {:top "0px"}]
    ["100%" {:top "100px"}])

  (defrule :bg-red
    {:background-color "red"})

  (clojure.pprint/pprint
   (compile-css
    "foo"
    (quote
     ^{:pseudo  {:before {:color "red"}
                 :hover  {:color "green"}}
       :vendors {:justify-content [:webkit :moz]}
       :media   {:not-small
                 ^{:pseudo {:before {:color "blue"}}}
                 {:color   "yellow"
                  :active? {:background-color "black"}}

                 :large
                 {:color "purple"}}}
     {:color            "red"
      :background-color :bg-color
      :justify-content  "center"
      :width            #(str % "px")
      :border           [["thin" "solid" "grey"]]
      :active?          {:border           [["thin" "solid" :bdclr]]
                         :background-color "white"}})
    {}))

  (clojure.pprint/pprint
   (macroexpand-1
    '(defstyled my-btn :button
       {:color "blue"})))

  (clojure.pprint/pprint
   (macroexpand-1
    '(defstyled my-btn :button
       {:color str/blank?})))

  (clojure.pprint/pprint
   (macroexpand-1
    '(defstyled my-btn :button
       {:background-color :bg-clr
        :color            "blue"
        :font-size        #(inc (:fts %))})))

  (clojure.pprint/pprint
   (macroexpand-1
    '(defstyled my-btn :button.foo
       ^{:pseudo {:before {:content "'hello'"}}}
       {:background-color :bg-clr
        :color            "blue"
        :justify-content  "center"
        :align-items      "center"
        :font-size        #(inc (:fts %))})))

  )
