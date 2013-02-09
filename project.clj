(defproject hoard "0.1.0-SNAPSHOT"
  :description "Generic RESTful Resource over document or key value stores"
  :url "http://github.com/chrisabird/hoard"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
    [org.clojure/tools.cli "0.2.2"]
    [ring/ring-jetty-adapter "1.1.1"]
    [compojure "1.1.1"]
    [cheshire "5.0.1"]
    [congomongo "0.4.0"]
    [org.clojure/clojure "1.4.0"]]
  :profiles {
    :dev { 
      :dependencies [
        [ring-mock "0.1.2"]
        [midje "1.4.0"]
        [clj-http "0.5.7"]]
      :plugins [
        [lein-ring "0.7.5"]
        [lein-midje "2.0.0-SNAPSHOT"]]}}
  :ring {:handler hoard.core/app}
  :checksum :pass
  :main hoard.host)
