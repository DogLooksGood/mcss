# MCSS
> Macro your CSS in ClojureScript.

# Status?
WIP

# Target
- Design to use with hiccup style library, e.g. reagent.
- Seamless dynamic style supported by CSS variables.
- Generating CSS by macro, nearly zero runtime cost.

# Usage
## Basic Setup
All usefull stuff can be found in namespace `mcss.core`.

Put this in both init function and after-load function(in development).

```clojure
(load-styles!)
```

## defrule - Define a simple global style
Use keyword for property, string or number for simple value.

```clojure
(defrule c-red
  {:color "red"})
```

Use `[]` for comma-separated list values.

``` clojure
(defrule ft-lg
  {:font-size "1.8rem"
   :font-family ["Consolas" "Courier New" "Menlo"]})
```

Use `[[]]`(vector of vector) for whitespace-separated list value.

```clojure
(defrule bd
  {:border [["thin" "solid" "#666"]]})
```

Use `{}` for function call.

```clojure
(defrule foo
  {:color     {:rgb [255 0 0]}
   :transform {:rotate "10deg"}})
```

## defstyled - Define a styled component
Simplest version:

```clojure
(defstyled my-btn :button
  {:background-color "#99ff99"})
```

Use it like this:

```clojure
[my-btn {:on-click #(js/alert "!")}]
```

Combine atomic styles in keyword tag:

```clojure
(defstyled my-btn :button
  [c-red ft-lg bd]
  {:background-color "#99ff99"})
```

Dynamic style supported by use inline function as property.

```clojure
(defstyled my-btn :button
  {:background-color #(get % :bg-clr)})
```

This will be expanded to something like:

```clojure
(mcss.rt/inject-style!
 "some-ns__my-btn{background-color:var(--bg-clr)}")

(defn my-btn [props & children]
  (let [css (:css props)
        bg-clr (#(get % :bg-color) css)]
    (into [:button (merge (dissoc props :css) {:style {"--bg-clr" bg-clr}})]
          children)))
```

Use like this:

```clojure
[my-btn {:on-click #(js/alert "!")
         :css {:bg-color "blue"}}]
```

Use a keyword ends with `?` to define style valid in some case.

```clojure
(defstyled my-div :div
  {:background-color "white"
   :active? {:background-color "black"}})
```

Use keyword for property access. (Only inline function or keyword)

```clojure
(defstyled my-btn :button
  {:background-color :bg-clr
   :color            :clr})
```

## defcustom - Define a custom variable

A custom variable is a variable locate in `:root` selector.

```clojure
(defcustom some-color "#99ff99")
```

Use it like this:

```clojure
(defstyled my-div :div
  {:color some-color})
```

## defkeyframes - Define a keyframe

```clojure
(defkeyframes my-kf
  [:from {:color "blue"}]
  [:to {:color "red"}])
```

Use it like this:

```clojure
(defstyled my-div :div
  {:animation [[my-kf "2s" "infinite" "alternate"]]})
```

## Pseudo support.

```clojure
(defstyled my-div :div
  ^{:pseudo {:before {:content "'Before Here!'"
                      :color   :clr}}}
  ;;                           ^ dynamic style is possible here
  {:background-color "blue"
   :width            "3rem"
   :height           "3rem"})
```

Provide your own `mcss/defaults.clj` to overwrite default vendors settings.

## Media query support.

```clojure
(defstyled my-div :div
  ^{:media {:width {:color "blue"}
            :narrow {:color "red"}}}
  ;;                ^ Note: dynamic style here is not supported!
  {:color "black"})
```

Provide your own `mcss/defaults.clj` to overwrite default media query defines.

# Roadmap
- Add macro validation via spec.
- Add built-in atomic styles.

# Good Libraries I learn from
[cljss](https://github.com/clj-commons/cljss)
[herb](https://github.com/roosta/herb)
[tachyons](https://github.com/tachyons-css/tachyons)
