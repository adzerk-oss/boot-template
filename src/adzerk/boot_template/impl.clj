(ns adzerk.boot-template.impl
  (:import org.stringtemplate.v4.ST))

(defn render-template
  [^String template ^String path m]
  (.render ^ST (reduce-kv #(.add ^ST %1 %2 %3) (ST. template \$ \$) m)))
