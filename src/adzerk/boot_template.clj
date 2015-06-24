(ns adzerk.boot-template
  {:boot/export-tasks true}
  (:require [clojure.java.io :as io]
            [boot.core       :as core]
            [boot.util       :as util]
            [boot.pod        :as pod]))

(def ^:private pod-deps
  '[[org.antlr/ST4 "4.0.8"]])

(defn- copy [tf dir]
  (let [f (core/tmp-file tf)]
    (util/with-let [to (doto (io/file dir (:path tf)) io/make-parents)]
      (io/copy f to))))

(core/deftask template
  "Perform text substitution on files in the fileset using StringTemplate.  For instance, with a file foo/bar.txt in the fileset with this content:

       My name is $name$

   And this task used like this:

     (template :paths [\"foo/bar.txt\"] :subs {\"name\" \"Barney\"})

   foo/bar.txt will appear in the target directory with this content:

       My name is Barney"

  [p paths       PATH    #{str}     "FileSet root-relative paths of input file(s) to perform substitutions on."
   s subs        OLD=NEW  {str str} "String substitutions to perform on the file(s)."
   o output-type TYPE     kw        "Optional output type; can be one of :resource or :source.  Default is :resource."]

  (let [p   (-> (core/get-env)
                (update-in [:dependencies] (fnil into []) pod-deps)
                pod/make-pod
                future)
        tgt (core/tmp-dir!)]
    (core/with-pre-wrap [fs]
      (core/empty-dir! tgt)
      (if-let [files (and subs (seq (filter (comp (set paths) core/tmp-path) (core/input-files fs))))]
        (do (doseq [f files :let [subf (copy f tgt)
                                  txt  (slurp subf)
                                  path (core/tmp-path f)]]
              (util/info "Performing substitutions on %s\n" path)
              (spit subf (pod/with-call-in @p (adzerk.boot-template.impl/render-template ~txt ~path ~subs))))
            (-> fs
                (core/rm files)
                ((if (= output-type :source)
                   core/add-source
                   core/add-resource) tgt)
                core/commit!))
        fs))))
