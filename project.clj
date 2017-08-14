(defproject boggle "1.0.0"
  :description "Boggle Board Solver"
  :url "https://github.com/gfarrell/boggle-solver-clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [org.clojure/tools.namespace "0.2.11"]]
  :main ^:skip-aot boggle.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
