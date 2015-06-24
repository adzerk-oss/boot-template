(set-env!
 :src-paths    #{"src"}
 :dependencies '[[adzerk/bootlaces "0.1.11" :scope "test"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "1.0.0")
(bootlaces! +version+)

(task-options!
 pom {:project     'adzerk/boot-template
      :version     +version+
      :description "StringTemplate Boot task"
      :url         "https://github.com/adzerk-oss/boot-template"
      :scm         {:url "https://github.com/adzerk-oss/boot-template"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})
