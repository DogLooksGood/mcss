(ns mcss.core
  (:require [clojure.string :as str]
            [mcss.defaults :refer [default-vendors default-media]]
            [cljs.analyzer.api :as aa]))


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

(defn- invalid-component-position-error [c]
  (throw (ex-info "Invalid component here." {:component c})))

(defn- invalid-symbol-error [s]
  (throw (ex-info "Invalid symbol here." {:symbol s})))



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
    (let [{:keys [meta] :as r} (aa/resolve (:env opts) v)
          #:mcss{:keys [type]} meta]
      (case type
        :keyframes
        (->sanitize-symbol-name v opts)

        :custom
        (format "var(--%s)" (->sanitize-symbol-name v opts))

        :static-component
        (invalid-component-position-error v)

        :dynamic-component
        (invalid-component-position-error v)

        ;; Other symbol will be considered as function.
        (invalid-symbol-error v)))

    ;; Function or symbol(refer to function) used to extract data
    (and (seq? v) (#{'fn 'fn*} (first v)))
    (let [s (or (get @vars* v) (gensym "cssvar__"))
          cssvar (format "var(--%s)" s)
          pair [v s]]
      (swap! vars* conj pair)
      cssvar)

    ;; Map will rewrite to a css function call.
    (map? v)
    (let [first-v (second (first v))
          args (if (vector? first-v)
                 first-v
                 [first-v])
          f (ffirst v)]
      (format "%s(%s)" (name f) (->> args
                                     (map #(->css-value % vars* opts))
                                     (str/join ","))))

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
        media (get default-media media-key)]
    (if media
      (format "@media %s{%s{%s}}" (->css-media media) css-sel (apply str css-stmts))
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
                                  [prop `(fn* [css#]
                                              (if (~k css#)
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
                    {:body style :vendors default-vendors :selector (name k)})
        css       (->> base-list
                       (map convert-vendors)
                       (map #(compile-source % (atom {}) opts)))

        kf-vendors (get default-vendors :keyframes)]
    (apply str
           (format "@keyframes %s{%s}" kf (apply str css))
           (map #(format "@-%s-keyframes %s{%s}" (name %) kf (apply str css))
                kf-vendors))))



;;; Output Generator

(defn- gen-dynamic-component [c cls css new-tag vars]
  (let [props-sym (gensym "props__")
        css-sym (gensym "css__")
        bind-vec  (->> vars
                       (mapcat (fn [[expr v]]
                                 (cond (keyword? expr)
                                       [v (list expr css-sym)]

                                       :else
                                       (let [arity-cnt (count (second expr))]
                                         (case arity-cnt
                                           0 [v (list expr)]
                                           1 [v (list expr css-sym)]
                                           (property-fn-arity-error arity-cnt))))))
                       (concat [css-sym (list :css props-sym)])
                       vec)
        style (->> vars
                   (map (fn [[_expr v]] [(str "--" v) v]))
                   (into {}))]
    `(do
       (mcss.rt/inject-style! ~cls ~css)
       (defn ~(with-meta c {:mcss/type :dynamic-component})
         [~props-sym & children#]
         (let ~bind-vec
           (into [~new-tag (merge (dissoc ~props-sym :css)
                                  {:style ~style})]
                 children#))))))

(defn- gen-static-component [c cls css new-tag]
  `(do
     (mcss.rt/inject-style! ~cls ~css)
     (def ~(with-meta c {:mcss/type :static-component}) ~new-tag)))

(defn- gen-keyframes [sym kf css]
  `(do (mcss.rt/inject-style! ~kf ~css)
       (def ~(with-meta sym {:mcss/type :keyframes}) nil)))

(defn- gen-custom [sym name css]
  `(do (mcss.rt/inject-custom! ~name ~css)
       (def ~(with-meta sym {:mcss/type :custom}) nil)))

(defn- gen-style [k css]
  `(mcss.rt/inject-style! ~k ~css))



;;; Public APIs

(defmacro defstyled
  "Define a styled hiccup component. "
  [c tag style]
  (let [opts       {:env (or &env {})}
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
  [sym & frames]
  (let [opts {:env &env}
        kf (->sanitize-symbol-name sym opts)
        css (compile-keyframes kf frames opts)]
    (gen-keyframes sym kf css)))

(defmacro defcustom
  "Define a custom variable."
  [sym value]
  (let [opts {:env &env}
        name (str "--" (->sanitize-symbol-name sym opts))
        css-val (->css-value value (atom {}) opts)]
    (gen-custom sym name (format "%s:%s;" name css-val))))

(defmacro defrule
  "Define a simple class based style."
  [sel style]
  (let [opts {:env &env}
        cls (subs sel 1)
        [css] (compile-css cls style opts)]
    (gen-style cls css)))

(defmacro t [x]
  `(def ~(with-meta x {:foo :bar}) nil))

(defmacro m [x]
  (str (:meta (aa/resolve &env x))))


(comment

  (macroexpand-1
   '(defrule ".r10"
      {:transform {:rotate demo.simple/r}}))

  )
