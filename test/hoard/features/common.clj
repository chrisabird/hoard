(ns hoard.features.common
  (:require [clj-http.client :as client])
  (:use midje.sweet hoard.core ring.adapter.jetty cheshire.core))

(defmacro with-server [& body]
  `(let [server# (run-jetty app {:port 3000 :join? false})]
    (try ~@body (finally (.stop server#)))))


(def apiBaseUrl "http://localhost:3000")

(defn client-creates-resource [uri entity] 
  (client/post uri {:body (generate-string entity) :throw-exceptions false}))

(defn client-lists-resource-index [uri]
   (client/get uri {:as :json}))

(defn client-deletes-resource [uri]
  (client/delete uri {:throw-exceptions false}))

(defn client-queries-resource-index-with-id [uri id]
  (client/get (str uri "?id=" id) {:follow-redirects false}))

(defn client-gets-subordinate-resource [uri]
  (client/get uri {:as :json :throw-exceptions false}))

(defn client-heads-resource-index [uri]
  (client/head uri))

(defn client-puts-to-resource [uri entity]
  (client/put uri {:body (generate-string entity) :throw-exceptions false}))
