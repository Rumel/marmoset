(defproject marmoset "0.0.1"
  :description ""
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [armadillo "0.0.4"]]
  :main ^:skip-aot marmoset.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
