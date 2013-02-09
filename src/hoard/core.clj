(ns hoard.core
  (:use compojure.core cheshire.core cheshire.generate)
  (:require [compojure.handler :as handler] [somnium.congomongo :as m]))


(add-encoder org.bson.types.ObjectId 
  (fn [value output]
    (.writeString output (.toString value))))

(def conn
  (m/make-connection "hoard"
    :host "127.0.0.1"
    :port 27017))

(defn generate-resource-index-entry [resource collection-name]
  {:links [{:href (str "http://localhost:3000/" collection-name "/" (:_id resource))}]}) 

(defn generate-resource-index-body [collection-name resources]
  (let [entries (map #(generate-resource-index-entry % collection-name) resources)]
    (generate-string {(symbol collection-name) entries})))

(defn get-resource-index [collection-name]
  (m/with-mongo conn
    {:body (generate-resource-index-body collection-name (m/fetch collection-name))}))

(defn create-resource [collection-name entity]
  (m/with-mongo conn
    (let [id (:_id (m/insert! collection-name (parse-stream (java.io.BufferedReader. (java.io.InputStreamReader. entity "UTF-8")))))]
      {:headers {"location" (str "http://localhost:3000/" collection-name "/" id)} :status 201})))

(defn delete-resource-index [collection-name]
  (m/with-mongo conn
    (m/drop-coll! collection-name))
    {:status 200})

(defroutes app-routes 
  (GET "/:collection" [collection] (get-resource-index collection))
  (DELETE "/:collection" [collection] (delete-resource-index collection))
  (POST "/:collection" {{collection :collection} :params body :body} (create-resource collection body)))

(def app (handler/site app-routes))
